package us.myles.ViaVersion.bungee.listeners;

import java.util.Collections;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.api.minecraft.metadata.MetaType;
import us.myles.ViaVersion.api.minecraft.metadata.Metadata;
import us.myles.ViaVersion.api.minecraft.metadata.types.MetaType1_9;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.api.type.types.version.Types1_9;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class ElytraPatch implements Listener {
  @EventHandler(priority = 32)
  public void onServerConnected(ServerConnectedEvent event) {
    UserConnection user = Via.getManager().getConnection(event.getPlayer().getUniqueId());
    if (user == null)
      return; 
    try {
      if (user.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
        int entityId = ((EntityTracker1_9)user.get(EntityTracker1_9.class)).getProvidedEntityId();
        PacketWrapper wrapper = new PacketWrapper(57, null, user);
        wrapper.write((Type)Type.VAR_INT, Integer.valueOf(entityId));
        wrapper.write(Types1_9.METADATA_LIST, Collections.singletonList(new Metadata(0, (MetaType)MetaType1_9.Byte, Byte.valueOf((byte)0))));
        wrapper.send(Protocol1_9To1_8.class);
      } 
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\bungee\listeners\ElytraPatch.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */