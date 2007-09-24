package fabric.lang.arrays;

import fabric.client.Core;

public interface longArray {
  int getLength();

  long set(int i, long value);

  long get(int i);

  public static class $Impl extends fabric.lang.Object.$Impl implements
      longArray {
    private long[] value;

    /**
     * Creates a new long array at the given Core with the given length.
     * 
     * @param core
     *          The core on which to allocate the array.
     * @param length
     *          The length of the array.
     */
    public $Impl(Core core, int length) {
      super(core);
      value = new long[length];
    }

    /**
     * Creates a new long array at the given Core using the given backing
     * array.
     * 
     * @param core
     *          The core on which to allocate the array.
     * @param value
     *          The backing array to use.
     */
    public $Impl(Core core, long[] value) {
      super(core);
      this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#getLength()
     */
    public int getLength() {
      // TODO: mark as read
      return value.length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#get(int)
     */
    @SuppressWarnings("unchecked")
    public long get(int i) {
      // TODO: mark as read
      return this.value[i];
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#set(int, long)
     */
    public long set(int i, long value) {
      // TODO: mark as written
      return this.value[i] = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.Object.$Impl#$getProxy()
     */
    @Override
    public longArray.$Proxy $getProxy() {
      return new longArray.$Proxy(this);
    }

  }

  public static class $Proxy extends fabric.lang.Object.$Proxy implements
      longArray {

    public $Proxy(longArray.$Impl impl) {
      super(impl);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#getLength()
     */
    public int getLength() {
      return ((longArray) fetch()).getLength();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#get(int)
     */
    public long get(int i) {
      return ((longArray) fetch()).get(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fabric.lang.arrays.longArray#set(int, long)
     */
    public long set(int i, long value) {
      return ((longArray) fetch()).set(i, value);
    }
  }
}
