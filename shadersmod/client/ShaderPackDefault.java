package shadersmod.client;

import java.io.InputStream;

public class ShaderPackDefault implements IShaderPack {
  public void close() {}
  
  public InputStream getResourceAsStream(String resName) {
    return ShaderPackDefault.class.getResourceAsStream(resName);
  }
  
  public String getName() {
    return Shaders.packNameDefault;
  }
  
  public boolean hasDirectory(String name) {
    return false;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\shadersmod\client\ShaderPackDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */