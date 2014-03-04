package uk.co.eluinhost.ultrahardcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import uk.co.eluinhost.features.exceptions.FeatureIDNotFoundException;
import uk.co.eluinhost.ultrahardcore.features.deathbans.DeathBansFeature;
import uk.co.eluinhost.features.FeatureManager;

public class DeathBanCommand {

    private static final String SYNTAX = ChatColor.RED + "Syntax: /deathban ban <playername> <time> OR /deathban unban <playername>";
    private static final String BAN_SYNTAX = ChatColor.RED + "Syntax: /deathban ban <playername> [time]";
    private static final String UNBAN_SYNTAX = ChatColor.RED + "Syntax: /deathban unban <playername>";

    public static final String DEATH_BAN_BAN = "UHC.deathban.unban";
    public static final String DEATH_BAN_UNBAN = "UHC.deathban.ban";

    @Override
    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        if ("deathban".equals(command.getName())) {
            if (args.length < 1) {
                sender.sendMessage(SYNTAX);
                return true;
            }
            DeathBansFeature dbf;
            try {
                dbf = (DeathBansFeature) FeatureManager.getInstance().getFeatureByID("DeathBans");
            } catch (FeatureIDNotFoundException ignored) {
                sender.sendMessage(ChatColor.RED + "Module DeathBans is not loaded!");
                return true;
            }
            if ("ban".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(DEATH_BAN_BAN)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission");
                    return true;
                }
                if (args.length != 3) {
                    sender.sendMessage(BAN_SYNTAX);
                    return true;
                }
                String playername = args[1];
                String time = args[2];
                long duration = DeathBansFeature.parseBanTime(time);
                dbf.banPlayer(Bukkit.getOfflinePlayer(playername), "You are under a death ban, you will be unbanned in %timeleft", duration);
                sender.sendMessage(ChatColor.GOLD + "Banned player " + playername + " for " + DeathBansFeature.formatTimeLeft(System.currentTimeMillis() + duration));
                return true;
            } else if ("unban".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission(DEATH_BAN_UNBAN)) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission");
                    return true;
                }
                if (args.length != 2) {
                    sender.sendMessage(UNBAN_SYNTAX);
                    return true;
                }
                String playername = args[1];
                int amount = dbf.removeBan(playername);
                sender.sendMessage(ChatColor.GOLD + "Removed " + amount + " bans for player " + playername);
                return true;
            } else {
                sender.sendMessage(SYNTAX);
                return true;
            }
        }
        return false;
    }
}
