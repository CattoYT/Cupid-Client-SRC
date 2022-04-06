package us.myles.viaversion.libs.gson.internal.reflect;

import java.lang.reflect.AccessibleObject;
import us.myles.viaversion.libs.gson.internal.JavaVersion;

public abstract class ReflectionAccessor {
  private static final ReflectionAccessor instance = (JavaVersion.getMajorJavaVersion() < 9) ? new PreJava9ReflectionAccessor() : new UnsafeReflectionAccessor();
  
  public abstract void makeAccessible(AccessibleObject paramAccessibleObject);
  
  public static ReflectionAccessor getInstance() {
    return instance;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\reflect\ReflectionAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */