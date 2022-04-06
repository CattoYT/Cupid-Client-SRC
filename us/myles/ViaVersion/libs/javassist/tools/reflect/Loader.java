package us.myles.viaversion.libs.javassist.tools.reflect;

import us.myles.viaversion.libs.javassist.CannotCompileException;
import us.myles.viaversion.libs.javassist.ClassPool;
import us.myles.viaversion.libs.javassist.Loader;
import us.myles.viaversion.libs.javassist.NotFoundException;

public class Loader extends Loader {
  protected Reflection reflection;
  
  public static void main(String[] args) throws Throwable {
    Loader cl = new Loader();
    cl.run(args);
  }
  
  public Loader() throws CannotCompileException, NotFoundException {
    delegateLoadingOf("us.myles.viaversion.libs.javassist.tools.reflect.Loader");
    this.reflection = new Reflection();
    ClassPool pool = ClassPool.getDefault();
    addTranslator(pool, this.reflection);
  }
  
  public boolean makeReflective(String clazz, String metaobject, String metaclass) throws CannotCompileException, NotFoundException {
    return this.reflection.makeReflective(clazz, metaobject, metaclass);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\tools\reflect\Loader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */