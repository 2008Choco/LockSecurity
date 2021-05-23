package wtf.choco.locksecurity.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.util.StringUtil;

import wtf.choco.commons.util.UpdateChecker;
import wtf.choco.commons.util.UpdateChecker.UpdateResult;
import wtf.choco.locksecurity.LockSecurity;
import wtf.choco.locksecurity.util.LSConstants;

public final class CommandLockSecurity implements TabExecutor {

    private final LockSecurity plugin;

    public CommandLockSecurity(LockSecurity plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("info")) {
            PluginDescriptionFile description = plugin.getDescription();

            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------------------------------");
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GRAY + "Version: " + ChatColor.YELLOW + description.getVersion());
            sender.sendMessage(ChatColor.GRAY + "Developer: " + ChatColor.YELLOW + description.getAuthors().get(0));
            sender.sendMessage(ChatColor.GRAY + "Plugin Page: " + ChatColor.YELLOW + "https://www.spigotmc.org/resources/12650/");
            sender.sendMessage(ChatColor.GRAY + "Report bugs to: " + ChatColor.YELLOW + "https://github.com/2008Choco/LockSecurity/issues/");

            UpdateResult result = UpdateChecker.get().getLastResult();
            if (result != null && sender.hasPermission(LSConstants.LOCKSECURITY_NOTIFYUPDATE)) {
                switch (result.getReason()) {
                    case NEW_UPDATE:
                        sender.sendMessage(ChatColor.AQUA + "New version available: " + ChatColor.GREEN + ChatColor.BOLD + result.getNewestVersion());
                        break;
                    case UNRELEASED_VERSION:
                        sender.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "[WARNING] " + ChatColor.GRAY + "You are on a development build!");
                        break;
                    default:
                        break;
                }
            }

            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + ChatColor.STRIKETHROUGH + "--------------------------------------------");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission(LSConstants.LOCKSECURITY_COMMAND_RELOAD)) {
                sender.sendMessage(ChatColor.RED + "You have insufficient privileges to run this command");
                return true;
            }

            this.plugin.reloadConfig();
            this.plugin.reloadLockableBlocks();
            sender.sendMessage(ChatColor.GREEN + "Plugin successfully reloaded");
            return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            suggestions.add("version");
            suggestions.add("info");
            if (sender.hasPermission(LSConstants.LOCKSECURITY_COMMAND_RELOAD)) {
                suggestions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

}
