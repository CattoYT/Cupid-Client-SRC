package us.myles.viaversion.libs.fastutil.ints;

import java.io.Serializable;

public abstract class AbstractInt2IntFunction implements Int2IntFunction, Serializable {
  private static final long serialVersionUID = -4940583368468432370L;
  
  protected int defRetValue;
  
  public void defaultReturnValue(int rv) {
    this.defRetValue = rv;
  }
  
  public int defaultReturnValue() {
    return this.defRetValue;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\fastutil\ints\AbstractInt2IntFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */