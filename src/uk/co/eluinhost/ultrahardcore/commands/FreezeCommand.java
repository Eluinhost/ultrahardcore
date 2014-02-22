package uk.co.eluinhost.ultrahardcore.commands;

import java.util.AbstractList;
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

//TODO i don't like this, clean up
public class FreezeCommand implements UHCCommand {

    private static final AbstractList<PotionEffect> POTION_EFFECTS = new ArrayList<PotionEffect>();
    private boolean m_isActive;

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
                    //TODO clean up this bit
                    UltraHardcore.getInstance().getLogger().warning("Effect \"" + Arrays.toString(effect) + "\" has invalid potion type id \"" + effect[1] + "\"");
                    continue;
                }
            } catch (Exception ignored) {
                UltraHardcore.getInstance().getLogger().warning("Effect \"" + Arrays.toString(effect) + "\" has invalid potion type id \"" + effect[1] + "\"");
                continue;
            }
            int tier;
            try {
                tier = Integer.parseInt(effect[1]);
            } catch (Exception ignored) {
                UltraHardcore.getInstance().getLogger().warning("Effect \"" + Arrays.toString(effect) + "\" has invalid tier \"" + effect[1] + "\"");
                continue;
            }
            POTION_EFFECTS.add(new PotionEffect(type, ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FREEZE_TIME), tier));
            UltraHardcore.getInstance().getLogger().info("Added potion effect " + type.getName() + " tier " + tier);
        }
        Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance()
                , new FreezeJob()
                , 0
                , ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FREEZE_REAPPLY_TIME));
    }

    private class FreezeJob implements Runnable {
        @Override
        public void run() {
            if (m_isActive) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.hasPermission(PermissionNodes.ANTIFREEZE)) {
                        for (PotionEffect pot : POTION_EFFECTS) {
                            p.addPotionEffect(pot, true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label,
                             String[] args) {
        if ("freeze".equals(command.getName())) {
            if (!sender.hasPermission(PermissionNodes.FREEZE_PERMISSION)) {
                sender.sendMessage(ChatColor.RED + "You don't have the permission " + PermissionNodes.FREEZE_PERMISSION);
                return true;
            }
            m_isActive = !m_isActive;
            Bukkit.broadcastMessage(ChatColor.GOLD + "All players now " + (m_isActive ? "frozen." : "unfrozen."));
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<String>();
    }
}
