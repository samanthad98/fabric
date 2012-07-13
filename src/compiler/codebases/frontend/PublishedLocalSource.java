package codebases.frontend;

import java.io.IOException;

import javax.tools.FileObject;

import fabric.common.FabricLocation;

public class PublishedLocalSource extends DerivedLocalSource {

  protected PublishedLocalSource(String name, FileObject derivedFrom,
      boolean userSpecified, FabricLocation namespace) throws IOException {
    super(name, derivedFrom, userSpecified, namespace);
  }

  // Allow PublishedLocalSource to be equal to RemoteSource
  @Override
  public boolean equals(Object o) {
    if (o instanceof LocalSource) {
      return super.equals(o);
    } else if (o instanceof CodebaseSource) {
      CodebaseSource src = (CodebaseSource) o;
      return name().equals(src.name())
          && canonicalNamespace().equals(src.canonicalNamespace());
    }
    return false;
  }

  @Override
  public int hashCode() {
    return name().hashCode() ^ namespace().hashCode();
  }

}
