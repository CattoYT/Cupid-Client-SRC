package us.myles.viaversion.libs.javassist.compiler;

public class SyntaxError extends CompileError {
  private static final long serialVersionUID = 1L;
  
  public SyntaxError(Lex lexer) {
    super("syntax error near \"" + lexer.getTextAround() + "\"", lexer);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\SyntaxError.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */