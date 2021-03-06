package events.brainsynder.key;

import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class GameMaker implements Game {
    private boolean started = false;
    protected boolean endTask = false;
    
    @Override public void onEnd() {
        started = false;
        unregisterListeners();
        plugin.getEventMain().end();
        for (IGamePlayer player : players) {
            if (deadPlayers.contains(player)) continue;
            if (player.getPlayerData().isStored())
                player.getPlayerData().restoreData();
            player.setGame(null);
        }
        deadPlayers.clear();
        players.clear();
    }
    
    @Override public void onWin(IGamePlayer gamePlayer) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&b{PLAYER} &7just won &b" + getName() + '!').replace("{PLAYER}", gamePlayer.getPlayer().getName()));
    }
    
    @Override public void lost(IGamePlayer player) {
        if (player.getPlayerData().isStored())
            player.getPlayerData().restoreData();
        player.setGame(null);
        deadPlayers.add(player);
        //players.remove(player);
    }
    
    @Override public void onStart() {
        started = true;
        plugin.getEventMain().eventstarting = false;
        plugin.getEventMain().eventstarted = true;
        plugin.getEventMain().waiting = null;
        players.forEach(player ->
                player.getPlayer().setGameMode(GameMode.ADVENTURE));
        new BukkitRunnable() {
            @Override public void run() {
                if (!plugin.getEventMain().eventstarted) {
                    cancel();
                    return;
                }
                if (!started) {
                    cancel();
                    return;
                }
                
                if (players.size() <= 1) {
                    cancel();
                    return;
                }
                
                if (endTask) {
                    cancel();
                    return;
                }
                
                perTick();
            }
        }.runTaskTimer(plugin, 0, 1);
    }
    
    @Override public void perTick() {
    }
    
    @Override public boolean hasStarted() {
        return started;
    }
    
    @Override public void setStarted(boolean started) {
        this.started = started;
    }
    
    @Override public boolean isSetup() {
        return (settings.getData().isSet("setup." + getName() + ".world"));
    }
}
