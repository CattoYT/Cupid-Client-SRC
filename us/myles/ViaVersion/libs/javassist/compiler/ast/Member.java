package us.myles.viaversion.libs.javassist.compiler.ast;

import us.myles.viaversion.libs.javassist.CtField;
import us.myles.viaversion.libs.javassist.compiler.CompileError;

public class Member extends Symbol {
  private static final long serialVersionUID = 1L;
  
  private CtField field;
  
  public Member(String name) {
    super(name);
    this.field = null;
  }
  
  public void setField(CtField f) {
    this.field = f;
  }
  
  public CtField getField() {
    return this.field;
  }
  
  public void accept(Visitor v) throws CompileError {
    v.atMember(this);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\compiler\ast\Member.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */