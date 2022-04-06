package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class Pair extends ASTree {
  private static final long serialVersionUID = 1L;
  
  protected ASTree left;
  
  protected ASTree right;
  
  public Pair(ASTree _left, ASTree _right) {
    this.left = _left;
    this.right = _right;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atPair(this);
  }
  
  public String toString() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append("(<Pair> ");
    sbuf.append((this.left == null) ? "<null>" : this.left.toString());
    sbuf.append(" . ");
    sbuf.append((this.right == null) ? "<null>" : this.right.toString());
    sbuf.append(')');
    return sbuf.toString();
  }
  
  public ASTree getLeft() {
    return this.left;
  }
  
  public ASTree getRight() {
    return this.right;
  }
  
  public void setLeft(ASTree _left) {
    this.left = _left;
  }
  
  public void setRight(ASTree _right) {
    this.right = _right;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\Pair.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */