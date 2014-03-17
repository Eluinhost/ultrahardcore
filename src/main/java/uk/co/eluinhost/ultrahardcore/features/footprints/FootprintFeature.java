package uk.co.eluinhost.ultrahardcore.features.footprints;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import uk.co.eluinhost.configuration.ConfigManager;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FootprintFeature extends UHCFeature implements Runnable {

    public static final float REGULAR_OFFSET = 1.05F;
    public static final float SNOW_OFFSET = 1.13F;

    public static final int REPEAT_INTERVAL = 40;

    private final int m_maxRenderSquared;
    private final int m_minRenderSquared;
    private final int m_timeToLast;

    private static final ProtocolManager PROTOCOL_MANAGER = ProtocolLibrary.getProtocolManager();

    private final PacketContainer m_defaultPacket = PROTOCOL_MANAGER.createPacket(PacketType.Play.Server.WORLD_PARTICLES);

    public static final String FOOTPRINTS_FOR_PLAYER = BASE_PERMISSION + "footprints.leavePrints";

    private final Collection<Footstep> m_footsteps = new ArrayList<Footstep>();
    private int m_jobID = -1;

    /**
     * Leave 'footprints' behind you, requires protocollib
     */
    public FootprintFeature(Plugin plugin, ConfigManager configManager) {
        super(plugin, "Footprints","Leave footprints behind you...", configManager);
        FileConfiguration config = configManager.getConfig();
        int maxRender = config.getInt(getBaseConfig()+"maxdistance");
        int minRender = config.getInt(getBaseConfig()+"mindistance");
        m_maxRenderSquared = maxRender * maxRender;
        m_minRenderSquared = minRender * minRender;
        m_timeToLast = config.getInt(getBaseConfig()+"time");
    }

    @Override
    protected void enableCallback() {
        m_jobID = Bukkit.getScheduler().scheduleSyncRepeatingTask(getPlugin(), this, 1, REPEAT_INTERVAL);
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
            if (!p.hasPermission(FOOTPRINTS_FOR_PLAYER) || p.getGameMode() == GameMode.CREATIVE) {
                continue;
            }
            Block block = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (block.getType() == Material.AIR) {
                block = block.getRelative(BlockFace.DOWN);
            }
            Location loc = p.getLocation();
            if (block.getType() != Material.AIR && block.getType() != Material.WATER && isClearOfFootprints(loc, m_minRenderSquared, p.getName())) {
                float offset = REGULAR_OFFSET;
                if (block.getType() == Material.SNOW) {
                    offset = SNOW_OFFSET;
                }
                loc.setY(block.getLocation().getY() + offset);
                Footstep newFootstep = new Footstep(
                    loc,
                    m_timeToLast / 2,
                    p.getName()
                );
                sendFootstep(newFootstep);
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
                sendFootstep(footstep);
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

    /**
     * Sends the footstep to nearby players
     * @param footstep the footstep to send
     */
    private void sendFootstep(Footstep footstep){
        m_defaultPacket.getStrings().write(0, "footstep");
        Location loc = footstep.getLocation();
        m_defaultPacket.getFloat()
                .write(0, (float) loc.getX())//x
                .write(1, (float) loc.getY())//y
                .write(2, (float) loc.getZ())
                .write(3, 0.0F)//offsetx
                .write(4, 0.0F)//offsety
                .write(5, 0.0F)//offsetz
                .write(6, 1.0F);
        m_defaultPacket.getIntegers().write(0, 1);
        for (Player p : Bukkit.getOnlinePlayers()) {
            try {
                if (p.getLocation().distanceSquared(loc) < m_maxRenderSquared) {
                    PROTOCOL_MANAGER.sendServerPacket(p, m_defaultPacket);
                }
            } catch (IllegalArgumentException ignored) {
                //wrong world, doesn't matter
            } catch (InvocationTargetException ignored) {}
        }
    }
}
