package us.myles.ViaVersion.protocols.protocol1_14to1_13_2.data;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;

public class RecipeRewriter1_14 extends RecipeRewriter1_13_2 {
  public RecipeRewriter1_14(Protocol protocol, ItemRewriter.RewriteFunction rewriter) {
    super(protocol, rewriter);
    this.recipeHandlers.put("stonecutting", this::handleStonecutting);
    this.recipeHandlers.put("blasting", this::handleSmelting);
    this.recipeHandlers.put("smoking", this::handleSmelting);
    this.recipeHandlers.put("campfire_cooking", this::handleSmelting);
  }
  
  public void handleStonecutting(PacketWrapper wrapper) throws Exception {
    wrapper.passthrough(Type.STRING);
    Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
    for (Item item : items)
      this.rewriter.rewrite(item); 
    this.rewriter.rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_14to1_13_2\data\RecipeRewriter1_14.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */