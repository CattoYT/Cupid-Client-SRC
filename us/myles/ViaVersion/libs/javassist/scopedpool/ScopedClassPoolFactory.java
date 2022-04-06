package us.myles.viaversion.libs.javassist.scopedpool;

import us.myles.viaversion.libs.javassist.ClassPool;

public interface ScopedClassPoolFactory {
  ScopedClassPool create(ClassLoader paramClassLoader, ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
  
  ScopedClassPool create(ClassPool paramClassPool, ScopedClassPoolRepository paramScopedClassPoolRepository);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\scopedpool\ScopedClassPoolFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */