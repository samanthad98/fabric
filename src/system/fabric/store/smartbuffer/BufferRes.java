package fabric.store.smartbuffer;

import fabric.common.SerializedObject;
import fabric.common.util.OidKeyHashMap;

public class BufferRes {
    public final boolean result;
    public final OidKeyHashMap<SerializedObject> versionConflicts;

    public BufferRes() {
        this.result = true;
        this.versionConflicts = new OidKeyHashMap<>();
    }

    public BufferRes(OidKeyHashMap<SerializedObject> versionConflicts) {
        this.result = false;
        this.versionConflicts = versionConflicts;
    }

}
