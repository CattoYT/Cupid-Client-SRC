package us.myles.ViaVersion.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.util.GsonUtil;
import us.myles.viaversion.libs.bungeecordchat.api.ChatColor;
import us.myles.viaversion.libs.gson.JsonObject;
import us.myles.viaversion.libs.gson.JsonParseException;

public class UpdateUtil {
  public static final String PREFIX = ChatColor.GREEN + "" + ChatColor.BOLD + "[ViaVersion] " + ChatColor.GREEN;
  
  private static final String URL = "https://api.spiget.org/v2/resources/";
  
  private static final int PLUGIN = 19254;
  
  private static final String LATEST_VERSION = "/versions/latest";
  
  public static void sendUpdateMessage(UUID uuid) {
    Via.getPlatform().runAsync(() -> {
          String message = getUpdateMessage(false);
          if (message != null)
            Via.getPlatform().runSync(()); 
        });
  }
  
  public static void sendUpdateMessage() {
    Via.getPlatform().runAsync(() -> {
          String message = getUpdateMessage(true);
          if (message != null)
            Via.getPlatform().runSync(()); 
        });
  }
  
  @Nullable
  private static String getUpdateMessage(boolean console) {
    Version current;
    if (Via.getPlatform().getPluginVersion().equals("${project.version}"))
      return "You are using a debug/custom version, consider updating."; 
    String newestString = getNewestVersion();
    if (newestString == null) {
      if (console)
        return "Could not check for updates, check your connection."; 
      return null;
    } 
    try {
      current = new Version(Via.getPlatform().getPluginVersion());
    } catch (IllegalArgumentException e) {
      return "You are using a custom version, consider updating.";
    } 
    Version newest = new Version(newestString);
    if (current.compareTo(newest) < 0)
      return "There is a newer version available: " + newest.toString() + ", you're on: " + current.toString(); 
    if (console && current.compareTo(newest) != 0) {
      if (current.getTag().toLowerCase(Locale.ROOT).startsWith("dev") || current.getTag().toLowerCase(Locale.ROOT).startsWith("snapshot"))
        return "You are running a development version, please report any bugs to GitHub."; 
      return "You are running a newer version than is released!";
    } 
    return null;
  }
  
  @Nullable
  private static String getNewestVersion() {
    try {
      JsonObject statistics;
      URL url = new URL("https://api.spiget.org/v2/resources/19254/versions/latest?" + System.currentTimeMillis());
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      connection.setUseCaches(true);
      connection.addRequestProperty("User-Agent", "ViaVersion " + Via.getPlatform().getPluginVersion() + " " + Via.getPlatform().getPlatformName());
      connection.setDoOutput(true);
      BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      StringBuilder builder = new StringBuilder();
      String input;
      while ((input = br.readLine()) != null)
        builder.append(input); 
      br.close();
      try {
        statistics = (JsonObject)GsonUtil.getGson().fromJson(builder.toString(), JsonObject.class);
      } catch (JsonParseException e) {
        e.printStackTrace();
        return null;
      } 
      return statistics.get("name").getAsString();
    } catch (MalformedURLException e) {
      return null;
    } catch (IOException e) {
      return null;
    } 
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\ViaVersio\\update\UpdateUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */