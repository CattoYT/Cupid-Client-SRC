package us.myles.viaversion.libs.javassist.compiler;

import us.myles.viaversion.libs.javassist.CannotCompileException;
import us.myles.viaversion.libs.javassist.NotFoundException;

public class CompileError extends Exception {
  private static final long serialVersionUID = 1L;
  
  private Lex lex;
  
  private String reason;
  
  public CompileError(String s, Lex l) {
    this.reason = s;
    this.lex = l;
  }
  
  public CompileError(String s) {
    this.reason = s;
    this.lex = null;
  }
  
  public CompileError(CannotCompileException e) {
    this(e.getReason());
  }
  
  public CompileError(NotFoundException e) {
    this("cannot find " + e.getMessage());
  }
  
  public Lex getLex() {
    return this.lex;
  }
  
  public String getMessage() {
    return this.reason;
  }
  
  public String toString() {
    return "compile error: " + this.reason;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\CompileError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */