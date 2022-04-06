package us.myles.viaversion.libs.gson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import us.myles.viaversion.libs.gson.internal.;

public final class FieldAttributes {
  private final Field field;
  
  public FieldAttributes(Field f) {
    .Gson.Preconditions.checkNotNull(f);
    this.field = f;
  }
  
  public Class<?> getDeclaringClass() {
    return this.field.getDeclaringClass();
  }
  
  public String getName() {
    return this.field.getName();
  }
  
  public Type getDeclaredType() {
    return this.field.getGenericType();
  }
  
  public Class<?> getDeclaredClass() {
    return this.field.getType();
  }
  
  public <T extends Annotation> T getAnnotation(Class<T> annotation) {
    return this.field.getAnnotation(annotation);
  }
  
  public Collection<Annotation> getAnnotations() {
    return Arrays.asList(this.field.getAnnotations());
  }
  
  public boolean hasModifier(int modifier) {
    return ((this.field.getModifiers() & modifier) != 0);
  }
  
  Object get(Object instance) throws IllegalAccessException {
    return this.field.get(instance);
  }
  
  boolean isSynthetic() {
    return this.field.isSynthetic();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\FieldAttributes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */