package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class FieldDecl extends ASTList {
  private static final long serialVersionUID = 1L;
  
  public FieldDecl(ASTree _head, ASTList _tail) {
    super(_head, _tail);
  }
  
  public ASTList getModifiers() {
    return (ASTList)getLeft();
  }
  
  public Declarator getDeclarator() {
    return (Declarator)tail().head();
  }
  
  public ASTree getInit() {
    return sublist(2).head();
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atFieldDecl(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\FieldDecl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */