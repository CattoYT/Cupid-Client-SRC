package us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content;

import java.util.Arrays;
import us.myles.viaversion.libs.bungeecordchat.api.chat.BaseComponent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.HoverEvent;

public class Text extends Content {
  private final Object value;
  
  public String toString() {
    return "Text(value=" + getValue() + ")";
  }
  
  public Object getValue() {
    return this.value;
  }
  
  public Text(BaseComponent[] value) {
    this.value = value;
  }
  
  public Text(String value) {
    this.value = value;
  }
  
  public HoverEvent.Action requiredAction() {
    return HoverEvent.Action.SHOW_TEXT;
  }
  
  public boolean equals(Object o) {
    if (this.value instanceof BaseComponent[])
      return (o instanceof Text && ((Text)o).value instanceof BaseComponent[] && 
        
        Arrays.equals((Object[])this.value, (Object[])((Text)o).value)); 
    return this.value.equals(o);
  }
  
  public int hashCode() {
    return (this.value instanceof BaseComponent[]) ? Arrays.hashCode((Object[])this.value) : this.value.hashCode();
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\api\chat\hover\content\Text.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */