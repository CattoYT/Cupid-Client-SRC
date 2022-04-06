package us.myles.viaversion.libs.javassist.compiler;

import us.myles.viaversion.libs.javassist.compiler.ast.ASTree;

public class NoFieldException extends CompileError {
  private static final long serialVersionUID = 1L;
  
  private String fieldName;
  
  private ASTree expr;
  
  public NoFieldException(String name, ASTree e) {
    super("no such field: " + name);
    this.fieldName = name;
    this.expr = e;
  }
  
  public String getField() {
    return this.fieldName;
  }
  
  public ASTree getExpr() {
    return this.expr;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\NoFieldException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */