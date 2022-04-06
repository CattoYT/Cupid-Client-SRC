package us.myles.viaversion.libs.gson.internal;

import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class UnsafeAllocator {
  public abstract <T> T newInstance(Class<T> paramClass) throws Exception;
  
  public static UnsafeAllocator create() {
    try {
      Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
      Field f = unsafeClass.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      final Object unsafe = f.get(null);
      final Method allocateInstance = unsafeClass.getMethod("allocateInstance", new Class[] { Class.class });
      return new UnsafeAllocator() {
          public <T> T newInstance(Class<T> c) throws Exception {
            assertInstantiable(c);
            return (T)allocateInstance.invoke(unsafe, new Object[] { c });
          }
        };
    } catch (Exception exception) {
      try {
        Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
        getConstructorId.setAccessible(true);
        final int constructorId = ((Integer)getConstructorId.invoke(null, new Object[] { Object.class })).intValue();
        final Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, int.class });
        newInstance.setAccessible(true);
        return new UnsafeAllocator() {
            public <T> T newInstance(Class<T> c) throws Exception {
              assertInstantiable(c);
              return (T)newInstance.invoke(null, new Object[] { c, Integer.valueOf(this.val$constructorId) });
            }
          };
      } catch (Exception exception1) {
        try {
          final Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
          newInstance.setAccessible(true);
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> c) throws Exception {
                assertInstantiable(c);
                return (T)newInstance.invoke(null, new Object[] { c, Object.class });
              }
            };
        } catch (Exception exception2) {
          return new UnsafeAllocator() {
              public <T> T newInstance(Class<T> c) {
                throw new UnsupportedOperationException("Cannot allocate " + c);
              }
            };
        } 
      } 
    } 
  }
  
  static void assertInstantiable(Class<?> c) {
    int modifiers = c.getModifiers();
    if (Modifier.isInterface(modifiers))
      throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.getName()); 
    if (Modifier.isAbstract(modifiers))
      throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.getName()); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\UnsafeAllocator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */