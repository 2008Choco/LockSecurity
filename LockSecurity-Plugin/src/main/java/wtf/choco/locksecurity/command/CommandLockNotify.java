package wtf.choco.locksecurity.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.player.LockSecurityPlayer;

public final class CommandLockNotify implements TabExecutor {

    private final LockSecurity plugin;

    public CommandLockNotify(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are able to be notified about newly locked blocks.");
            return true;
        }

        LockSecurityPlayer player = plugin.getPlayer((Player) sender);
        player.setLockNotifications(!player.hasLockNotifications());
        sender.sendMessage((player.isIgnoringLocks() ? ChatColor.GREEN : ChatColor.RED) + "You are " + (player.hasLockNotifications() ? "now" : "no longer") + " being notified about newly locked blocks.");
        if (player.hasLockNotifications()) {
            sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "(This will not inform you of blocks that you lock)");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
