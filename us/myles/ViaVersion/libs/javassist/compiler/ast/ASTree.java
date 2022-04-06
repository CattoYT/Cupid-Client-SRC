package us.myles.viaversion.libs.javassist.compiler.ast;

import java.io.Serializable;
import us.myles.viaversion.libs.javassist.compiler.CompileError;

public abstract class ASTree implements Serializable {
  private static final long serialVersionUID = 1L;
  
  public ASTree getLeft() {
    return null;
  }
  
  public ASTree getRight() {
    return null;
  }
  
  public void setLeft(ASTree _left) {}
  
  public void setRight(ASTree _right) {}
  
  public abstract void accept(Visitor paramVisitor) throws CompileError;
  
  public String toString() {
    StringBuffer sbuf = new StringBuffer();
    sbuf.append('<');
    sbuf.append(getTag());
    sbuf.append('>');
    return sbuf.toString();
  }
  
  protected String getTag() {
    String name = getClass().getName();
    return name.substring(name.lastIndexOf('.') + 1);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\ASTree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */