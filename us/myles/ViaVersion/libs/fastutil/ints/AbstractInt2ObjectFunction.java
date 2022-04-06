package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2ObjectFunction<V> implements Int2ObjectFunction<V>, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  protected V defRetValue;
  
  public void defaultReturnValue(V rv) {
    this.defRetValue = rv;
  }
  
  public V defaultReturnValue() {
    return this.defRetValue;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\AbstractInt2ObjectFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */