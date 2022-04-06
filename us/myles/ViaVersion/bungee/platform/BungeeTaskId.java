package us.myles.ViaVersion.bungee.platform;

import us.myles.ViaVersion.api.platform.TaskId;

public class BungeeTaskId implements TaskId {
  private final Integer object;
  
  public BungeeTaskId(Integer object) {
    this.object = object;
  }
  
  public Integer getObject() {
    return this.object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\platform\BungeeTaskId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */