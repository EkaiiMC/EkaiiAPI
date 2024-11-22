package fr.ekaiimc.ekaiiapi.api.routing;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.javalin.http.Handler;
import io.papermc.paper.ban.BanListType;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.time.Instant;
import java.util.stream.Collectors;

public class ServerController {

  public static Handler pingServer = ctx -> {
    ctx.json(new ServerPingResponse(200, "OK", true, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()));
  };

  public static Handler stopServer = ctx -> {
    Bukkit.shutdown();
    ctx.json(new ServerCommandResponse(200, "OK", "stop"));
  };

  public static Handler getPlayerList = ctx -> {
    HashSet<ServerPlayer> players = Bukkit.getOnlinePlayers().stream().map(player -> new ServerPlayer(player.getName(), player.getUniqueId(), Instant.ofEpochMilli(player.getLastLogin()).toString())).collect(Collectors.toCollection(HashSet::new));
    ctx.json(new ServerPlayerResponse(200, "OK", players));
  };

  public static Handler getBannedPlayers = ctx -> {
    Set<BannedServerPlayer> players = Bukkit.getBanList(BanListType.PROFILE).getEntries().stream().map(entry -> new BannedServerPlayer(((PlayerProfile) entry.getBanTarget()).getName(), ((PlayerProfile) entry.getBanTarget()).getId(), entry.getReason(), entry.getCreated().toInstant().toString(), entry.getSource())).collect(Collectors.toCollection(HashSet::new));
    ctx.json(new BannedServerPlayerResponse(200, "OK", players));
  };

  public record ServerPlayer(String name, UUID uuid, String lastLogin) {}

  public record BannedServerPlayer(String name, UUID uuid, String reason, String date, String source) {}

  public record ServerPlayerResponse(int status, String statusText, Set<ServerPlayer> players) {}

  public record BannedServerPlayerResponse(int status, String statusText, Set<BannedServerPlayer> players) {}

  public record ServerPingResponse(int status, String statusText, boolean isOnline, int onlinePlayers, int maxPlayers) {}

  public record ServerCommandResponse(int status, String statusText, String command) {}
}
