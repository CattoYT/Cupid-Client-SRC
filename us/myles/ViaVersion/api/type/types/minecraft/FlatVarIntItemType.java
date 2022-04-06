package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.Type;
import us.myles.viaversion.libs.opennbt.tag.builtin.CompoundTag;

public class FlatVarIntItemType extends BaseItemType {
  public FlatVarIntItemType() {
    super("FlatVarIntItem");
  }
  
  public Item read(ByteBuf buffer) throws Exception {
    boolean present = buffer.readBoolean();
    if (!present)
      return null; 
    Item item = new Item();
    item.setIdentifier(Type.VAR_INT.readPrimitive(buffer));
    item.setAmount(buffer.readByte());
    item.setTag((CompoundTag)Type.NBT.read(buffer));
    return item;
  }
  
  public void write(ByteBuf buffer, Item object) throws Exception {
    if (object == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      Type.VAR_INT.writePrimitive(buffer, object.getIdentifier());
      buffer.writeByte(object.getAmount());
      Type.NBT.write(buffer, object.getTag());
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\FlatVarIntItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */