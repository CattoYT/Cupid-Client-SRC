package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class Keyword extends ASTree {
  private static final long serialVersionUID = 1L;
  
  protected int tokenId;
  
  public Keyword(int token) {
    this.tokenId = token;
  }
  
  public int get() {
    return this.tokenId;
  }
  
  public String toString() {
    return "id:" + this.tokenId;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atKeyword(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\Keyword.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */