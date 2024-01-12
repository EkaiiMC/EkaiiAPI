package fr.ekaiimc.ekaiiapi.api;

import fr.ekaiimc.ekaiiapi.api.routing.ServerController;
import fr.ekaiimc.ekaiiapi.api.routing.WhitelistController;
import io.javalin.Javalin;
import io.javalin.http.HttpResponseException;

import java.util.Objects;

import static io.javalin.apibuilder.ApiBuilder.*;

public class WebServer {

  private static WebServer instance;

  private Javalin javalin;
  private final int port;
  private final String apiToken;

  public static WebServer getInstance() {
    return instance;
  }

  public WebServer(int port, String apiToken) {
    instance = this;
    this.port = port;
    this.apiToken = apiToken;
  }

  public void start() {
    javalin = Javalin.create();

    javalin.before(ctx -> {
      ctx.header("Content-Type", "application/json");
      if (!Objects.equals(ctx.header("Authorization"), "Bearer " + apiToken)) {
        ctx.header("WWW-Authenticate", "Basic realm=\"EkaiiAPI\"");
        throw new HttpResponseException(401, "Unauthorized");
      }
    });

    javalin.routes(() -> {
      path("whitelist", () -> {
        path("{uuid}", () -> {
          post(WhitelistController.addPlayerToWhitelist);
          delete(WhitelistController.removePlayerFromWhitelist);
          get(WhitelistController.isPlayerWhitelisted);
        });
        get(WhitelistController.getWhitelistedPlayers);
      });

      path("server", () -> {
        get("ping", ServerController.pingServer);
        get("stop", ServerController.stopServer);
        get("players", ServerController.getPlayerList);
      });
    });

    javalin.start(port);
  }

  public void stop() {
    javalin.stop();
  }
}
