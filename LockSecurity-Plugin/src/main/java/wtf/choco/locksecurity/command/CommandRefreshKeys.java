package wtf.choco.locksecurity.command;

import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import wtf.choco.locksecurity.key.KeyFactory;

public final class CommandRefreshKeys implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to run this command. You don't have an inventory, silly!");
            return true;
        }

        Player player = (Player) sender;
        PlayerInventory inventory = player.getInventory();
        int refreshed = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (!KeyFactory.SMITHED.isKey(item)) {
                continue;
            }

            ItemStack newItem = KeyFactory.SMITHED.refresh(item);
            inventory.setItem(i, newItem);

            if (!item.isSimilar(newItem)) {
                refreshed++;
            }
        }

        if (refreshed != 0) {
            player.sendMessage(ChatColor.YELLOW.toString() + refreshed + " key" + (refreshed > 1 ? "s" : "") + ChatColor.GRAY + " in your inventory have been refreshed.");
        } else {
            player.sendMessage(ChatColor.GRAY + "There were " + ChatColor.YELLOW + "no keys " + ChatColor.GRAY + "in your inventory that needed refreshing");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }

}
