package fabric.lang.arrays;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Arrays;

import fabric.client.Core;
import fabric.client.TransactionManager;
import fabric.common.Policy;
import fabric.core.SerializedObject.ObjectInput;
import fabric.lang.Object;

public interface shortArray extends Object {
  int get$length();

  short set(int i, short value);

  short get(int i);

  public static class $Impl extends Object.$Impl implements
      shortArray {
    private short[] value;

    /**
     * Creates a new short array at the given Core with the given length.
     * 
     * @param core
     *          The core on which to allocate the array.
     * @param length
     *          The length of the array.
     */
    public $Impl(Core core, int length) {
      super(core);
      value = new short[length];
    }

    /**
     * Creates a new short array at the given Core using the given backing
     * array.
     * 
     * @param core
     *          The core on which to allocate the array.
     * @param value
     *          The backing array to use.
     */
    public $Impl(Core core, short[] value) {
      super(core);
      this.value = value;
    }

    /**
     * Used for deserializing.
     */
    public $Impl(Core core, long onum, int version, Policy policy,
        ObjectInput in) throws IOException, ClassNotFoundException {
      super(core, onum, version, policy, in);
      value = new short[in.readInt()];
      for (int i = 0; i < value.length; i++)
        value[i] = in.readShort();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#getLength()
     */
    public int get$length() {
      TransactionManager.INSTANCE.registerRead(this);
      return value.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#get(int)
     */
    @SuppressWarnings("unchecked")
    public short get(int i) {
      TransactionManager.INSTANCE.registerRead(this);
      return this.value[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#set(int, short)
     */
    public short set(int i, short value) {
      boolean transactionCreated = TransactionManager.INSTANCE.registerWrite(this);
      short result = this.value[i] = value;
      if (transactionCreated) TransactionManager.INSTANCE.commitTransaction();
      return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.Object.$Impl#$copyStateFrom(fabric.lang.Object.$Impl)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void $copyStateFrom(Object.$Impl other) {
      super.$copyStateFrom(other);
      shortArray.$Impl src = (shortArray.$Impl) other;
      value = Arrays.copyOf(src.value, src.value.length);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.Object.$Impl#$makeProxy()
     */
    @Override
    protected shortArray.$Proxy $makeProxy() {
      return new shortArray.$Proxy(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.Object.$Impl#$serialize(java.io.ObjectOutput)
     */
    @Override
    public void $serialize(ObjectOutput out) throws IOException {
      super.$serialize(out);
      out.writeInt(value.length);
      for (int i = 0; i < value.length; i++)
        out.writeShort(value[i]);
    }
  }

  public static class $Proxy extends Object.$Proxy implements
      shortArray {

    public $Proxy(Core core, long onum) {
      super(core, onum);
    }

    public $Proxy(shortArray.$Impl impl) {
      super(impl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#getLength()
     */
    public int get$length() {
      return ((shortArray) fetch()).get$length();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#get(int)
     */
    public short get(int i) {
      return ((shortArray) fetch()).get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.shortArray#set(int, short)
     */
    public short set(int i, short value) {
      return ((shortArray) fetch()).set(i, value);
    }
  }
}
