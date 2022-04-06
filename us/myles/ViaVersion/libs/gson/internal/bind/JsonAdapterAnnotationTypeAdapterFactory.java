package us.myles.viaversion.libs.gson.internal.bind;

import us.myles.viaversion.libs.gson.Gson;
import us.myles.viaversion.libs.gson.JsonDeserializer;
import us.myles.viaversion.libs.gson.JsonSerializer;
import us.myles.viaversion.libs.gson.TypeAdapter;
import us.myles.viaversion.libs.gson.TypeAdapterFactory;
import us.myles.viaversion.libs.gson.annotations.JsonAdapter;
import us.myles.viaversion.libs.gson.internal.ConstructorConstructor;
import us.myles.viaversion.libs.gson.reflect.TypeToken;

public final class JsonAdapterAnnotationTypeAdapterFactory implements TypeAdapterFactory {
  private final ConstructorConstructor constructorConstructor;
  
  public JsonAdapterAnnotationTypeAdapterFactory(ConstructorConstructor constructorConstructor) {
    this.constructorConstructor = constructorConstructor;
  }
  
  public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> targetType) {
    Class<? super T> rawType = targetType.getRawType();
    JsonAdapter annotation = rawType.<JsonAdapter>getAnnotation(JsonAdapter.class);
    if (annotation == null)
      return null; 
    return (TypeAdapter)getTypeAdapter(this.constructorConstructor, gson, targetType, annotation);
  }
  
  TypeAdapter<?> getTypeAdapter(ConstructorConstructor constructorConstructor, Gson gson, TypeToken<?> type, JsonAdapter annotation) {
    TypeAdapter<?> typeAdapter;
    Object instance = constructorConstructor.get(TypeToken.get(annotation.value())).construct();
    if (instance instanceof TypeAdapter) {
      typeAdapter = (TypeAdapter)instance;
    } else if (instance instanceof TypeAdapterFactory) {
      typeAdapter = ((TypeAdapterFactory)instance).create(gson, type);
    } else if (instance instanceof JsonSerializer || instance instanceof JsonDeserializer) {
      JsonSerializer<?> serializer = (instance instanceof JsonSerializer) ? (JsonSerializer)instance : null;
      JsonDeserializer<?> deserializer = (instance instanceof JsonDeserializer) ? (JsonDeserializer)instance : null;
      typeAdapter = new TreeTypeAdapter(serializer, deserializer, gson, type, null);
    } else {
      throw new IllegalArgumentException("Invalid attempt to bind an instance of " + instance
          .getClass().getName() + " as a @JsonAdapter for " + type.toString() + ". @JsonAdapter value must be a TypeAdapter, TypeAdapterFactory, JsonSerializer or JsonDeserializer.");
    } 
    if (typeAdapter != null && annotation.nullSafe())
      typeAdapter = typeAdapter.nullSafe(); 
    return typeAdapter;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\gson\internal\bind\JsonAdapterAnnotationTypeAdapterFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */