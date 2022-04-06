package us.myles.viaversion.libs.javassist;

final class ClassPathList {
  ClassPathList next;
  
  ClassPath path;
  
  ClassPathList(ClassPath p, ClassPathList n) {
    this.next = n;
    this.path = p;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\ClassPathList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */