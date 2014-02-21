package uk.co.eluinhost.ultrahardcore.features.core;

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

import uk.co.eluinhost.ultrahardcore.UltraHardcore;
import uk.co.eluinhost.ultrahardcore.config.ConfigHandler;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

public class FootprintFeature extends UHCFeature implements Runnable {

    public static final float REGULAR_OFFSET = 1.05F;
    public static final float SNOW_OFFSET = 1.13F;

    public static final int REPEAT_INTERVAL = 40;

    private static final int MAX_RENDER_DISTANCE = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_RENDER_DISTANCE);
    private static final int MAX_RENDER_DISTANCE_SQUARED = MAX_RENDER_DISTANCE * MAX_RENDER_DISTANCE;
    private static final int MIN_RENDER_DISTANCE = ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_MIN_DISTANCE);
    private static final int MIN_DISTANCE_SQUARED = MIN_RENDER_DISTANCE * MIN_RENDER_DISTANCE;

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    private final ArrayList<Footstep> m_footsteps = new ArrayList<Footstep>();
    private int m_jobID = -1;

    public FootprintFeature() {
        super("Footprints");
        setDescription("Leave footprints behind you...");
    }

    //TODO move class
    public static class Footstep {

        private final Location m_location;
        private int m_timeRemaining;
        private final String m_playerName;

        private final PacketContainer m_defaultPacket = PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.WORLD_PARTICLES);

        public Footstep(Location loc, int timeToLast, String name) {
            m_playerName = name;
            m_location = loc;
            m_timeRemaining = timeToLast;
            m_defaultPacket.getStrings().write(0, "footstep");
            m_defaultPacket.getFloat()
                    .write(0, (float) loc.getX())//x
                    .write(1, (float) loc.getY())//y
                    .write(2, (float) loc.getZ())
                    .write(3, 0.0F)//offsetx
                    .write(4, 0.0F)//offsety
                    .write(5, 0.0F)//offsetz
                    .write(6, 1.0F);
            m_defaultPacket.getIntegers().write(0, 1);
        }

        public int getTimeRemaining() {
            return m_timeRemaining;
        }

        public void decrementTimeRemaining() {
            m_timeRemaining -= 1;
        }

        public Location getLocation() {
            return m_location;
        }

        public void sendPackets() {
            for (Player p : Bukkit.getOnlinePlayers()) {
                try {
                    if (p.getLocation().distanceSquared(m_location) < MAX_RENDER_DISTANCE_SQUARED) {
                        PROTOCOL_MANAGER.sendServerPacket(p, m_defaultPacket);
                    }
                } catch (IllegalArgumentException ignored) {
                    //wrong world, doesn't matter
                } catch (InvocationTargetException ignored) {}
            }

        }

        public String getName() {
            return m_playerName;
        }
    }

    @Override
    protected void enableCallback() {
        m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UltraHardcore.getInstance(), this, 1, REPEAT_INTERVAL);
    }

    @Override
    protected void disableCallback() {
        Bukkit.getScheduler().cancelTask(m_jobID);
        m_jobID = -1;
        m_footsteps.clear();
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(PermissionNodes.FOOTPRINTS_FOR_PLAYER) || p.getGameMode() == GameMode.CREATIVE) {
                continue;
            }
            Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType() == Material.AIR) {
                block = block.getRelative(BlockFace.DOWN);
            }
            Location loc = p.getLocation();
            if (block.getType() != Material.AIR && block.getType() != Material.WATER && isClearOfFootprints(loc, MIN_DISTANCE_SQUARED, p.getName())) {
                float offset = REGULAR_OFFSET;
                if (block.getType() == Material.SNOW) {
                    offset = SNOW_OFFSET;
                }
                loc.setY(block.getLocation().getY() + offset);
                Footstep newFootstep = new Footstep(
                    loc,
                    ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.FOOTPRINTS_TIME_TO_LAST) / 2,
                    p.getName()
                );
                newFootstep.sendPackets();
                m_footsteps.add(newFootstep);
            }
        }
        Iterator<Footstep> iterator = m_footsteps.iterator();
        while (iterator.hasNext()) {
            Footstep footstep = iterator.next();
            footstep.decrementTimeRemaining();
            if (footstep.getTimeRemaining() <= 0) {
                iterator.remove();
            } else {
                footstep.sendPackets();
            }
        }
    }

    /**
     * Checks for footprints within the distance of the location for the player name
     * @param loc Location
     * @param distance int
     * @param name String
     * @return boolean true if clear, false if not
     */
    private boolean isClearOfFootprints(Location loc, int distance, String name) {
        for (Footstep footstep : m_footsteps) {
            if (footstep.getName().equals(name)) {
                try {
                    if (footstep.getLocation().distanceSquared(loc) < distance) {
                        return false;
                    }
                } catch (IllegalArgumentException ignored) {/*happens on different dimension*/}
            }
        }
        return true;
    }
}
