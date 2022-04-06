package us.myles.ViaVersion.api.rewriters;

import java.util.HashMap;
import java.util.Map;
import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.protocol.ClientboundPacketType;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.remapper.PacketRemapper;
import us.myles.ViaVersion.api.type.Type;

public abstract class RecipeRewriter {
  protected final Protocol protocol;
  
  protected final ItemRewriter.RewriteFunction rewriter;
  
  protected final Map<String, RecipeConsumer> recipeHandlers = new HashMap<>();
  
  protected RecipeRewriter(Protocol protocol, ItemRewriter.RewriteFunction rewriter) {
    this.protocol = protocol;
    this.rewriter = rewriter;
  }
  
  public void handle(PacketWrapper wrapper, String type) throws Exception {
    RecipeConsumer handler = this.recipeHandlers.get(type);
    if (handler != null)
      handler.accept(wrapper); 
  }
  
  public void registerDefaultHandler(ClientboundPacketType packetType) {
    this.protocol.registerOutgoing(packetType, new PacketRemapper() {
          public void registerMap() {
            handler(wrapper -> {
                  int size = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
                  for (int i = 0; i < size; i++) {
                    String type = ((String)wrapper.passthrough(Type.STRING)).replace("minecraft:", "");
                    String id = (String)wrapper.passthrough(Type.STRING);
                    RecipeRewriter.this.handle(wrapper, type);
                  } 
                });
          }
        });
  }
  
  @FunctionalInterface
  public static interface RecipeConsumer {
    void accept(PacketWrapper param1PacketWrapper) throws Exception;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\rewriters\RecipeRewriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */