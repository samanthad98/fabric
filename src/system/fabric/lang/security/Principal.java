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
package fabric.lang.security;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.List;

import fabric.common.Crypto;
import fabric.common.RefTypeEnum;
import fabric.common.util.Pair;
import fabric.lang.security.PrincipalUtil.TopPrincipal;
import fabric.net.UnreachableNodeException;
import fabric.worker.Store;
import fabric.worker.Worker;
import fabric.worker.transaction.TransactionManager;

/**
 * This is implemented in Java so that the constructor can provide default
 * labels so that a Principal p can be labelled with {p→_; p←p}.
 */
public interface Principal extends fabric.lang.Object {

  String name();

  boolean delegatesTo(final Principal p);

  boolean equals(final Principal p);

  boolean isAuthorized(final java.lang.Object authPrf, final Closure closure,
      final Label lb, final boolean executeNow);

  ActsForProof findProofUpto(final Store store, final Principal p,
      final java.lang.Object searchState);

  ActsForProof findProofDownto(final Store store, final Principal q,
      final java.lang.Object searchState);

  PublicKey getPublicKey();

  PrivateKeyObject getPrivateKeyObject();

  public static class _Proxy extends fabric.lang.Object._Proxy implements
      Principal {

    public _Proxy(Principal._Impl impl) {
      super(impl);
    }

    public _Proxy(fabric.worker.Store store, long onum) {
      super(store, onum);
    }

    public String name() {
      return ((Principal) fetch()).name();
    }

    public boolean delegatesTo(Principal p) {
      return ((Principal) fetch()).delegatesTo(p);
    }

    public boolean equals(Principal p) {
      return ((Principal) fetch()).equals(p);
    }

    public boolean isAuthorized(java.lang.Object authPrf, Closure closure,
        Label lb, boolean executeNow) {
      return ((Principal) fetch()).isAuthorized(authPrf, closure, lb,
          executeNow);
    }

    public ActsForProof findProofUpto(Store store, Principal p,
        java.lang.Object searchState) {
      return ((Principal) fetch()).findProofUpto(store, p, searchState);
    }

    public ActsForProof findProofDownto(Store store, Principal q,
        java.lang.Object searchState) {
      return ((Principal) fetch()).findProofDownto(store, q, searchState);
    }

    public PublicKey getPublicKey() {
      return ((Principal) fetch()).getPublicKey();
    }

    public PrivateKeyObject getPrivateKeyObject() {
      return ((Principal) fetch()).getPrivateKeyObject();
    }
  }

  abstract public static class _Impl extends fabric.lang.Object._Impl implements
      Principal {

    private PublicKey publicKey;
    private PrivateKeyObject privateKeyObject;

    public _Impl(Store store, Label label) {
      // If the given label is null, temporarily create the object with an
      // overly restrictive label.
      super(store, label == null ? Worker.getWorker().getLocalStore()
          .getPublicReadonlyLabel() : label);

      Principal._Proxy thisProxy = (Principal._Proxy) this.$getProxy();
      IntegPolicy integ =
          LabelUtil._Impl.writerPolicy(store, thisProxy, thisProxy);

      // Always ensure that the principal can modify its own object.
      // Because of bootstrapping issues, we special case the top principal.
      if (!(this instanceof TopPrincipal)) {
        // {this <- this}
        ConfPolicy bottomConf =
            Worker.getWorker().getLocalStore().getBottomConfidPolicy();
        Label thisIntegLabel =
            LabelUtil._Impl.toLabel(store, bottomConf, integ);

        if (label == null) {
          // Replace the temporary label with {this <- this}.
          this.$label = thisIntegLabel;
        } else {
          // Join the given label with {this <- this}.
          this.$label =
              LabelUtil._Impl.join(this.$label.$getStore(), this.$label,
                  thisIntegLabel);
        }
      }

      // Generate a new key pair for this principal.
      KeyPair keyPair = Crypto.genKeyPair();
      this.publicKey = keyPair.getPublic();

      // Create the label {this->this; this<-this} for the private key object.
      ConfPolicy conf =
          LabelUtil._Impl.readerPolicy(store, thisProxy, thisProxy);
      Label privateLabel = LabelUtil._Impl.toLabel(store, conf, integ);
      this.privateKeyObject =
          new PrivateKeyObject._Impl(store, privateLabel, keyPair.getPrivate());
    }

    abstract public String name();

    abstract public boolean delegatesTo(final Principal p);

    abstract public boolean equals(final Principal p);

    abstract public boolean isAuthorized(final java.lang.Object authPrf,
        final Closure closure, final Label lb, final boolean executeNow);

    abstract public ActsForProof findProofUpto(final Store store,
        final Principal p, final java.lang.Object searchState);

    abstract public ActsForProof findProofDownto(final Store store,
        final Principal q, final java.lang.Object searchState);

    @Override
    protected fabric.lang.Object._Proxy $makeProxy() {
      return new Principal._Proxy(this);
    }

    @Override
    public void $serialize(ObjectOutput out, List<RefTypeEnum> refTypes,
        List<Long> intraStoreRefs, List<Pair<String, Long>> interStoreRefs)
        throws IOException {
      super.$serialize(out, refTypes, intraStoreRefs, interStoreRefs);
    }

    public _Impl(Store store, long onum, int version, long expiry, long label,
        ObjectInput in, Iterator<RefTypeEnum> refTypes,
        Iterator<Long> intraStoreRefs) throws java.io.IOException,
        ClassNotFoundException {
      super(store, onum, version, expiry, label, in, refTypes, intraStoreRefs);
    }

    public final PublicKey getPublicKey() {
      TransactionManager.getInstance().registerRead(this);
      return publicKey;
    }

    public final PrivateKeyObject getPrivateKeyObject() {
      TransactionManager.getInstance().registerRead(this);
      return privateKeyObject;
    }
  }

  interface _Static extends fabric.lang.Object, Cloneable {
    final class _Proxy extends fabric.lang.Object._Proxy implements
        Principal._Static {

      public _Proxy(Principal._Static._Impl impl) {
        super(impl);
      }

      public _Proxy(Store store, long onum) {
        super(store, onum);
      }

      final public static Principal._Static $instance;

      static {
        Principal._Static._Impl impl =
            (Principal._Static._Impl) fabric.lang.Object._Static._Proxy
                .$makeStaticInstance(Principal._Static._Impl.class);
        $instance = (Principal._Static) impl.$getProxy();
        impl.$init();
      }
    }

    class _Impl extends fabric.lang.Object._Impl implements
        fabric.lang.security.Principal._Static {

      public _Impl(Store store, Label label) throws UnreachableNodeException {
        super(store, label);
      }

      @Override
      protected fabric.lang.Object._Proxy $makeProxy() {
        return new fabric.lang.security.Principal._Static._Proxy(this);
      }

      private void $init() {
      }
    }
  }
}
