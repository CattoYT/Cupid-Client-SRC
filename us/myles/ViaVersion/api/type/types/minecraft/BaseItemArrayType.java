package us.myles.ViaVersion.api.type.types.minecraft;

import us.myles.ViaVersion.api.minecraft.item.Item;
import us.myles.ViaVersion.api.type.Type;

public abstract class BaseItemArrayType extends Type<Item[]> {
  public BaseItemArrayType() {
    super(Item[].class);
  }
  
  public BaseItemArrayType(String typeName) {
    super(typeName, Item[].class);
  }
  
  public Class<? extends Type> getBaseClass() {
    return (Class)BaseItemArrayType.class;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\BaseItemArrayType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */