/**
 * Copyright (C) 2010 Fabric project group, Cornell University
 *
 * This file is part of Fabric.
 *
 * Fabric is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 2 of the License, or (at your option) any later
 * version.
 * 
 * Fabric is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 */
package fabric.lang.arrays;

import fabric.worker.Store;
import fabric.lang.Object;
import fabric.lang.security.Label;

public interface byteArray extends Object {
  byte get(int i);

  byte set(int i, byte value);
  
  int  get$length();

  public static class _Impl extends Object._Impl implements byteArray {
    public _Impl(Store store, Label label, int length) {
      super(store, label);
    }

    public native byte get(int i);

    public native byte set(int i, byte value);
    
    public native int  get$length();
  }
}
