package us.myles.viaversion.libs.bungeecordchat.api.chat.hover.content;

import us.myles.viaversion.libs.bungeecordchat.api.chat.HoverEvent;
import us.myles.viaversion.libs.bungeecordchat.api.chat.ItemTag;

public class Item extends Content {
  private String id;
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setCount(int count) {
    this.count = count;
  }
  
  public void setTag(ItemTag tag) {
    this.tag = tag;
  }
  
  public Item(String id, int count, ItemTag tag) {
    this.id = id;
    this.count = count;
    this.tag = tag;
  }
  
  public String toString() {
    return "Item(id=" + getId() + ", count=" + getCount() + ", tag=" + getTag() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Item))
      return false; 
    Item other = (Item)o;
    if (!other.canEqual(this))
      return false; 
    Object this$id = getId(), other$id = other.getId();
    if ((this$id == null) ? (other$id != null) : !this$id.equals(other$id))
      return false; 
    if (getCount() != other.getCount())
      return false; 
    Object this$tag = getTag(), other$tag = other.getTag();
    return !((this$tag == null) ? (other$tag != null) : !this$tag.equals(other$tag));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Item;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $id = getId();
    result = result * 59 + (($id == null) ? 43 : $id.hashCode());
    result = result * 59 + getCount();
    Object $tag = getTag();
    return result * 59 + (($tag == null) ? 43 : $tag.hashCode());
  }
  
  public String getId() {
    return this.id;
  }
  
  private int count = -1;
  
  private ItemTag tag;
  
  public int getCount() {
    return this.count;
  }
  
  public ItemTag getTag() {
    return this.tag;
  }
  
  public HoverEvent.Action requiredAction() {
    return HoverEvent.Action.SHOW_ITEM;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\bungeecordchat\api\chat\hover\content\Item.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */