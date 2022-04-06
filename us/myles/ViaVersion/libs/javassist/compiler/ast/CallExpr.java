package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;
import us.myles.viaversion.libs.javassist.compiler.MemberResolver;

public class CallExpr extends Expr {
  private static final long serialVersionUID = 1L;
  
  private MemberResolver.Method method;
  
  private CallExpr(ASTree _head, ASTList _tail) {
    super(67, _head, _tail);
    this.method = null;
  }
  
  public void setMethod(MemberResolver.Method m) {
    this.method = m;
  }
  
  public MemberResolver.Method getMethod() {
    return this.method;
  }
  
  public static CallExpr makeCall(ASTree target, ASTree args) {
    return new CallExpr(target, new ASTList(args));
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atCallExpr(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\CallExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */