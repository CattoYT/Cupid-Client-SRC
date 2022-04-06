package us.myles.ViaVersion.protocols.protocol1_9to1_8.storage;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.data.StoredObject;
import us.myles.ViaVersion.api.data.UserConnection;
import us.myles.ViaVersion.protocols.protocol1_9to1_8.providers.BulkChunkTranslatorProvider;

public class ClientChunks extends StoredObject {
  private final Set<Long> loadedChunks = Sets.newConcurrentHashSet();
  
  private final Set<Long> bulkChunks = Sets.newConcurrentHashSet();
  
  public ClientChunks(UserConnection user) {
    super(user);
  }
  
  public static long toLong(int msw, int lsw) {
    return (msw << 32L) + lsw - -2147483648L;
  }
  
  public List<Object> transformMapChunkBulk(Object packet) throws Exception {
    return ((BulkChunkTranslatorProvider)Via.getManager().getProviders().get(BulkChunkTranslatorProvider.class)).transformMapChunkBulk(packet, this);
  }
  
  public Set<Long> getLoadedChunks() {
    return this.loadedChunks;
  }
  
  public Set<Long> getBulkChunks() {
    return this.bulkChunks;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_9to1_8\storage\ClientChunks.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */