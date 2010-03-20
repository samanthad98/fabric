package fabric.lang.arrays.internal;

import fabric.worker.Core;
import fabric.lang.arrays.*;
import jif.lang.Label;
import fabric.lang.Object;

/**
 * A utility class for converting Java arrays into Fabric arrays.
 */
public class Compat {
  public static booleanArray convert(Core core, Label label, boolean[] array) {
    booleanArray result = new booleanArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (booleanArray) result.$getProxy();
  }

  public static byteArray convert(Core core, Label label, byte[] array) {
    byteArray result = new byteArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (byteArray) result.$getProxy();
  }

  public static charArray convert(Core core, Label label, char[] array) {
    charArray result = new charArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (charArray) result.$getProxy();
  }

  public static doubleArray convert(Core core, Label label, double[] array) {
    doubleArray result = new doubleArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (doubleArray) result.$getProxy();
  }

  public static floatArray convert(Core core, Label label, float[] array) {
    floatArray result = new floatArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (floatArray) result.$getProxy();
  }

  public static intArray convert(Core core, Label label, int[] array) {
    intArray result = new intArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (intArray) result.$getProxy();
  }

  public static longArray convert(Core core, Label label, long[] array) {
    longArray result = new longArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (longArray) result.$getProxy();
  }

  public static shortArray convert(Core core, Label label, short[] array) {
    shortArray result = new shortArray._Impl(core, label, array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (shortArray) result.$getProxy();
  }

  public static ObjectArray convert(Core core, Label label, Object[] array) {
    ObjectArray result =
        new ObjectArray._Impl(core, label, array.getClass().getComponentType(),
            array.length);
    for (int i = 0; i < array.length; i++)
      result.set(i, array[i]);
    return (ObjectArray) result.$getProxy();
  }
}
