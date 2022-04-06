package us.myles.ViaVersion.protocols.protocol1_13to1_12_2.data;

import us.myles.ViaVersion.api.PacketWrapper;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.protocol.Protocol;
import us.myles.ViaVersion.api.rewriters.ItemRewriter;
import us.myles.ViaVersion.api.rewriters.RecipeRewriter;
import us.myles.ViaVersion.api.type.Type;

public class RecipeRewriter1_13_2 extends RecipeRewriter {
  public RecipeRewriter1_13_2(Protocol protocol, ItemRewriter.RewriteFunction rewriter) {
    super(protocol, rewriter);
    this.recipeHandlers.put("crafting_shapeless", this::handleCraftingShapeless);
    this.recipeHandlers.put("crafting_shaped", this::handleCraftingShaped);
    this.recipeHandlers.put("smelting", this::handleSmelting);
  }
  
  public void handleSmelting(PacketWrapper wrapper) throws Exception {
    wrapper.passthrough(Type.STRING);
    Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
    for (Item item : items)
      this.rewriter.rewrite(item); 
    this.rewriter.rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
    wrapper.passthrough((Type)Type.FLOAT);
    wrapper.passthrough((Type)Type.VAR_INT);
  }
  
  public void handleCraftingShaped(PacketWrapper wrapper) throws Exception {
    int ingredientsNo = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue() * ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
    wrapper.passthrough(Type.STRING);
    for (int j = 0; j < ingredientsNo; j++) {
      Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
      for (Item item : items)
        this.rewriter.rewrite(item); 
    } 
    this.rewriter.rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
  
  public void handleCraftingShapeless(PacketWrapper wrapper) throws Exception {
    wrapper.passthrough(Type.STRING);
    int ingredientsNo = ((Integer)wrapper.passthrough((Type)Type.VAR_INT)).intValue();
    for (int j = 0; j < ingredientsNo; j++) {
      Item[] items = (Item[])wrapper.passthrough(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT);
      for (Item item : items)
        this.rewriter.rewrite(item); 
    } 
    this.rewriter.rewrite((Item)wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\protocols\protocol1_13to1_12_2\data\RecipeRewriter1_13_2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */