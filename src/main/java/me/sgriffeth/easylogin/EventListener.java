package me.sgriffeth.easylogin;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.GameMode;

public class EventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.SPECTATOR);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Main.loggedIn.replace(e.getPlayer().getUniqueId().toString(), false);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(Main.loggedIn.get(e.getPlayer().getUniqueId().toString()) == null) {
            e.setCancelled(true);
        } else if(!Main.loggedIn.get(e.getPlayer().getUniqueId().toString())) {
            e.setCancelled(true);
        }
    }
}
