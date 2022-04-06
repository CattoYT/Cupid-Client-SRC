package us.myles.viaversion.libs.javassist.bytecode;

class ExceptionTableEntry {
  int startPc;
  
  int endPc;
  
  int handlerPc;
  
  int catchType;
  
  ExceptionTableEntry(int start, int end, int handle, int type) {
    this.startPc = start;
    this.endPc = end;
    this.handlerPc = handle;
    this.catchType = type;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\bytecode\ExceptionTableEntry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */