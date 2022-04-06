package us.myles.viaversion.libs.javassist;

class FieldInitLink {
  FieldInitLink next;
  
  CtField field;
  
  CtField.Initializer init;
  
  FieldInitLink(CtField f, CtField.Initializer i) {
    this.next = null;
    this.field = f;
    this.init = i;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\FieldInitLink.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */