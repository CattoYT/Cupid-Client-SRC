package us.myles.viaversion.libs.gson.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import us.myles.viaversion.libs.gson.InstanceCreator;
import us.myles.viaversion.libs.gson.JsonIOException;
import us.myles.viaversion.libs.gson.internal.reflect.ReflectionAccessor;
import us.myles.viaversion.libs.gson.reflect.TypeToken;

public final class ConstructorConstructor {
  private final Map<Type, InstanceCreator<?>> instanceCreators;
  
  private final ReflectionAccessor accessor = ReflectionAccessor.getInstance();
  
  public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators) {
    this.instanceCreators = instanceCreators;
  }
  
  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    Class<? super T> rawType = typeToken.getRawType();
    final InstanceCreator<T> typeCreator = (InstanceCreator<T>)this.instanceCreators.get(type);
    if (typeCreator != null)
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)typeCreator.createInstance(type);
          }
        }; 
    final InstanceCreator<T> rawTypeCreator = (InstanceCreator<T>)this.instanceCreators.get(rawType);
    if (rawTypeCreator != null)
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)rawTypeCreator.createInstance(type);
          }
        }; 
    ObjectConstructor<T> defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null)
      return defaultConstructor; 
    ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(type, rawType);
    if (defaultImplementation != null)
      return defaultImplementation; 
    return newUnsafeAllocator(type, rawType);
  }
  
  private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
    try {
      final Constructor<? super T> constructor = rawType.getDeclaredConstructor(new Class[0]);
      if (!constructor.isAccessible())
        this.accessor.makeAccessible(constructor); 
      return new ObjectConstructor<T>() {
          public T construct() {
            try {
              Object[] args = null;
              return constructor.newInstance(args);
            } catch (InstantiationException e) {
              throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
            } catch (InvocationTargetException e) {
              throw new RuntimeException("Failed to invoke " + constructor + " with no args", e
                  .getTargetException());
            } catch (IllegalAccessException e) {
              throw new AssertionError(e);
            } 
          }
        };
    } catch (NoSuchMethodException e) {
      return null;
    } 
  }
  
  private <T> ObjectConstructor<T> newDefaultImplementationConstructor(final Type type, Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new TreeSet();
            }
          }; 
      if (EnumSet.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              if (type instanceof ParameterizedType) {
                Type elementType = ((ParameterizedType)type).getActualTypeArguments()[0];
                if (elementType instanceof Class)
                  return (T)EnumSet.noneOf((Class<Enum>)elementType); 
                throw new JsonIOException("Invalid EnumSet type: " + type.toString());
              } 
              throw new JsonIOException("Invalid EnumSet type: " + type.toString());
            }
          }; 
      if (Set.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new LinkedHashSet();
            }
          }; 
      if (Queue.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ArrayDeque();
            }
          }; 
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)new ArrayList();
          }
        };
    } 
    if (Map.class.isAssignableFrom(rawType)) {
      if (ConcurrentNavigableMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ConcurrentSkipListMap<Object, Object>();
            }
          }; 
      if (ConcurrentMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new ConcurrentHashMap<Object, Object>();
            }
          }; 
      if (SortedMap.class.isAssignableFrom(rawType))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new TreeMap<Object, Object>();
            }
          }; 
      if (type instanceof ParameterizedType && !String.class.isAssignableFrom(
          TypeToken.get(((ParameterizedType)type).getActualTypeArguments()[0]).getRawType()))
        return new ObjectConstructor<T>() {
            public T construct() {
              return (T)new LinkedHashMap<Object, Object>();
            }
          }; 
      return new ObjectConstructor<T>() {
          public T construct() {
            return (T)new LinkedTreeMap<Object, Object>();
          }
        };
    } 
    return null;
  }
  
  private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType) {
    return new ObjectConstructor<T>() {
        private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
        
        public T construct() {
          try {
            Object newInstance = this.unsafeAllocator.newInstance(rawType);
            return (T)newInstance;
          } catch (Exception e) {
            throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". Registering an InstanceCreator with Gson for this type may fix this problem.", e);
          } 
        }
      };
  }
  
  public String toString() {
    return this.instanceCreators.toString();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\ConstructorConstructor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */