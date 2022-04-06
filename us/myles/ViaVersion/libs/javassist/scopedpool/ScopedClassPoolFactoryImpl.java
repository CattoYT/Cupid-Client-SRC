package us.myles.viaversion.libs.javassist.scopedpool;

import us.myles.viaversion.libs.javassist.ClassPool;

public class ScopedClassPoolFactoryImpl implements ScopedClassPoolFactory {
  public ScopedClassPool create(ClassLoader cl, ClassPool src, ScopedClassPoolRepository repository) {
    return new ScopedClassPool(cl, src, repository, false);
  }
  
  public ScopedClassPool create(ClassPool src, ScopedClassPoolRepository repository) {
    return new ScopedClassPool(null, src, repository, true);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\scopedpool\ScopedClassPoolFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */