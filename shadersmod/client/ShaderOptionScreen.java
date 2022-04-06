package shadersmod.client;

public class ShaderOptionScreen extends ShaderOption {
  public ShaderOptionScreen(String name) {
    super(name, (String)null, (String)null, new String[] { null }, (String)null, (String)null);
  }
  
  public String getNameText() {
    return Shaders.translate("screen." + getName(), getName());
  }
  
  public String getDescriptionText() {
    return Shaders.translate("screen." + getName() + ".comment", (String)null);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\shadersmod\client\ShaderOptionScreen.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */