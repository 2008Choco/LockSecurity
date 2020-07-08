package wtf.choco.locksecurity.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Enums;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

import wtf.choco.locksecurity.api.key.KeyFlag;
import wtf.choco.locksecurity.key.KeyFactory;
import wtf.choco.locksecurity.util.LSConstants;

public final class CommandEditKey implements TabExecutor {

    // editkey <get|set> <flag> <value>

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players are able to edit keys.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Missing argument. " + ChatColor.YELLOW + "/" + label + " <get|set>");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!KeyFactory.SMITHED.isKey(item)) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "You must be holding a " + ChatColor.ITALIC + "Smithed Key " + ChatColor.GRAY + "in your hand.");
            return true;
        }

        if (args[0].equalsIgnoreCase("get")) {
            if (args.length >= 2) {
                Optional<KeyFlag> optionalFlag = Enums.getIfPresent(KeyFlag.class, args[1].toUpperCase()).toJavaUtil();
                if (!optionalFlag.isPresent()) {
                    sender.sendMessage(LSConstants.WARNING_PREFIX + "Unknown key flag, \"" + ChatColor.YELLOW + args[1] + ChatColor.GRAY + "\". Could not get value.");
                    return true;
                }

                KeyFlag flag = optionalFlag.get();
                sender.sendMessage(ChatColor.GRAY + WordUtils.capitalize(flag.name().toLowerCase().replace("_", " ")) + ": " + (KeyFactory.SMITHED.hasFlag(item, flag) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
            }

            // List all flags
            else {
                Set<KeyFlag> flags = KeyFactory.SMITHED.getFlags(item);
                for (KeyFlag flag : KeyFlag.values()) {
                    sender.sendMessage(ChatColor.GRAY + WordUtils.capitalize(flag.name().toLowerCase().replace("_", " ")) + ": " + (flags.contains(flag) ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                }
            }
        }

        else if (args[0].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sender.sendMessage(LSConstants.WARNING_PREFIX + "Missing argument. " + ChatColor.YELLOW + "/" + label + " set " + (args.length == 2 ? "<flag>" : args[1]) + " <value>");
                return true;
            }

            Optional<KeyFlag> optionalFlag = Enums.getIfPresent(KeyFlag.class, args[1].toUpperCase()).toJavaUtil();
            if (!optionalFlag.isPresent()) {
                sender.sendMessage(LSConstants.WARNING_PREFIX + "Unknown key flag, \"" + ChatColor.YELLOW + args[1] + ChatColor.GRAY + "\". Could not set.");
                return true;
            }

            KeyFlag flag = optionalFlag.get();
            boolean newState = Boolean.valueOf(args[2]);
            boolean current = KeyFactory.SMITHED.hasFlag(item, flag);

            if (newState == current) {
                sender.sendMessage(ChatColor.GRAY + "Key is unchanged. Flag " + ChatColor.YELLOW + flag + ChatColor.GRAY + " is already set to " + (newState ? ChatColor.GREEN : ChatColor.RED) + newState + ChatColor.GRAY + ".");
                return true;
            }

            ItemStack modified = KeyFactory.SMITHED.modify(item).withFlag(flag, newState).build(item.getAmount());
            player.getInventory().setItemInMainHand(modified);
            sender.sendMessage(ChatColor.GRAY + "Set the flag " + ChatColor.YELLOW + flag + ChatColor.GRAY + " to " + (newState ? ChatColor.GREEN : ChatColor.RED) + newState + ChatColor.GRAY + ".");
        }

        else {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Unrecognized argument, \"" + ChatColor.RED + args[0] + ChatColor.GRAY + "\". " + ChatColor.YELLOW + "/" + label + " <get|set>");
        }

        return true;
    }

    private static final List<String> ARGS_0_SUGGESTIONS = Arrays.asList("get", "set");
    private static final List<String> KEY_FLAG_SUGGESTIONS = Arrays.stream(KeyFlag.values()).map(f -> f.name().toLowerCase()).collect(Collectors.toList());
    private static final List<String> ARGS_3_SUGGESTIONS = Arrays.asList("true", "false");

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], ARGS_0_SUGGESTIONS, new ArrayList<>());
        }

        else if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], KEY_FLAG_SUGGESTIONS, new ArrayList<>());
        }

        else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return StringUtil.copyPartialMatches(args[2], ARGS_3_SUGGESTIONS, new ArrayList<>());
        }

        return Collections.emptyList();
    }

}
