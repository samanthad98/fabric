package fabric.store.db;

import static com.sleepycat.je.OperationStatus.SUCCESS;
import static fabric.common.Logging.STORE_DB_LOGGER;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import com.sleepycat.bind.tuple.BooleanBinding;
import com.sleepycat.bind.tuple.LongBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockConflictException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import fabric.common.FastSerializable;
import fabric.common.Logging;
import fabric.common.ONumConstants;
import fabric.common.Resources;
import fabric.common.SerializedObject;
import fabric.common.Surrogate;
import fabric.common.Threading;
import fabric.common.VersionWarranty;
import fabric.common.exceptions.AccessException;
import fabric.common.exceptions.InternalError;
import fabric.common.net.RemoteIdentity;
import fabric.common.util.Cache;
import fabric.common.util.LongKeyCache;
import fabric.common.util.MutableInteger;
import fabric.common.util.MutableLong;
import fabric.common.util.OidKeyHashMap;
import fabric.common.util.Pair;
import fabric.lang.FClass;
import fabric.lang.security.Principal;
import fabric.store.SubscriptionManager;
import fabric.store.TransactionManager;
import fabric.worker.Store;
import fabric.worker.Worker;
import fabric.worker.remote.RemoteWorker;

/**
 * An ObjectDB backed by a Berkeley Database.
 */
public class BdbDB extends ObjectDB {

  private Environment env;
  private Database meta;

  /**
   * Database containing the actual serialized Fabric objects.
   */
  private Database db;

  /**
   * Database containing data for prepared but uncommitted transactions.
   */
  private Database prepared;

  /**
   * Database containing objects modified by prepared transactions.
   */
  private Database preparedWrites;

  /**
   * Database containing the commit times for confirmed transactions
   * (uncommitted transactions for which COMMIT messages have been received).
   */
  private Database commitTimes;

  private final DatabaseEntry initializationStatus;
  private final DatabaseEntry onumCounter;

  /**
   * The BDB key under which the longest warranty is stored.
   */
  private final DatabaseEntry longestWarrantyEntry;

  private final MutableLong nextOnum;

  /**
   * To prevent touching BDB on every onum reservation request, we keep a bunch
   * of onums in reserve. If nextOnum > lastReservedOnum, it's time to touch BDB
   * again to reserve more onums.
   */
  private final MutableLong lastReservedOnum;

  /**
   * Cache: maps onums to object versions of objects that are currently stored
   * in BDB. Because Integers are interned, it doesn't make much sense to have
   * SoftReferences to Integers, so we use MutableIntegers instead.
   */
  private final LongKeyCache<MutableInteger> cachedVersions;

  /**
   * Cache: objects.
   */
  private final LongKeyCache<SerializedObject> cachedObjects;

  /**
   * Cache: maps BDB keys to prepared-transaction records.
   */
  private final Cache<ByteArray, PendingTransaction> preparedTransactions;

  /**
   * Creates a new BdbStore for the store specified. A new database will be
   * created if it does not exist.
   * 
   * @param name
   *          name of store to create store for.
   */
  public BdbDB(String name, PrivateKey privateKey) {
    super(name, privateKey);

    String path = Resources.relpathRewrite("var", "bdb", name);
    new File(path).mkdirs(); // create path if it does not exist

    try {
      EnvironmentConfig conf = new EnvironmentConfig();
      conf.setAllowCreate(true);
      conf.setTransactional(true);
      conf.setCachePercent(40);
      conf.setSharedCache(true);
      env = new Environment(new File(path), conf);

      STORE_DB_LOGGER.info("Bdb env opened");

      DatabaseConfig dbconf = new DatabaseConfig();
      dbconf.setAllowCreate(true);
      dbconf.setTransactional(true);
      db = env.openDatabase(null, "store", dbconf);
      prepared = env.openDatabase(null, "prepared", dbconf);
      commitTimes = env.openDatabase(null, "commitTimes", dbconf);
      meta = env.openDatabase(null, "meta", dbconf);

      dbconf.setSortedDuplicates(true);
      preparedWrites = env.openDatabase(null, "preparedWrites", dbconf);

      STORE_DB_LOGGER.info("Bdb databases opened");
    } catch (DatabaseException e) {
      STORE_DB_LOGGER.log(Level.SEVERE, "Bdb error in <init>: ", e);
      throw new InternalError(e);
    }

    try {
      initializationStatus =
          new DatabaseEntry("initialization_status".getBytes("UTF-8"));
      onumCounter = new DatabaseEntry("onum_counter".getBytes("UTF-8"));
      longestWarrantyEntry =
          new DatabaseEntry("longest_warranty".getBytes("UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new InternalError(e);
    }

    this.nextOnum = new MutableLong(-1);
    this.lastReservedOnum = new MutableLong(-2);
    this.cachedVersions = new LongKeyCache<MutableInteger>();
    this.cachedObjects = new LongKeyCache<SerializedObject>();
    this.preparedTransactions = new Cache<ByteArray, PendingTransaction>();
  }

  @Override
  protected void saveLongestWarranty() {
    STORE_DB_LOGGER.fine("Bdb saving longest warranty");

    runInBdbTransaction(new Code<Void, RuntimeException>() {
      @Override
      public Void run(Transaction txn) throws RuntimeException {
        DatabaseEntry data = new DatabaseEntry();
        LongBinding.longToEntry(longestWarranty[0].expiry(), data);

        meta.put(txn, longestWarrantyEntry, data);
        return null;
      }
    });
  }

  @Override
  public void finishPrepareWrites(long tid, Principal worker) {
    // Copy the transaction data into BDB.
    OidKeyHashMap<PendingTransaction> submap = pendingByTid.get(tid);
    final PendingTransaction pending;
    synchronized (submap) {
      pending = submap.remove(worker);
      if (submap.isEmpty()) pendingByTid.remove(tid, submap);
    }

    final DatabaseEntry key = new DatabaseEntry(toBytes(tid, worker));

    runInBdbTransaction(new Code<Void, RuntimeException>() {
      @Override
      public Void run(Transaction txn) throws RuntimeException {
        DatabaseEntry data = new DatabaseEntry(toBytesNoModData(pending));
        prepared.put(txn, key, data);

        ModDataSerializer serializer = new ModDataSerializer();
        for (Pair<SerializedObject, UpdateType> obj : pending.modData) {
          data.setData(serializer.toBytes(obj));
          preparedWrites.put(txn, key, data);
        }

        return null;
      }
    });

    preparedTransactions.put(new ByteArray(key.getData()), pending);
    STORE_DB_LOGGER.log(Level.FINER, "Bdb prepare success tid {0}", tid);
  }

  @Override
  public void scheduleCommit(long tid, long commitTime,
      RemoteIdentity<RemoteWorker> workerIdentity, SubscriptionManager sm) {
    scheduleCommit(tid, commitTime, workerIdentity, sm, true);
  }

  /**
   * @param logCommitTime
   *          whether to log the commit time to stable storage for recovery
   *          purposes.
   */
  private void scheduleCommit(final long tid, final long commitTime,
      final RemoteIdentity<RemoteWorker> workerIdentity,
      final SubscriptionManager sm, boolean logCommitTime) {
    if (STORE_DB_LOGGER.isLoggable(Level.FINER)) {
      long commitDelay = commitTime - System.currentTimeMillis();
      Logging.log(STORE_DB_LOGGER, Level.FINER,
          "Scheduling Bdb commit for tid {0} to run at {1} (in {2} ms)", tid,
          new Date(commitTime), commitDelay);
    }

    // Record the commit time in BDB.
    final DatabaseEntry tidBdbKey =
        new DatabaseEntry(toBytes(tid, workerIdentity.principal));
    if (logCommitTime) {
      runInBdbTransaction(new Code<Void, RuntimeException>() {
        @Override
        public Void run(Transaction txn) throws RuntimeException {
          DatabaseEntry data = new DatabaseEntry();
          LongBinding.longToEntry(commitTime, data);

          commitTimes.put(txn, tidBdbKey, data);
          return null;
        }
      });
    }

    Threading.scheduleAt(commitTime, new Runnable() {
      @Override
      public void run() {
        STORE_DB_LOGGER.log(Level.FINER, "Bdb commit begin tid {0}", tid);

        PendingTransaction pending =
            runInBdbTransaction(new Code<PendingTransaction, RuntimeException>() {
              @Override
              public PendingTransaction run(Transaction txn)
                  throws RuntimeException {
                // Remove the commit time from BDB.
                commitTimes.delete(txn, tidBdbKey);

                // Obtain the transaction record.
                PendingTransaction pending =
                    remove(workerIdentity.principal, txn, tid);

                if (pending != null) {
                  FSSerializer<SerializedObject> serializer =
                      new FSSerializer<SerializedObject>();
                  for (Pair<SerializedObject, UpdateType> update : pending.modData) {
                    SerializedObject o = update.first;
                    long onum = o.getOnum();
                    STORE_DB_LOGGER.log(Level.FINEST,
                        "Bdb committing onum {0}", onum);

                    DatabaseEntry onumData = new DatabaseEntry();
                    LongBinding.longToEntry(onum, onumData);

                    DatabaseEntry objData =
                        new DatabaseEntry(serializer.toBytes(o));

                    db.put(txn, onumData, objData);
                  }

                  return pending;
                } else {
                  STORE_DB_LOGGER.log(Level.WARNING,
                      "Bdb commit not found tid {0}", tid);
                  throw new InternalError("Unknown transaction id " + tid);
                }
              }
            });

        // Fix up caches.
        for (Pair<SerializedObject, UpdateType> update : pending.modData) {
          SerializedObject o = update.first;
          long onum = o.getOnum();

          if (update.second == UpdateType.WRITE) {
            // Remove any cached globs containing the old version of this object.
            notifyCommittedUpdate(sm, onum, workerIdentity.node);
          }

          // Update caches.
          cacheVersionNumber(onum, o.getVersion());
          cachedObjects.put(onum, o);
        }

        STORE_DB_LOGGER.log(Level.FINER, "Bdb commit success tid {0}", tid);
      }
    });
  }

  @Override
  public void rollback(final long tid, final Principal worker) {
    STORE_DB_LOGGER.log(Level.FINER, "Bdb rollback begin tid {0}", tid);

    runInBdbTransaction(new Code<Void, RuntimeException>() {
      @Override
      public Void run(Transaction txn) throws RuntimeException {
        remove(worker, txn, tid);
        return null;
      }
    });

    STORE_DB_LOGGER.log(Level.FINER, "Bdb rollback success tid {0}", tid);
  }

  @Override
  public SerializedObject read(final long onum) {
    SerializedObject cached = cachedObjects.get(onum);
    if (cached != null) return cached;

    STORE_DB_LOGGER.log(Level.FINEST, "Bdb read onum {0}", onum);

    return runInBdbTransaction(new Code<SerializedObject, RuntimeException>() {
      @Override
      public SerializedObject run(Transaction txn) throws RuntimeException {
        DatabaseEntry key = new DatabaseEntry();
        LongBinding.longToEntry(onum, key);

        DatabaseEntry data = new DatabaseEntry();

        if (db.get(null, key, data, LockMode.DEFAULT) == SUCCESS) {
          SerializedObject result = toSerializedObject(data.getData());
          cacheVersionNumber(onum, result.getVersion());
          cachedObjects.put(onum, result);
          return result;
        }

        return null;
      }
    });
  }

  @Override
  public int getVersion(long onum) throws AccessException {
    MutableInteger ver = cachedVersions.get(onum);

    if (ver == null) return super.getVersion(onum);

    synchronized (ver) {
      return ver.value;
    }
  }

  @Override
  public boolean exists(final long onum) {
    return runInBdbTransaction(new Code<Boolean, RuntimeException>() {
      @Override
      public Boolean run(Transaction txn) throws RuntimeException {
        DatabaseEntry key = new DatabaseEntry();
        LongBinding.longToEntry(onum, key);

        DatabaseEntry data = new DatabaseEntry();
        if (db.get(null, key, data, LockMode.DEFAULT) == SUCCESS) {
          return true;
        }

        return false;
      }
    });
  }

  private final long ONUM_RESERVE_SIZE = 10240;

  @Override
  public long[] newOnums(final int num) {
    STORE_DB_LOGGER.fine("Bdb new onums begin");

    long[] onums = new long[num];
    synchronized (nextOnum) {
      synchronized (lastReservedOnum) {
        for (int i = 0; i < num; i++) {
          final int curI = i;
          if (nextOnum.value > lastReservedOnum.value) {
            // Reserve more onums from BDB.
            runInBdbTransaction(new Code<Void, RuntimeException>() {
              @Override
              public Void run(Transaction txn) throws RuntimeException {
                DatabaseEntry data = new DatabaseEntry();
                nextOnum.value = ONumConstants.FIRST_UNRESERVED;

                if (meta.get(txn, onumCounter, data, LockMode.DEFAULT) == SUCCESS) {
                  nextOnum.value = LongBinding.entryToLong(data);
                }

                lastReservedOnum.value =
                    nextOnum.value + ONUM_RESERVE_SIZE + num - curI - 1;

                LongBinding.longToEntry(lastReservedOnum.value + 1, data);
                meta.put(txn, onumCounter, data);

                return null;
              }
            });

            Logging.log(STORE_DB_LOGGER, Level.FINE,
                "Bdb reserved onums {0}--{1}", nextOnum, lastReservedOnum);
          }

          onums[i] = nextOnum.value++;
        }
      }
    }

    return onums;
  }

  /**
   * Clean up and close database.
   */
  @Override
  public void close() {
    try {
      if (db != null) db.close();
      if (prepared != null) prepared.close();
      if (preparedWrites != null) preparedWrites.close();
      if (commitTimes != null) commitTimes.close();
      if (meta != null) meta.close();
      if (env != null) env.close();
    } catch (DatabaseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isInitialized() {
    STORE_DB_LOGGER.fine("Bdb is initialized begin");

    return runInBdbTransaction(new Code<Boolean, RuntimeException>() {
      @Override
      public Boolean run(Transaction txn) throws RuntimeException {
        DatabaseEntry data = new DatabaseEntry();

        if (meta.get(txn, initializationStatus, data, LockMode.DEFAULT) == SUCCESS) {
          return BooleanBinding.entryToBoolean(data);
        }

        return false;
      }
    });
  }

  @Override
  public void setInitialized() {
    STORE_DB_LOGGER.fine("Bdb set initialized begin");

    runInBdbTransaction(new Code<Void, RuntimeException>() {
      @Override
      public Void run(Transaction txn) throws RuntimeException {
        DatabaseEntry data = new DatabaseEntry();
        BooleanBinding.booleanToEntry(true, data);
        meta.put(txn, initializationStatus, data);
        return null;
      }
    });
  }

  @Override
  protected void recoverState(TransactionManager tm) {
    STORE_DB_LOGGER.fine("Bdb recovering state");

    SortedMap<Long, List<Pair<Long, Principal>>> commitSchedule =
        runInBdbTransaction(new Code<SortedMap<Long, List<Pair<Long, Principal>>>, RuntimeException>() {
          @Override
          public SortedMap<Long, List<Pair<Long, Principal>>> run(
              Transaction txn) throws RuntimeException {
            // Scan through the database of prepares to recover writeLocks,
            // writtenOnumsByTid, and warranties.
            DatabaseEntry key = new DatabaseEntry();
            DatabaseEntry data = new DatabaseEntry();
            {
              Cursor preparedCursor = prepared.openCursor(txn, null);
              while (preparedCursor.getNext(key, data, null) != OperationStatus.NOTFOUND) {
                PendingTransaction pending =
                    toPendingTransaction(data.getData());
                long tid = pending.tid;
                Principal owner = pending.owner;
                if (commitTimes.get(txn, key, data, LockMode.DEFAULT) != SUCCESS) {
                  throw new InternalError("Cannot find commit time for tid="
                      + tid + ", owner=" + owner.$getStore() + "/"
                      + owner.$getOnum());
                }
                VersionWarranty warranty =
                    new VersionWarranty(LongBinding.entryToLong(data));

                if (STORE_DB_LOGGER.isLoggable(Level.FINER)) {
                  Logging.log(STORE_DB_LOGGER, Level.FINER,
                      "Recoving state for tid={0}, owner={1}/{2} "
                          + "(commit time={3})", tid, owner.$getStore(),
                      owner.$getOnum(), new Date(warranty.expiry()));
                }

                // Loop through the updates for the current transaction.
                for (Pair<SerializedObject, UpdateType> update : pending.modData) {
                  long onum = update.first.getOnum();

                  // Recover the transaction's write lock for the current update.
                  writeLocks.put(onum, tid);

                  if (update.second == UpdateType.WRITE) {
                    STORE_DB_LOGGER.log(Level.FINEST, "Recovered write to {0}",
                        onum);
                    addWrittenOnumByTid(tid, owner, onum);

                    // Restore the object's warranty.
                    versionWarrantyTable.put(onum, warranty);
                  } else {
                    STORE_DB_LOGGER.log(Level.FINEST,
                        "Recovered create for {0}", onum);
                  }
                }
              }
              preparedCursor.close();
            }

            // Scan through the commitTimes database and build a commit schedule.
            SortedMap<Long, List<Pair<Long, Principal>>> commitSchedule =
                new TreeMap<Long, List<Pair<Long, Principal>>>();
            {
              Cursor commitTimesCursor = commitTimes.openCursor(txn, null);
              while (commitTimesCursor.getNext(key, data, null) != OperationStatus.NOTFOUND) {
                Pair<Long, Principal> tid = toTid(key.getData());
                long commitTime = LongBinding.entryToLong(data);

                List<Pair<Long, Principal>> toCommit =
                    commitSchedule.get(commitTime);
                if (toCommit == null) {
                  toCommit = new ArrayList<Pair<Long, Principal>>(2);
                  commitSchedule.put(commitTime, toCommit);
                }

                toCommit.add(tid);
              }
              commitTimesCursor.close();
            }

            // Recover the longest warranty issued and use that as the default
            // warranty.
            if (meta.get(txn, longestWarrantyEntry, data, LockMode.DEFAULT) == SUCCESS) {
              longestWarranty[0] =
                  new VersionWarranty(LongBinding.entryToLong(data));
              versionWarrantyTable.setDefaultWarranty(longestWarranty[0]);
            }

            return commitSchedule;
          }
        });

    // Re-schedule commits.
    SubscriptionManager sm = tm.subscriptionManager();
    for (Entry<Long, List<Pair<Long, Principal>>> entry : commitSchedule
        .entrySet()) {
      long commitTime = entry.getKey();
      for (Pair<Long, Principal> tid : entry.getValue()) {
        scheduleCommit(tid.first, commitTime, new RemoteIdentity<RemoteWorker>(
            null, tid.second), sm, false);
      }
    }
  }

  /**
   * Removes a PendingTransaction from the prepare log and returns it. If no
   * transaction with the given transaction id is found, null is returned.
   * 
   * @param worker
   *          the principal under which this action is being executed.
   * @param txn
   *          the BDB Transaction instance that should be used to perform the
   *          retrieval.
   * @param tid
   *          the transaction id.
   * @return the PendingTransaction corresponding to tid
   * @throws DatabaseException
   *           if a database error occurs
   */
  private PendingTransaction remove(Principal worker, Transaction txn, long tid)
      throws DatabaseException {
    byte[] key = toBytes(tid, worker);
    DatabaseEntry bdbKey = new DatabaseEntry(key);
    DatabaseEntry data = new DatabaseEntry();

    PendingTransaction pending =
        preparedTransactions.remove(new ByteArray(key));

    if (pending == null
        && prepared.get(txn, bdbKey, data, LockMode.DEFAULT) == SUCCESS) {
      pending = toPendingTransaction(data.getData());

      Cursor cursor = preparedWrites.openCursor(txn, null);
      for (OperationStatus result = cursor.getSearchKey(bdbKey, data, null); result == SUCCESS; result =
          cursor.getNextDup(bdbKey, data, null)) {
        pending.modData.add(toModData(data.getData()));
      }
      cursor.close();
    }

    if (pending == null) return null;
    prepared.delete(txn, bdbKey);
    preparedWrites.delete(txn, bdbKey);

    unpin(pending);
    return pending;
  }

  private void cacheVersionNumber(long onum, int versionNumber) {
    MutableInteger curEntry =
        cachedVersions.putIfAbsent(onum, new MutableInteger(versionNumber));

    if (curEntry != null) {
      synchronized (curEntry) {
        curEntry.value = versionNumber;
      }
    }
  }

  private static int MAX_TX_RETRIES = 20;
  private static int MAX_TX_WAIT_AVG = 5000;
  private static final Random RAND = new Random();

  private static int randInt(int max) {
    synchronized (RAND) {
      return RAND.nextInt(max);
    }
  }

  /**
   * Executes the given code from within a BDB transaction, automatically
   * retrying as necessary with a random exponential back-off. If the given code
   * throws an exception, the transaction will be aborted.
   */
  private <T, E extends Exception> T runInBdbTransaction(Code<T, E> code)
      throws E {
    // This code is adapted from the JavaDoc for LockConflictException.
    boolean success = false;
    int backoff = 1;
    List<LockConflictException> conflicts =
        new ArrayList<LockConflictException>(MAX_TX_RETRIES);
    for (int i = 0; i < MAX_TX_RETRIES; i++) {
      int waitTime = randInt(backoff);
      if (waitTime > 0) {
        while (true) {
          try {
            Thread.sleep(waitTime);
            break;
          } catch (InterruptedException e) {
            Logging.logIgnoredInterruptedException(e);
          }
        }
      }

      if (backoff < MAX_TX_WAIT_AVG) {
        backoff *= 2;
      } else {
        backoff = 2 * MAX_TX_WAIT_AVG;
      }

      Transaction txn = null;
      try {
        txn = env.beginTransaction(null, null);
        T result = code.run(txn);
        txn.commit();
        success = true;
        return result;
      } catch (LockConflictException e) {
        conflicts.add(e);
        continue;
      } catch (DatabaseException e) {
        STORE_DB_LOGGER.log(Level.SEVERE, "Bdb error: ", e);
        throw new InternalError(e);
      } finally {
        if (!success && txn != null) txn.abort();
      }
    }

    throw new InternalError(
        "BDB transaction failed too many times. List of LockConflictExceptions "
            + "we got: " + conflicts);
  }

  private static interface Code<T, E extends Exception> {
    T run(Transaction txn) throws E;
  }

  private static byte[] toBytes(long tid, Principal worker) {
    try {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(bos);
      dos.writeLong(tid);
      if (worker != null) {
        dos.writeUTF(worker.$getStore().name());
        dos.writeLong(worker.$getOnum());
      }
      dos.flush();
      return bos.toByteArray();
    } catch (IOException e) {
      throw new InternalError(e);
    }
  }

  private static Pair<Long, Principal> toTid(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      DataInputStream dis = new DataInputStream(bis);
      long tid = dis.readLong();

      if (data.length < 10) return new Pair<Long, Principal>(tid, null);
      Store store = Worker.getWorker().getStore(dis.readUTF());
      long onum = dis.readLong();
      Principal owner = new Principal._Proxy(store, onum);
      return new Pair<Long, Principal>(tid, owner);
    } catch (IOException e) {
      throw new InternalError();
    }
  }

  private static byte[] toBytesNoModData(PendingTransaction pending) {
    return new Serializer<PendingTransaction>() {
      @Override
      public void write(PendingTransaction pending, ObjectOutputStream out)
          throws IOException {
        pending.writeNoModData(out);
      }
    }.toBytes(pending);
  }

  private static PendingTransaction toPendingTransaction(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bis);
      return new PendingTransaction(ois);
    } catch (IOException e) {
      throw new InternalError(e);
    }
  }

  private static SerializedObject toSerializedObject(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bis);
      return new SerializedObject(ois);
    } catch (IOException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Utility class for serializing objects. This avoids creating a new
   * ObjectOutputStream for each object serialized, while maintaining the
   * illusion that each object is serialized with a fresh ObjectOutputStream.
   * This illusion allows each object to be deserialized separately.
   */
  private static abstract class Serializer<T> {
    private final ByteArrayOutputStream bos;
    private final ObjectOutputStream oos;
    private static final byte[] HEADER;

    static {
      // Save the header that ObjectOutputStream writes upon its initialization.
      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.flush();
        HEADER = bos.toByteArray();
      } catch (IOException e) {
        throw new InternalError(e);
      }
    }

    public Serializer() {
      try {
        this.bos = new ByteArrayOutputStream();
        this.oos = new ObjectOutputStream(bos);
        oos.flush();
      } catch (IOException e) {
        throw new InternalError(e);
      }
    }

    public final byte[] toBytes(T obj) {
      try {
        bos.reset();
        bos.write(HEADER);
        write(obj, oos);
        oos.flush();
        return bos.toByteArray();
      } catch (IOException e) {
        throw new InternalError(e);
      }
    }

    public abstract void write(T obj, ObjectOutputStream out)
        throws IOException;
  }

  /**
   * Utility class for serializing FastSerializable objects. This avoids
   * creating a new ObjectOutputStream for each object serialized, while
   * maintaining the illusion that each object is serialized with a fresh
   * ObjectOutputStream. This allows each object to be deserialized separately.
   */
  private static class FSSerializer<FS extends FastSerializable> extends
      Serializer<FS> {
    @Override
    public void write(FS obj, ObjectOutputStream out) throws IOException {
      obj.write(out);
    }
  }

  private static class ModDataSerializer extends
      Serializer<Pair<SerializedObject, UpdateType>> {
    @Override
    public void write(Pair<SerializedObject, UpdateType> obj,
        ObjectOutputStream out) throws IOException {
      obj.first.write(out);
      out.writeBoolean(obj.second == UpdateType.CREATE);
    }
  }

  private static Pair<SerializedObject, UpdateType> toModData(byte[] data) {
    try {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream ois = new ObjectInputStream(bis);

      SerializedObject obj = new SerializedObject(ois);
      UpdateType updateType =
          ois.readBoolean() ? UpdateType.CREATE : UpdateType.WRITE;

      return new Pair<SerializedObject, UpdateType>(obj, updateType);
    } catch (IOException e) {
      throw new InternalError(e);
    }
  }

  /**
   * Dumps the contents of a BDB object database to stdout.
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Usage: fabric.store.db.BdbDB STORE_NAME");
      System.err.println();
      System.err.println("  Dumps a BDB object database in CSV format.");
      return;
    }

    BdbDB db = new BdbDB(args[0], null);
    Cursor cursor = db.db.openCursor(null, null);
    DatabaseEntry key = new DatabaseEntry();
    DatabaseEntry value = new DatabaseEntry();

    System.out.println("onum,class name,version number,update label onum,"
        + "access label onum");
    while (cursor.getNext(key, value, null) == OperationStatus.SUCCESS) {
      SerializedObject obj = toSerializedObject(value.getData());
      long onum = obj.getOnum();
      String className = obj.getClassName();
      int version = obj.getVersion();
      long updateLabelOnum = obj.getUpdateLabelOnum();
      long accessPolicyOnum = obj.getAccessPolicyOnum();
      String extraInfo = "";
      try {
        // Get extra information on surrogates and FClasses.
        // This code depends on the serialization format of those classes, and
        // is therefore rather fragile.
        if (Surrogate.class.getName().equals(className)) {
          ObjectInputStream ois =
              new ObjectInputStream(obj.getSerializedDataStream());
          extraInfo = ",ref=" + ois.readUTF() + "/" + ois.readLong();
        } else if (FClass.class.getName().equals(className)) {
          ObjectInputStream ois =
              new ObjectInputStream(obj.getSerializedDataStream());
          extraInfo = ",name=" + ois.readObject();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
      System.out.println(onum + "," + className + "," + version + ","
          + updateLabelOnum + "," + accessPolicyOnum + extraInfo);
    }
  }

  private static class ByteArray {
    private final byte[] data;

    public ByteArray(byte[] data) {
      this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ByteArray)) return false;

      byte[] data = ((ByteArray) obj).data;
      return Arrays.equals(data, ((ByteArray) obj).data);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(data);
    }

  }
}
