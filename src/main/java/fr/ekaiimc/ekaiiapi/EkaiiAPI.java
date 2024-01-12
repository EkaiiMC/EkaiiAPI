package fr.ekaiimc.ekaiiapi;

import fr.ekaiimc.ekaiiapi.api.WebServer;
import org.bukkit.plugin.java.JavaPlugin;

public final class EkaiiAPI extends JavaPlugin {

  private WebServer webServer;

  @Override
  public void onEnable() {
    saveResource("config.yml", false);
    saveDefaultConfig();

    getConfig().options().copyDefaults(true);

    this.getLogger().info("EkaiiAPI is now enabled !");

    webServer = new WebServer(getConfig().getInt("server_port"), getConfig().getString("api_token"));
    webServer.start();
  }

  @Override
  public void onDisable() {
    webServer.stop();
    this.getLogger().info("EkaiiAPI is now disabled !");
  }
}
