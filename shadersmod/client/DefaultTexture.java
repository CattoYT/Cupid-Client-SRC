package shadersmod.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.resources.IResourceManager;

public class DefaultTexture extends AbstractTexture {
  public DefaultTexture() {
    loadTexture((IResourceManager)null);
  }
  
  public void loadTexture(IResourceManager resourcemanager) {
    int[] aint = ShadersTex.createAIntImage(1, -1);
    ShadersTex.setupTexture(getMultiTexID(), aint, 1, 1, false, false);
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar!\shadersmod\client\DefaultTexture.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */