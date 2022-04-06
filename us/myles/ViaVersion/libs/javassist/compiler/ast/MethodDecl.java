package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class MethodDecl extends ASTList {
  private static final long serialVersionUID = 1L;
  
  public static final String initName = "<init>";
  
  public MethodDecl(ASTree _head, ASTList _tail) {
    super(_head, _tail);
  }
  
  public boolean isConstructor() {
    Symbol sym = getReturn().getVariable();
    return (sym != null && "<init>".equals(sym.get()));
  }
  
  public ASTList getModifiers() {
    return (ASTList)getLeft();
  }
  
  public Declarator getReturn() {
    return (Declarator)tail().head();
  }
  
  public ASTList getParams() {
    return (ASTList)sublist(2).head();
  }
  
  public ASTList getThrows() {
    return (ASTList)sublist(3).head();
  }
  
  public Stmnt getBody() {
    return (Stmnt)sublist(4).head();
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atMethodDecl(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\MethodDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */