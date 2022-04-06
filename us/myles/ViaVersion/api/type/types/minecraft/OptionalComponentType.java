package us.myles.ViaVersion.api.type.types.minecraft;

import io.netty.buffer.ByteBuf;
import us.myles.ViaVersion.api.type.Type;
import us.myles.viaversion.libs.gson.JsonElement;

public class OptionalComponentType extends Type<JsonElement> {
  public OptionalComponentType() {
    super(JsonElement.class);
  }
  
  public JsonElement read(ByteBuf buffer) throws Exception {
    boolean present = buffer.readBoolean();
    return present ? (JsonElement)Type.COMPONENT.read(buffer) : null;
  }
  
  public void write(ByteBuf buffer, JsonElement object) throws Exception {
    if (object == null) {
      buffer.writeBoolean(false);
    } else {
      buffer.writeBoolean(true);
      Type.COMPONENT.write(buffer, object);
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\type\types\minecraft\OptionalComponentType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */