package us.myles.viaversion.libs.javassist.scopedpool;

import java.util.Map;
import us.myles.viaversion.libs.javassist.ClassPool;

public interface ScopedClassPoolRepository {
  void setClassPoolFactory(ScopedClassPoolFactory paramScopedClassPoolFactory);
  
  ScopedClassPoolFactory getClassPoolFactory();
  
  boolean isPrune();
  
  void setPrune(boolean paramBoolean);
  
  ScopedClassPool createScopedClassPool(ClassLoader paramClassLoader, ClassPool paramClassPool);
  
  ClassPool findClassPool(ClassLoader paramClassLoader);
  
  ClassPool registerClassLoader(ClassLoader paramClassLoader);
  
  Map<ClassLoader, ScopedClassPool> getRegisteredCLs();
  
  void clearUnregisteredClassLoaders();
  
  void unregisterClassLoader(ClassLoader paramClassLoader);
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\scopedpool\ScopedClassPoolRepository.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */