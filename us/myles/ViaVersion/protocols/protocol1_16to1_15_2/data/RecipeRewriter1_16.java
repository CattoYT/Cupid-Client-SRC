package us.myles.ViaVersion.protocols.protocol1_16to1_15_2.data;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.type.Type;
import us.myles.ViaVersion.protocols.protocol1_14to1_13_2.data.RecipeRewriter1_14;

public class RecipeRewriter1_16 extends RecipeRewriter1_14 {
  public RecipeRewriter1_16(Protocol protocol, ItemRewriter.RewriteFunction rewriter) {
    super(protocol, rewriter);
    this.recipeHandlers.put("smithing", this::handleSmithing);
  }
  
  public void handleSmithing(PacketWrapper wrapper) throws Exception {
    Item[] baseIngredients = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
    for (Item item : baseIngredients)
      this.rewriter.rewrite(item); 
    Item[] ingredients = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
    for (Item item : ingredients)
      this.rewriter.rewrite(item); 
    this.rewriter.rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_16to1_15_2\data\RecipeRewriter1_16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */