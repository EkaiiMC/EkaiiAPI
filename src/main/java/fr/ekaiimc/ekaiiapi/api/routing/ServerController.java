package fr.ekaiimc.ekaiiapi.api.routing;

import io.javalin.http.Handler;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
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
    HashSet<ServerPlayer> players = Bukkit.getOnlinePlayers().stream().map(player -> new ServerPlayer(player.getName(), player.getUniqueId())).collect(Collectors.toCollection(HashSet::new));
    Bukkit.getLogger().info(players.toString());
    ctx.json(new ServerPlayerResponse(200, "OK", players));
  };

  public record ServerPlayer(String name, UUID uuid) {}

  public record ServerPlayerResponse(int status, String statusText, Set<ServerPlayer> player) {}

  public record ServerPingResponse(int status, String statusText, boolean isOnline, int onlinePlayers, int maxPlayers) {}

  public record ServerCommandResponse(int status, String statusText, String command) {}
}
