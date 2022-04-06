package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class BinExpr extends Expr {
  private static final long serialVersionUID = 1L;
  
  private BinExpr(int op, ASTree _head, ASTList _tail) {
    super(op, _head, _tail);
  }
  
  public static BinExpr makeBin(int op, ASTree oprand1, ASTree oprand2) {
    return new BinExpr(op, oprand1, new ASTList(oprand2));
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atBinExpr(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\BinExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */