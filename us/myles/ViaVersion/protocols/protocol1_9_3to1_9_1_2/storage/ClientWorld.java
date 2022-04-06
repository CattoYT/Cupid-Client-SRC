package us.myles.ViaVersion.protocols.protocol1_9_3to1_9_1_2.storage;

import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.Environment;

public class ClientWorld extends StoredObject {
  private Environment environment;
  
  public ClientWorld(UserConnection user) {
    super(user);
  }
  
  public Environment getEnvironment() {
    return this.environment;
  }
  
  public void setEnvironment(int environmentId) {
    this.environment = Environment.getEnvironmentById(environmentId);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9_3to1_9_1_2\storage\ClientWorld.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */