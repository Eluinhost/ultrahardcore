package uk.co.eluinhost.UltraHardcore.features.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;

import com.comphenix.protocol.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import uk.co.eluinhost.UltraHardcore.UltraHardcore;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

public class FootprintFeature extends UHCFeature implements Runnable {

    private static int MAX_RENDER_DISTANCE_SQUARED;
    private static ProtocolManager pm = ProtocolLibrary.getProtocolManager();
    private ArrayList<Footstep> footsteps = new ArrayList<Footstep>();
    private static int MIN_DISTANCE_SQUARED;
    private int jobID = -1;

    public FootprintFeature(boolean enabled) {
        super("Footprints", enabled);
        setDescription("Leave footprints behind you...");
        MAX_RENDER_DISTANCE_SQUARED = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_RENDER_DISTANCE);
        MAX_RENDER_DISTANCE_SQUARED *= MAX_RENDER_DISTANCE_SQUARED;
        MIN_DISTANCE_SQUARED = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_MIN_DISTANCE);
        MIN_DISTANCE_SQUARED *= MIN_DISTANCE_SQUARED;
    }

    public class Footstep {

        private Location loc;
        private int timeRemaining;
        private String name;

        private PacketContainer defaultPacket = pm.createPacket(PacketType.Play.Server.WORLD_PARTICLES);

        public Footstep(Location loc, int timeToLast, String name) {
            setName(name);
            setLocation(loc);
            setTimeRemaining(timeToLast);
            defaultPacket.getStrings().write(0, "footstep");
            defaultPacket.getFloat()
                    .write(0, (float) loc.getX())//x
                    .write(1, (float) loc.getY())//y
                    .write(2, (float) loc.getZ())
                    .write(3, 0F)//offsetx
                    .write(4, 0F)//offsety
                    .write(5, 0F)//offsetz
                    .write(6, 1F);
            defaultPacket.getIntegers().write(0, 1);
            sendPackets();
        }

        public int getTimeRemaining() {
            return timeRemaining;
        }

        public void setTimeRemaining(int timeRemaining) {
            this.timeRemaining = timeRemaining;
        }

        public void decrementTimeRemaining() {
            timeRemaining -= 1;
        }

        public Location getLocation() {
            return loc;
        }

        public void setLocation(Location loc) {
            this.loc = loc;
        }

        public void sendPackets() {
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    if (p.getLocation().distanceSquared(loc) < MAX_RENDER_DISTANCE_SQUARED) {
                        pm.sendServerPacket(p, defaultPacket);
                    }
                } catch (IllegalArgumentException ex) {
                    //wrong world, doesn't matter
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @Override
    public void enableFeature() {
        jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), this, 1, 2 * 20);
    }

    @Override
    public void disableFeature() {
        Bukkit.getScheduler().cancelTask(jobID);
        jobID = -1;
        footsteps.clear();
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(PermissionNodes.FOOTPRINTS_FOR_PLAYER) || p.getGameMode().equals(GameMode.CREATIVE)) {
                continue;
            }
            Block b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (b.getType() == Material.AIR) {
                b = b.getRelative(BlockFace.DOWN);
            }
            Location loc = p.getLocation();
            if (b.getType() != Material.AIR && b.getType() != Material.WATER && noneClose(loc, MIN_DISTANCE_SQUARED, p.getName())) {
                float offset = 1.05F;
                if (b.getType() == Material.SNOW) {
                    offset = 1.13F;
                }
                loc.setY(b.getLocation().getY() + offset);
                footsteps.add(new Footstep(loc,
                        ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_TIME_TO_LAST) / 2
                        , p.getName()));
            }
        }
        Iterator<Footstep> i = footsteps.iterator();
        while (i.hasNext()) {
            Footstep f = i.next();
            f.decrementTimeRemaining();
            if (f.getTimeRemaining() <= 0) {
                i.remove();
            } else {
                f.sendPackets();
            }
        }
    }

    private boolean noneClose(Location loc, int distance, String name) {
        for (Footstep f : footsteps) {
            if (f.getName().equals(name)) {
                try {
                    if (f.getLocation().distanceSquared(loc) < distance) {
                        return false;
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
        return true;
    }
}
