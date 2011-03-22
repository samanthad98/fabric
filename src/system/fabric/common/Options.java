package fabric.common;

import java.io.PrintStream;
import java.util.*;

import fabric.common.Options.Flag.Handler;
import fabric.common.Options.Flag.Kind;
import fabric.common.exceptions.InternalError;
import fabric.common.exceptions.TerminationException;
import fabric.common.exceptions.UsageError;

public abstract class Options {
  private final SortedSet<Flag> flags;

  /**
   * Whether to turn off SSL encryption for debugging purposes.
   */
  public static boolean DEBUG_NO_SSL = false;

  public static class Flag implements Comparable<Flag> {
    protected final Kind kind;
    protected final Set<String> ids;
    protected final String params;
    protected final String usage;
    protected final Handler handler;

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String id, String params, String usage, Handler handler) {
      this(id, params, usage, null, handler);
    }

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value for the flag parameter, to be printed out with
     *          help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String id, String params, String usage, int defaultValue,
        Handler handler) {
      this(id, params, usage, new Integer(defaultValue).toString(), handler);
    }

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value(s) for the flag parameter(s), to be printed
     *          out with help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String id, String params, String usage, String defaultValue,
        Handler handler) {
      this(new String[] { id }, params, usage, defaultValue, handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String[] ids, String params, String usage, Handler handler) {
      this(ids, params, usage, null, handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value for the flag parameter, to be printed out with
     *          help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String[] ids, String params, String usage, int defaultValue,
        Handler handler) {
      this(ids, params, usage, new Integer(defaultValue).toString(), handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value(s) for the flag parameter(s), to be printed
     *          out with help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(String[] ids, String params, String usage, String defaultValue,
        Handler handler) {
      this(Kind.MAIN, ids, params, usage, defaultValue, handler);
    }

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String id, String params, String usage,
        Handler handler) {
      this(kind, id, params, usage, null, handler);
    }

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value for the flag parameter, to be printed out with
     *          help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String id, String params, String usage,
        int defaultValue, Handler handler) {
      this(kind, id, params, usage, new Integer(defaultValue).toString(),
          handler);
    }

    /**
     * @param id
     *          The flag ID. e.g., "--name", "-n", or "-name".
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value(s) for the flag parameter(s), to be printed
     *          out with help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String id, String params, String usage,
        String defaultValue, Handler handler) {
      this(kind, new String[] { id }, params, usage, defaultValue, handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String[] ids, String params, String usage,
        Handler handler) {
      this(kind, ids, params, usage, null, handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value for the flag parameter, to be printed out with
     *          help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String[] ids, String params, String usage,
        int defaultValue, Handler handler) {
      this(kind, ids, params, usage, new Integer(defaultValue).toString(),
          handler);
    }

    /**
     * @param ids
     *          Flag IDs. e.g., { "--name", "-n", "-name"}. The first one
     *          specified will be the one printed out with help info.
     * @param params
     *          A string representing parameters for the flag, to be printed out
     *          with help info. e.g., "&lt;hostname&gt;"
     * @param usage
     *          Usage information, to be printed out with help info. e.g.,
     *          "The name of the node."
     * @param defaultValue
     *          The default value(s) for the flag parameter(s), to be printed
     *          out with help info.
     * @param handler
     *          Code for handling the flag.
     */
    public Flag(Kind kind, String[] ids, String params, String usage,
        String defaultValue, Handler handler) {
      this.kind = kind;

      this.ids = new LinkedHashSet<String>(ids.length);
      for (String flag : ids)
        this.ids.add(flag);

      this.params = params;

      if (defaultValue != null) usage += " (default: " + defaultValue + ")";
      this.usage = usage;
      this.handler = handler;
    }

    public static enum Kind {
      MAIN, DEBUG, VERSION, HELP, SECRET, SECRET_HELP
    }

    public static interface Handler {
      /**
       * Handles a usage flag.
       * 
       * @param args
       *          Arguments from the command line.
       * @param index
       *          The index of the argument following the usage flag.
       * @return The next index to be processed. e.g., if calling this method
       *         processes two arguments, then the return value should be
       *         index+2.
       * @throws UsageError
       *           If an error occurs while handling the usage flag.
       */
      int handle(String[] args, int index) throws UsageError;
    }

    public int compareTo(Flag other) {
      if (other == null) return 1;
      if (kind != other.kind) return kind.compareTo(other.kind);

      String firstFlag = ids.iterator().next();
      String otherFirstFlag = other.ids.iterator().next();
      return firstFlag.compareTo(otherFirstFlag);
    }

    /**
     * The maximum width of a line when printing usage information.
     */
    private static final int USAGE_SCREEN_WIDTH = 76;

    /**
     * The number of spaces from the left that the description for flags will be
     * displayed.
     */
    private static final int USAGE_FLAG_WIDTH = 27;

    /**
     * Outputs this flag and a description of its usage in a nice format.
     * 
     * @param out
     *          output PrintStream
     */
    public void printUsage(PrintStream out) {
      String flagID = ids.iterator().next();
      if (params != null && !params.equals("")) flagID += " " + params;

      out.print("  ");
      out.print(flagID);

      // cur is where the cursor is on the screen.
      int cur = flagID.length() + 2;

      if (cur < USAGE_FLAG_WIDTH) {
        printSpaces(out, USAGE_FLAG_WIDTH - cur);
      } else {
        // The flag is long. Get a new line before printing the description.
        out.println();
        printSpaces(out, USAGE_FLAG_WIDTH);
      }

      cur = USAGE_FLAG_WIDTH;

      // Break up the description.
      StringTokenizer st = new StringTokenizer(usage);
      while (st.hasMoreTokens()) {
        String s = st.nextToken();
        if (cur + s.length() > USAGE_SCREEN_WIDTH) {
          out.println();
          printSpaces(out, USAGE_FLAG_WIDTH);
          cur = USAGE_FLAG_WIDTH;
        }

        out.print(s);
        cur += s.length();
        if (st.hasMoreTokens()) {
          if (cur + 1 > USAGE_SCREEN_WIDTH) {
            out.println();
            printSpaces(out, USAGE_FLAG_WIDTH);
            cur = USAGE_FLAG_WIDTH;
          } else {
            out.print(" ");
            cur++;
          }
        }
      }

      out.println();
    }

    private void printSpaces(PrintStream out, int n) {
      while (n-- > 0)
        out.print(' ');
    }
  }

  protected Options() {
    this.flags = new TreeSet<Flag>();

    // By default, add help and version flags.
    flags.add(new Flag(Kind.HELP,
        new String[] { "--help", "-h", "-help", "-?" }, null,
        "print this message", new Handler() {
          public int handle(String[] args, int index) throws UsageError {
            throw new UsageError("", 0);
          }
        }));

    flags.add(new Flag(Kind.VERSION, new String[] { "--version", "-v",
        "-version" }, null, "print version info", new Handler() {
      public int handle(String[] args, int index) {
        throw new TerminationException(0);
      }
    }));

    flags.add(new Flag(Kind.SECRET_HELP, "--secret-menu", null,
        "show the secret menu", new Handler() {
          public int handle(String[] args, int index) throws UsageError {
            throw new UsageError("", 0, true);
          }
        }));

    populateFlags(flags);
    checkFlagConsistency();
    setDefaultValues();
  }

  protected Options(String[] args) throws UsageError {
    this();
    parseCommandLine(args);
    validateOptions();
  }

  private void checkFlagConsistency() {
    Set<String> flagIDs = new HashSet<String>();
    for (Flag flag : flags) {
      for (String id : flag.ids) {
        if (flagIDs.contains(id)) {
          throw new InternalError("Duplicate command-line option flag: " + id);
        }

        flagIDs.add(id);
      }
    }
  }

  protected abstract void populateFlags(Set<Flag> flags);

  protected abstract void setDefaultValues();

  protected abstract void validateOptions() throws UsageError;

  /**
   * Handles an argument that doesn't match any known flag.
   * 
   * @param args
   *          Arguments from the command line.
   * @param index
   *          The index of the argument following the usage flag.
   * @return The next index to be processed. e.g., if calling this method
   *         processes two arguments, then the return value should be index+2.
   * @throws UsageError
   *           If an error occurs while handling the usage flag.
   */
  protected int defaultHandler(String[] args, int index) throws UsageError {
    throw new UsageError("Illegal option: " + args[index]);
  }

  public final void parseCommandLine(String args[]) throws UsageError {
    L: for (int i = 0; i < args.length;) {
      try {
        // Find a flag whose id matches args[i] and let it process the
        // arguments.
        for (Flag flag : flags) {
          if (flag.ids.contains(args[i])) {
            i = flag.handler.handle(args, i + 1);
            continue L;
          }
        }

        // No flag found. Give the rest to the default handler.
        i = defaultHandler(args, i);
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new UsageError("Missing argument");
      }
    }
  }

  public void usage(PrintStream out, boolean showSecretMenu) {
    usageHeader(out);

    boolean firstSecretItem = true;
    for (Flag flag : flags) {
      boolean isSecret = flag.kind.compareTo(Kind.SECRET) >= 0;
      if (showSecretMenu && isSecret && firstSecretItem) {
        out.println();
        out.println("Secret menu:");
        firstSecretItem = false;
      }
      
      if (showSecretMenu || !isSecret) flag.printUsage(out);
    }
  }

  /**
   * Prints a header for the usage message to the given output.
   */
  protected abstract void usageHeader(PrintStream out);
}
