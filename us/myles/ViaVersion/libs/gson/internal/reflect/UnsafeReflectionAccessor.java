package us.myles.viaversion.libs.gson.internal.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import us.myles.viaversion.libs.gson.JsonIOException;

final class UnsafeReflectionAccessor extends ReflectionAccessor {
  private static Class unsafeClass;
  
  private final Object theUnsafe = getUnsafeInstance();
  
  private final Field overrideField = getOverrideField();
  
  public void makeAccessible(AccessibleObject ao) {
    boolean success = makeAccessibleWithUnsafe(ao);
    if (!success)
      try {
        ao.setAccessible(true);
      } catch (SecurityException e) {
        throw new JsonIOException("Gson couldn't modify fields for " + ao + "\nand sun.misc.Unsafe not found.\nEither write a custom type adapter, or make fields accessible, or include sun.misc.Unsafe.", e);
      }  
  }
  
  boolean makeAccessibleWithUnsafe(AccessibleObject ao) {
    if (this.theUnsafe != null && this.overrideField != null)
      try {
        Method method = unsafeClass.getMethod("objectFieldOffset", new Class[] { Field.class });
        long overrideOffset = ((Long)method.invoke(this.theUnsafe, new Object[] { this.overrideField })).longValue();
        Method putBooleanMethod = unsafeClass.getMethod("putBoolean", new Class[] { Object.class, long.class, boolean.class });
        putBooleanMethod.invoke(this.theUnsafe, new Object[] { ao, Long.valueOf(overrideOffset), Boolean.valueOf(true) });
        return true;
      } catch (Exception exception) {} 
    return false;
  }
  
  private static Object getUnsafeInstance() {
    try {
      unsafeClass = Class.forName("sun.misc.Unsafe");
      Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      return unsafeField.get(null);
    } catch (Exception e) {
      return null;
    } 
  }
  
  private static Field getOverrideField() {
    try {
      return AccessibleObject.class.getDeclaredField("override");
    } catch (NoSuchFieldException e) {
      return null;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\reflect\UnsafeReflectionAccessor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */