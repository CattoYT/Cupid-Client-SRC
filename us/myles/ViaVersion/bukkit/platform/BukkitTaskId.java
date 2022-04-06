package us.myles.ViaVersion.bukkit.platform;

import us.myles.ViaVersion.api.platform.TaskId;

public class BukkitTaskId implements TaskId {
  private final Integer object;
  
  public BukkitTaskId(Integer object) {
    this.object = object;
  }
  
  public Integer getObject() {
    return this.object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bukkit\platform\BukkitTaskId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */