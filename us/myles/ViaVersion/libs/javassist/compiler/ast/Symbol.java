package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class Symbol extends ASTree {
  private static final long serialVersionUID = 1L;
  
  protected String identifier;
  
  public Symbol(String sym) {
    this.identifier = sym;
  }
  
  public String get() {
    return this.identifier;
  }
  
  public String toString() {
    return this.identifier;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atSymbol(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\Symbol.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */