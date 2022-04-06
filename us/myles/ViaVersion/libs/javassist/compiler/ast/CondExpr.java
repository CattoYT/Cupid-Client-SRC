package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class CondExpr extends ASTList {
  private static final long serialVersionUID = 1L;
  
  public CondExpr(ASTree cond, ASTree thenp, ASTree elsep) {
    super(cond, new ASTList(thenp, new ASTList(elsep)));
  }
  
  public ASTree condExpr() {
    return head();
  }
  
  public void setCond(ASTree t) {
    setHead(t);
  }
  
  public ASTree thenExpr() {
    return tail().head();
  }
  
  public void setThen(ASTree t) {
    tail().setHead(t);
  }
  
  public ASTree elseExpr() {
    return tail().tail().head();
  }
  
  public void setElse(ASTree t) {
    tail().tail().setHead(t);
  }
  
  public String getTag() {
    return "?:";
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atCondExpr(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\CondExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */