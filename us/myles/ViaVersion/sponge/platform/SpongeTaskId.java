package us.myles.ViaVersion.sponge.platform;

import org.spongepowered.api.scheduler.Task;
import us.myles.ViaVersion.api.platform.TaskId;

public class SpongeTaskId implements TaskId {
  private final Task object;
  
  public SpongeTaskId(Task object) {
    this.object = object;
  }
  
  public Task getObject() {
    return this.object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\sponge\platform\SpongeTaskId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */