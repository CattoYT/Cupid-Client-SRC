package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class StringL extends ASTree {
  private static final long serialVersionUID = 1L;
  
  protected String text;
  
  public StringL(String t) {
    this.text = t;
  }
  
  public String get() {
    return this.text;
  }
  
  public String toString() {
    return "\"" + this.text + "\"";
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atStringL(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\StringL.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */