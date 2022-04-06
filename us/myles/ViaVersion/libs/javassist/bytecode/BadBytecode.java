package us.myles.viaversion.libs.javassist.bytecode;

public class BadBytecode extends Exception {
  private static final long serialVersionUID = 1L;
  
  public BadBytecode(int opcode) {
    super("bytecode " + opcode);
  }
  
  public BadBytecode(String msg) {
    super(msg);
  }
  
  public BadBytecode(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  public BadBytecode(MethodInfo minfo, Throwable cause) {
    super(minfo.toString() + " in " + minfo
        .getConstPool().getClassName() + ": " + cause
        .getMessage(), cause);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\BadBytecode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */