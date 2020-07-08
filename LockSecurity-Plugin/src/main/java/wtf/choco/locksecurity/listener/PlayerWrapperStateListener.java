package wtf.choco.locksecurity.listener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public final class PlayerWrapperStateListener implements Listener {

    private final LockSecurity plugin;

    public PlayerWrapperStateListener(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        LockSecurityPlayer player = plugin.getPlayer(event.getPlayer());
        UUID uuid = player.getUniqueId();

        File playerDataFile = new File(plugin.getPlayerDataDirectory(), uuid.toString() + ".json");
        if (!playerDataFile.exists()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (BufferedReader reader = new BufferedReader(new FileReader(playerDataFile))) {
                JsonObject object = LockSecurity.GSON.fromJson(reader, JsonObject.class);
                player.read(object);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException | JsonIOException e) {
                this.plugin.getLogger().warning("Could not load player data for player \"" + event.getPlayer().getName() + "\" (" + uuid + "). Deleting...");
                playerDataFile.delete();
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        LockSecurityPlayer player = plugin.getPlayer(event.getPlayer());
        File playerDataFile = new File(plugin.getPlayerDataDirectory(), player.getUniqueId().toString() + ".json");

        this.plugin.getPlayerDataDirectory().mkdirs();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerDataFile))) {
                writer.write(LockSecurity.GSON.toJson(player.write(new JsonObject())));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}
