package fr.ekaiimc.ekaiiapi.api.routing;

import fr.ekaiimc.ekaiiapi.EkaiiAPI;
import io.javalin.http.Handler;
import io.javalin.http.HttpResponseException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class WhitelistController {

  public static Handler addPlayerToWhitelist = ctx -> {
    UUID playerUuid = null;
    try {
      playerUuid = UUID.fromString(ctx.pathParam("uuid"));
    } catch (Exception e){
      throw new HttpResponseException(400, "Bad Request");
    }
    try {
      (new WhitelistChangeRunner(playerUuid, true)).runTask(JavaPlugin.getPlugin(EkaiiAPI.class));

      ctx.json(new WhitelistAddRemoveGetResponse(200, "OK", playerUuid, true));
    } catch (Exception e) {
      Bukkit.getLogger().severe(e.getMessage());
      throw new HttpResponseException(500, "Internal server error");
    }
  };

  public static Handler removePlayerFromWhitelist = ctx -> {
    UUID playerUuid = null;
    try {
      playerUuid = UUID.fromString(ctx.pathParam("uuid"));
    } catch (Exception e){
      throw new HttpResponseException(400, "Bad Request");
    }
    try {
      (new WhitelistChangeRunner(playerUuid, false)).runTask(JavaPlugin.getPlugin(EkaiiAPI.class));

      ctx.json(new WhitelistAddRemoveGetResponse(200, "OK", playerUuid, false));
    } catch (Exception e) {
      Bukkit.getLogger().severe(e.getMessage());
      throw new HttpResponseException(500, "Internal server error");
    }
  };

  public static Handler isPlayerWhitelisted = ctx -> {
    UUID playerUuid = null;
    try {
      playerUuid = UUID.fromString(ctx.pathParam("uuid"));
    } catch (Exception e) {
      throw new HttpResponseException(400, "Bad Request");
    }
    try {
      ctx.json(new WhitelistAddRemoveGetResponse(200, "OK", playerUuid, Bukkit.getOfflinePlayer(playerUuid).isWhitelisted()));

    } catch (Exception e) {
      Bukkit.getLogger().severe(e.getMessage());
      throw new HttpResponseException(500, "Internal server error");
    }
  };

  public static Handler getWhitelistedPlayers = ctx -> {
    try {
      ctx.json(new WhitelistGetAllResponse(200, "OK", Bukkit.getWhitelistedPlayers().stream().map(player -> player.getUniqueId().toString()).toArray(String[]::new)));
    } catch (Exception e) {
      Bukkit.getLogger().severe(e.getMessage());
      throw new HttpResponseException(500, "Internal server error");
    }
  };

  public record WhitelistAddRemoveGetResponse(int code, String statusText, UUID playerUuid, boolean isWhitelisted) {
  }

  public record WhitelistGetAllResponse(int code, String statusText, String[] whitelistedPlayers) {
  }

  public static class WhitelistChangeRunner extends BukkitRunnable {
    private final UUID playerUuid;
    private final boolean isWhitelisted;

    public WhitelistChangeRunner(UUID playerUuid, boolean isWhitelisted) {
      this.playerUuid = playerUuid;
      this.isWhitelisted = isWhitelisted;
    }

    @Override
    public void run() {
      Bukkit.getOfflinePlayer(playerUuid).setWhitelisted(isWhitelisted);
    }
  }

}
