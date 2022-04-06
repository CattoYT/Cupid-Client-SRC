package us.myles.ViaVersion.velocity.platform;

import com.velocitypowered.api.scheduler.ScheduledTask;
import us.myles.ViaVersion.api.platform.TaskId;

public class VelocityTaskId implements TaskId {
  private final ScheduledTask object;
  
  public VelocityTaskId(ScheduledTask object) {
    this.object = object;
  }
  
  public ScheduledTask getObject() {
    return this.object;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\velocity\platform\VelocityTaskId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */