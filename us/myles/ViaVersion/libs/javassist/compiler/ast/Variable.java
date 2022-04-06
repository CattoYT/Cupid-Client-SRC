package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class Variable extends Symbol {
  private static final long serialVersionUID = 1L;
  
  protected Declarator declarator;
  
  public Variable(String sym, Declarator d) {
    super(sym);
    this.declarator = d;
  }
  
  public Declarator getDeclarator() {
    return this.declarator;
  }
  
  public String toString() {
    return this.identifier + ":" + this.declarator.getType();
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atVariable(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\Variable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */