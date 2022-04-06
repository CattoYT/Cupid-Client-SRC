package us.myles.viaversion.libs.fastutil.objects;

import java.io.Serializable;

public abstract class AbstractObject2ObjectFunction<K, V> implements Object2ObjectFunction<K, V>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  protected V defRetValue;
  
  public void defaultReturnValue(V rv) {
    this.defRetValue = rv;
  }
  
  public V defaultReturnValue() {
    return this.defRetValue;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\objects\AbstractObject2ObjectFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */