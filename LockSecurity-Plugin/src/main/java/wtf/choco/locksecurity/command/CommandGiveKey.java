package wtf.choco.locksecurity.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.util.LSConstants;

public final class CommandGiveKey implements TabExecutor {

    // givekey [player] [amount] [position]

    private final LockSecurity plugin;

    public CommandGiveKey(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Entity selection
        List<Entity> targets = (sender instanceof Entity) ? Arrays.asList((Entity) sender) : Collections.emptyList();
        if (args.length >= 1) {
            targets = Bukkit.selectEntities(sender, args[0]);
            targets.removeIf(e -> e.getType() != EntityType.PLAYER);
        }
        else if (!(sender instanceof Player)) {
            sender.sendMessage("You must specify a player when running this command from the console");
            return false;
        }

        if (targets.isEmpty()) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Invalid selection of entities (" + ChatColor.YELLOW + args[0] + ChatColor.GRAY + "). Only players are supported. Are they online?");
            return true;
        }

        // Key item generation
        int amount = (args.length >= 2) ? NumberUtils.toInt(args[1], 1) : 1;
        ItemStack keyItem = null;

        // ... specifying block position arguments (x y z world) for a smithed key
        if (args.length >= 3) {
            if (args.length < 5 || !NumberUtils.isNumber(args[2]) || !NumberUtils.isNumber(args[3]) || !NumberUtils.isNumber(args[4])) {
                sender.sendMessage(LSConstants.WARNING_PREFIX + "A complete, valid set of " + ChatColor.YELLOW + "coordinates " + ChatColor.GRAY + "must be provided.");
                return true;
            }

            World world = (sender instanceof Entity) ? ((Entity) sender).getWorld() : null;
            if (args.length >= 6) {
                world = Bukkit.getWorld(args[5]);
            }

            if (world == null) {
                sender.sendMessage(LSConstants.WARNING_PREFIX + "Invalid or unknown world name, " + ChatColor.AQUA + args[5]);
                return true;
            }

            int x = NumberUtils.toInt(args[2]);
            int y = NumberUtils.toInt(args[3]);
            int z = NumberUtils.toInt(args[4]);

            LockedBlock lockedBlock = plugin.getLockedBlockManager().getLockedBlock(world.getBlockAt(x, y, z));
            if (lockedBlock == null) {
                sender.sendMessage(LSConstants.WARNING_PREFIX + "The block at " + ChatColor.YELLOW + "(" + x + ", " + y + ", " + z + ")" + ChatColor.GRAY + " in world " + ChatColor.AQUA + world.getName() + ChatColor.GRAY + " is not locked and cannot be added to a key.");
                return true;
            }

            keyItem = KeyFactory.SMITHED.builder().unlocks(lockedBlock).build(amount);
        }
        else {
            keyItem = KeyFactory.createUnsmithedKey(amount);
        }

        // Giving the key to the targets
        ItemStack keyItemFinal = keyItem; // Stupid lambdas
        targets.forEach(e -> ((Player) e).getInventory().addItem(keyItemFinal));

        // Command feedback
        if (targets.size() > 1) {
            sender.sendMessage(ChatColor.GRAY + "You have given " + ChatColor.YELLOW + targets.size() + ChatColor.GRAY + " players " + ChatColor.AQUA + (amount > 1 ? amount + " keys" : "a key") + ChatColor.GRAY + ".");
            targets.forEach(e -> {
                if (e != sender) {
                    e.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You have been given " + ChatColor.YELLOW + ChatColor.ITALIC + (amount > 1 ? amount + " keys" : "a key") + ChatColor.GRAY + ChatColor.ITALIC + " from " + ChatColor.AQUA + ChatColor.ITALIC + sender.getName() + ChatColor.GRAY + ".");
                }
            });
        }
        else {
            Entity target = targets.get(0);
            boolean self = (target == sender);

            target.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "You have " + (self ? "given " + ChatColor.AQUA + ChatColor.ITALIC + "yourself " : "been given ") + ChatColor.YELLOW + ChatColor.ITALIC + (amount > 1 ? amount + " keys" : "a key") + ChatColor.GRAY + ChatColor.ITALIC + (self ? "" : " from " + ChatColor.AQUA + ChatColor.ITALIC + sender.getName()) + ChatColor.GRAY + ".");
            if (!self) {
                sender.sendMessage(ChatColor.GRAY + "You have given " + ChatColor.YELLOW + (amount > 1 ? amount + " keys" : "a key") + ChatColor.GRAY + " to " + ChatColor.AQUA + target.getName());
            }
        }

        return true;
    }

    private static final List<String> AMOUNT_SUGGESTION = Arrays.asList("[amount]", "1", "64", "128");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Entity selector completion
        if (args.length == 1) {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            List<String> suggestions = new ArrayList<>(players.size() + 3);
            suggestions.add("@p");
            suggestions.add("@a");
            suggestions.add("@r");
            players.forEach(p -> suggestions.add(p.getName()));

            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        // Key amount completion
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], AMOUNT_SUGGESTION, new ArrayList<>());
        }

        // Target block completion
        if (args.length >= 3 && sender instanceof Player) {
            Player player = (Player) sender;
            Block target = player.getTargetBlockExact(6);

            if (target == null) {
                return Collections.emptyList();
            }

            // Switch statements please :((
            switch (args.length) {
                case 3: return StringUtil.copyPartialMatches(args[2], Arrays.asList(String.valueOf(target.getX())), new ArrayList<>());
                case 4: return StringUtil.copyPartialMatches(args[3], Arrays.asList(String.valueOf(target.getY())), new ArrayList<>());
                case 5: return StringUtil.copyPartialMatches(args[4], Arrays.asList(String.valueOf(target.getZ())), new ArrayList<>());
                case 6: return StringUtil.copyPartialMatches(args[5], Arrays.asList(target.getWorld().getName()), new ArrayList<>());
            }
        }

        return Collections.emptyList();
    }

}
