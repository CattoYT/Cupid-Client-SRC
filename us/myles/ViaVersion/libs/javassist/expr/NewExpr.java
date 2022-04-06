package us.myles.viaversion.libs.javassist.expr;

import us.myles.viaversion.libs.javassist.CannotCompileException;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.CtBehavior;
import us.myles.viaversion.libs.javassist.CtClass;
import us.myles.viaversion.libs.javassist.CtConstructor;
import us.myles.viaversion.libs.javassist.NotFoundException;
import us.myles.viaversion.libs.javassist.bytecode.BadBytecode;
import us.myles.viaversion.libs.javassist.bytecode.Bytecode;
import us.myles.viaversion.libs.javassist.bytecode.CodeAttribute;
import us.myles.viaversion.libs.javassist.bytecode.CodeIterator;
import us.myles.viaversion.libs.javassist.bytecode.ConstPool;
import us.myles.viaversion.libs.javassist.bytecode.Descriptor;
import us.myles.viaversion.libs.javassist.bytecode.MethodInfo;
import us.myles.viaversion.libs.javassist.compiler.CompileError;
import us.myles.viaversion.libs.javassist.compiler.Javac;
import us.myles.viaversion.libs.javassist.compiler.JvstCodeGen;
import us.myles.viaversion.libs.javassist.compiler.JvstTypeChecker;
import us.myles.viaversion.libs.javassist.compiler.ProceedHandler;
import us.myles.viaversion.libs.javassist.compiler.ast.ASTList;

public class NewExpr extends Expr {
  String newTypeName;
  
  int newPos;
  
  protected NewExpr(int pos, CodeIterator i, CtClass declaring, MethodInfo m, String type, int np) {
    super(pos, i, declaring, m);
    this.newTypeName = type;
    this.newPos = np;
  }
  
  public CtBehavior where() {
    return super.where();
  }
  
  public int getLineNumber() {
    return super.getLineNumber();
  }
  
  public String getFileName() {
    return super.getFileName();
  }
  
  private CtClass getCtClass() throws NotFoundException {
    return this.thisClass.getClassPool().get(this.newTypeName);
  }
  
  public String getClassName() {
    return this.newTypeName;
  }
  
  public String getSignature() {
    ConstPool constPool = getConstPool();
    int methodIndex = this.iterator.u16bitAt(this.currentPos + 1);
    return constPool.getMethodrefType(methodIndex);
  }
  
  public CtConstructor getConstructor() throws NotFoundException {
    ConstPool cp = getConstPool();
    int index = this.iterator.u16bitAt(this.currentPos + 1);
    String desc = cp.getMethodrefType(index);
    return getCtClass().getConstructor(desc);
  }
  
  public CtClass[] mayThrow() {
    return super.mayThrow();
  }
  
  private int canReplace() throws CannotCompileException {
    int op = this.iterator.byteAt(this.newPos + 3);
    if (op == 89)
      return (this.iterator.byteAt(this.newPos + 4) == 94 && this.iterator
        .byteAt(this.newPos + 5) == 88) ? 6 : 4; 
    if (op == 90 && this.iterator
      .byteAt(this.newPos + 4) == 95)
      return 5; 
    return 3;
  }
  
  public void replace(String statement) throws CannotCompileException {
    this.thisClass.getClassFile();
    int bytecodeSize = 3;
    int pos = this.newPos;
    int newIndex = this.iterator.u16bitAt(pos + 1);
    int codeSize = canReplace();
    int end = pos + codeSize;
    for (int i = pos; i < end; i++)
      this.iterator.writeByte(0, i); 
    ConstPool constPool = getConstPool();
    pos = this.currentPos;
    int methodIndex = this.iterator.u16bitAt(pos + 1);
    String signature = constPool.getMethodrefType(methodIndex);
    Javac jc = new Javac(this.thisClass);
    ClassPool cp = this.thisClass.getClassPool();
    CodeAttribute ca = this.iterator.get();
    try {
      CtClass[] params = Descriptor.getParameterTypes(signature, cp);
      CtClass newType = cp.get(this.newTypeName);
      int paramVar = ca.getMaxLocals();
      jc.recordParams(this.newTypeName, params, true, paramVar, 
          withinStatic());
      int retVar = jc.recordReturnType(newType, true);
      jc.recordProceed(new ProceedForNew(newType, newIndex, methodIndex));
      checkResultValue(newType, statement);
      Bytecode bytecode = jc.getBytecode();
      storeStack(params, true, paramVar, bytecode);
      jc.recordLocalVariables(ca, pos);
      bytecode.addConstZero(newType);
      bytecode.addStore(retVar, newType);
      jc.compileStmnt(statement);
      if (codeSize > 3)
        bytecode.addAload(retVar); 
      replace0(pos, bytecode, 3);
    } catch (CompileError e) {
      throw new CannotCompileException(e);
    } catch (NotFoundException e) {
      throw new CannotCompileException(e);
    } catch (BadBytecode e) {
      throw new CannotCompileException("broken method");
    } 
  }
  
  static class ProceedForNew implements ProceedHandler {
    CtClass newType;
    
    int newIndex;
    
    int methodIndex;
    
    ProceedForNew(CtClass nt, int ni, int mi) {
      this.newType = nt;
      this.newIndex = ni;
      this.methodIndex = mi;
    }
    
    public void doit(JvstCodeGen gen, Bytecode bytecode, ASTList args) throws CompileError {
      bytecode.addOpcode(187);
      bytecode.addIndex(this.newIndex);
      bytecode.addOpcode(89);
      gen.atMethodCallCore(this.newType, "<init>", args, false, true, -1, null);
      gen.setType(this.newType);
    }
    
    public void setReturnType(JvstTypeChecker c, ASTList args) throws CompileError {
      c.atMethodCallCore(this.newType, "<init>", args);
      c.setType(this.newType);
    }
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\expr\NewExpr.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */