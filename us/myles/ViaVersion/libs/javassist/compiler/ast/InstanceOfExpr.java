package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class InstanceOfExpr extends CastExpr {
  private static final long serialVersionUID = 1L;
  
  public InstanceOfExpr(ASTList className, int dim, ASTree expr) {
    super(className, dim, expr);
  }
  
  public InstanceOfExpr(int type, int dim, ASTree expr) {
    super(type, dim, expr);
  }
  
  public String getTag() {
    return "instanceof:" + this.castType + ":" + this.arrayDim;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atInstanceOfExpr(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\InstanceOfExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */