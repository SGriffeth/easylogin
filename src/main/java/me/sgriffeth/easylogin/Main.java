package me.sgriffeth.easylogin;

import java.util.logging.Logger;
import java.util.HashMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static final Logger LOGGER = Logger.getLogger("easylogin");
    public static final HashMap<String, Boolean> loggedIn = new HashMap<String, Boolean>();

    

    public void onEnable() {
        getCommand("login").setExecutor(new CommandLogin(this));
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        LOGGER.info("Easy login is enabled");
    }

    public void onDisable() {
        LOGGER.info("Easy login is disabled");
    }
    
}