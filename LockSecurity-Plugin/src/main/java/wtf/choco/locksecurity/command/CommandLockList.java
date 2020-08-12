package wtf.choco.locksecurity.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.block.LockedBlock;
import wtf.choco.locksecurity.util.LSConstants;

public final class CommandLockList implements TabExecutor {

    // locklist [player]

    private final LockSecurity plugin;

    public CommandLockList(LockSecurity plugin) {
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
            sender.sendMessage(LSConstants.WARNING_PREFIX + "You must specify a player when running this command from the console.");
            return false;
        }

        if (targets.isEmpty()) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Invalid selection of entities (" + ChatColor.YELLOW + args[0] + ChatColor.GRAY + "). Only " + ChatColor.AQUA + "players " + ChatColor.GRAY + "are supported. Are they online?");
            return true;
        }

        if (targets.size() > 1) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "Only one target may be selected. (" + ChatColor.YELLOW + targets.size() + ChatColor.GRAY + ") have been selected (" + ChatColor.AQUA + args[0] + ChatColor.GRAY + ").");
            return true;
        }

        OfflinePlayer target = (OfflinePlayer) targets.get(0);
        if (target != sender && !sender.hasPermission(LSConstants.LOCKSECURITY_COMMAND_LOCKLIST_OTHER)) {
            sender.sendMessage(LSConstants.WARNING_PREFIX + "You do not have permission to view the locklist of another player");
            return true;
        }

        // Command feedback
        Collection<LockedBlock> ownedBlocks = plugin.getLockedBlockManager().getLockedBlocks(target);
        if (ownedBlocks.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " does not own any blocks");
        }
        else {
            boolean shouldShowTeleportation = (sender instanceof Player && sender.hasPermission(LSConstants.MINECRAFT_COMMAND_TELEPORT));

            sender.sendMessage(ChatColor.YELLOW + target.getName() + ChatColor.GRAY + " owns blocks at the following locations:");
            for (LockedBlock block : ownedBlocks) {
                if (shouldShowTeleportation && block.getWorld() == ((Entity) sender).getWorld()) {
                    sender.spigot().sendMessage(teleportToBlockComponent(sender, block));
                } else {
                    sender.sendMessage(" - (" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") : " + block.getWorld().getName());
                }
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Entity selector completion
        if (args.length == 1) {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            List<String> suggestions = new ArrayList<>(players.size() + 3);
            suggestions.add("@p");
            if (sender.hasPermission(LSConstants.LOCKSECURITY_COMMAND_LOCKLIST_OTHER) || players.size() == 1 /* It will only select them anyways, so why not */) {
                suggestions.add("@a");
                suggestions.add("@r");
                players.forEach(p -> suggestions.add(p.getName()));
            } else if (players.size() > 1) {
                suggestions.add(sender.getName());
            }

            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private TextComponent teleportToBlockComponent(CommandSender sender, LockedBlock block) {
        TextComponent blockHoverComponent = new TextComponent("(" + block.getX() + ", " + block.getY() + ", " + block.getZ() + ") : " + block.getWorld().getName());
        blockHoverComponent.setColor(net.md_5.bungee.api.ChatColor.RESET);
        blockHoverComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to teleport!")));
        blockHoverComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/minecraft:teleport " + sender.getName() + " " + block.getX() + " " + block.getY() + " " + block.getZ()));

        TextComponent component = new TextComponent(" - " + (block.hasNickname() ? block.getNickname() + " " : ""));
        component.addExtra(blockHoverComponent);
        return component;
    }

}
