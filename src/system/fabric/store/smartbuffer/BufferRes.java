package fabric.store.smartbuffer;

import fabric.common.SerializedObject;
import fabric.common.util.OidKeyHashMap;

/**
 * Result of SmartBuffer.
 */
public class BufferRes {
    /**
     * True if the txn is ready to be prepared again. False if the txn is ejected because of version conflict.
     */
    public final boolean result;

    /**
     * Version conflicts that cause the ejection of the txn. Non-empty iff result is false.
     */
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
