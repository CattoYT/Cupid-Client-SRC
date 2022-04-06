package us.myles.ViaVersion.api.rewriters;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;

public class SoundRewriter {
  protected final Protocol protocol;
  
  protected final IdRewriteFunction idRewriter;
  
  public SoundRewriter(Protocol protocol) {
    this.protocol = protocol;
    this.idRewriter = (id -> protocol.getMappingData().getSoundMappings().getNewId(id));
  }
  
  public SoundRewriter(Protocol protocol, IdRewriteFunction idRewriter) {
    this.protocol = protocol;
    this.idRewriter = idRewriter;
  }
  
  public void registerSound(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            map((Type)Type.VAR_INT);
            handler(wrapper -> {
                  int soundId = ((Integer)wrapper.get((Type)Type.VAR_INT, 0)).intValue();
                  int mappedId = SoundRewriter.this.idRewriter.rewrite(soundId);
                  if (mappedId == -1) {
                    wrapper.cancel();
                  } else if (soundId != mappedId) {
                    wrapper.set((Type)Type.VAR_INT, 0, Integer.valueOf(mappedId));
                  } 
                });
          }
        });
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\SoundRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */