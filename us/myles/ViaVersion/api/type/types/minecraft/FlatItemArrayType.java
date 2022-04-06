package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.Type;

public class FlatItemArrayType extends BaseItemArrayType {
  public FlatItemArrayType() {
    super("Flat Item Array");
  }
  
  public Item[] read(ByteBuf buffer) throws Exception {
    int amount = Type.SHORT.readPrimitive(buffer);
    Item[] array = new Item[amount];
    for (int i = 0; i < amount; i++)
      array[i] = (Item)Type.FLAT_ITEM.read(buffer); 
    return array;
  }
  
  public void write(ByteBuf buffer, Item[] object) throws Exception {
    Type.SHORT.writePrimitive(buffer, (short)object.length);
    for (Item o : object)
      Type.FLAT_ITEM.write(buffer, o); 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\FlatItemArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */