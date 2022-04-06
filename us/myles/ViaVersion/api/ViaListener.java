package us.myles.ViaVersion.api;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.protocol.Protocol;

public abstract class ViaListener {
  private final Class<? extends Protocol> requiredPipeline;
  
  private boolean registered;
  
  public ViaListener(Class<? extends Protocol> requiredPipeline) {
    this.requiredPipeline = requiredPipeline;
  }
  
  @Nullable
  protected UserConnection getUserConnection(UUID uuid) {
    return Via.getManager().getConnection(uuid);
  }
  
  protected boolean isOnPipe(UUID uuid) {
    UserConnection userConnection = getUserConnection(uuid);
    return (userConnection != null && (this.requiredPipeline == null || userConnection
      .getProtocolInfo().getPipeline().contains(this.requiredPipeline)));
  }
  
  public abstract void register();
  
  protected Class<? extends Protocol> getRequiredPipeline() {
    return this.requiredPipeline;
  }
  
  protected boolean isRegistered() {
    return this.registered;
  }
  
  protected void setRegistered(boolean registered) {
    this.registered = registered;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\ViaListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */