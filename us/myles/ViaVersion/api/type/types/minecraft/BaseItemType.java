package us.myles.ViaVersion.api.type.types.minecraft;

import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.Type;

public abstract class BaseItemType extends Type<Item> {
  public BaseItemType() {
    super(Item.class);
  }
  
  public BaseItemType(String typeName) {
    super(typeName, Item.class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseItemType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\BaseItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */