package us.myles.ViaVersion.api.platform.providers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class ViaProviders {
  private final Map<Class<? extends Provider>, Provider> providers = new HashMap<>();
  
  private final List<Class<? extends Provider>> lonelyProviders = new ArrayList<>();
  
  public void require(Class<? extends Provider> provider) {
    this.lonelyProviders.add(provider);
  }
  
  public <T extends Provider> void register(Class<T> provider, T value) {
    this.providers.put(provider, (Provider)value);
  }
  
  public <T extends Provider> void use(Class<T> provider, T value) {
    this.lonelyProviders.remove(provider);
    this.providers.put(provider, (Provider)value);
  }
  
  @Nullable
  public <T extends Provider> T get(Class<T> provider) {
    Provider rawProvider = this.providers.get(provider);
    if (rawProvider != null)
      return (T)rawProvider; 
    if (this.lonelyProviders.contains(provider))
      throw new IllegalStateException("There was no provider for " + provider + ", one is required!"); 
    return null;
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersion\api\platform\providers\ViaProviders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */