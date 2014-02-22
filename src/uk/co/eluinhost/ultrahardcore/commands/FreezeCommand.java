package uk.co.eluinhost.ultrahardcore.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;

public class FreezeCommand implements UHCCommand {

    private static ArrayList<PotionEffect> effects = new ArrayList<PotionEffect>();
    private static boolean active = false;

    public FreezeCommand() {
        for (String configEffect : ConfigHandler.getConfig(ConfigHandler.MAIN).getStringList(ConfigNodes.FREEZE_EFFECTS)) {
            String[] effect = configEffect.split(":");
            if (effect.length != 2) {
                UltraHardcore.getInstance().getLogger().warning("Effect \"" + configEffect + "\" is invalid");
                continue;
            }
            PotionEffectType type;
            try {
                int typeID = Integer.parseInt(effect[0]);
                type = PotionEffectType.getById(typeID);      //TODO change config file to use names and use getbyname
                if (type == null) {
                    throw new Exception();
                }
            } catch (Exception ex) {
                UltraHardcore.getInstance().getLogger().warning("Effect \"" + Arrays.toString(effect) + "\" has invalid potion type id \"" + effect[1] + "\"");
                continue;
            }
            int tier;
            try {
                tier = Integer.parseInt(effect[1]);
            } catch (Exception ex) {
                UltraHardcore.getInstance().getLogger().warning("Effect \"" + Arrays.toString(effect) + "\" has invalid tier \"" + effect[1] + "\"");
                continue;
            }
            effects.add(new PotionEffect(type, ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FREEZE_TIME), tier));
            UltraHardcore.getInstance().getLogger().info("Added potion effect " + type.getName() + " tier " + tier);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance()
                , new FreezeJob()
                , 0
                , ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FREEZE_REAPPLY_TIME));
    }

    private static class FreezeJob implements Runnable {
        @Override
        public void run() {
            if (isEnabled()) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasPermission(PermissionNodes.ANTIFREEZE)) {
                        for (PotionEffect pot : effects) {
                            p.addPotionEffect(pot, true);
                        }
                    }
                }
            }
        }
    }

    public static boolean isEnabled() {
        return active;
    }

    public static void setEnabled(boolean a) {
        active = a;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        if (command.getName().equals("freeze")) {
            if (!sender.hasPermission(PermissionNodes.FREEZE_PERMISSION)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.FREEZE_PERMISSION);
                return true;
            }
            setEnabled(!isEnabled());
            Bukkit.broadcastMessage(ChatColor.GOLD + "All players now " + (isEnabled() ? "frozen." : "unfrozen."));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command,
                                      String alias, String[] args) {
        return new ArrayList<String>();
    }
}
