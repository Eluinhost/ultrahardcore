package uk.co.eluinhost.UltraHardcore.features.core;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;
import uk.co.eluinhost.UltraHardcore.util.ServerUtil;


/**
 * PlayerHeadsFeature
 * 
 * Handles the dropping of a player head on death
 * @author ghowden
 *
 */
public class PlayerHeadsFeature extends UHCFeature{
	
	public PlayerHeadsFeature(boolean enabled) {
		super("PlayerHeads",enabled);
		setDescription("Players can drop their heads on death");
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent pde){
		if(isEnabled()){
			if(pde.getEntity().hasPermission(PermissionNodes.DROP_SKULL)){
				if(ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_HEAD_PVP_ONLY)){
                    Player killer = pde.getEntity().getKiller();
					if(killer == null){
						return;
					}
                    if(ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_HEAD_PVP_NON_TEAM)){
                        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                        Team team1 = sb.getPlayerTeam(pde.getEntity());
                        Team team2 = sb.getPlayerTeam(killer);
                        if(team1 != null && team2 != null && team1.getName().equals(team2.getName())){
                            return;
                        }
                    }
				}
				Random r = new Random();
				if(r.nextInt(100)>=(100-ConfigHandler.getConfig(ConfigHandler.MAIN).getInt(ConfigNodes.PLAYER_HEAD_DROP_CHANCE))){
                    if(!ConfigHandler.getConfig(ConfigHandler.MAIN).getBoolean(ConfigNodes.PLAYER_HEAD_DROP_STAKE) || !makeHeadStakeForPlayer(pde.getEntity())){
					    pde.getDrops().add(playerSkullForName(pde.getEntity().getName()));
                    }
				}
			}
		}
	}	
	
	public boolean makeHeadStakeForPlayer(Player p){
        Location head = p.getEyeLocation();
        Block head_block = head.getBlock();
        Block ground = getClosestGround(head_block.getRelative(BlockFace.DOWN,2));
        if(ground != null){
            Block skull_block = ground.getRelative(BlockFace.UP,2);
            if(skull_block == null || !skull_block.isEmpty()){
                return false;
            }
            p.teleport(skull_block.getLocation());
            if(!p.hasPermission(PermissionNodes.PLAYER_HEAD_STAKE)){
                return false;
            }
            setBlockAsHead(p,skull_block);
            Block fence_block = ground.getRelative(BlockFace.UP);
            if(fence_block != null && fence_block.isEmpty()){
                fence_block.setType(Material.NETHER_FENCE);
            }
            return true;
        }
        return false;
    }

    private void setBlockAsHead(Player p, Block head_block){
        head_block.setType(Material.SKULL);
        head_block.setData((byte) 1);          //TODO depreacted but no alternative?
        Skull state = (Skull) head_block.getState();
        state.setSkullType(SkullType.PLAYER);
        state.setOwner(p.getName());
        state.setRotation(ServerUtil.getCardinalDirection(p));
        state.update();
    }

    private Block getClosestGround(Block b){
        if(b == null){
            return null;
        }
        if(!b.isEmpty()){
            return b;
        }
        return getClosestGround(b.getRelative(BlockFace.DOWN));
    }

	private ItemStack playerSkullForName(String name){
		ItemStack is = new ItemStack(Material.SKULL_ITEM,1);
		is.setDurability((short) 3);
		SkullMeta meta = (SkullMeta) is.getItemMeta();
		meta.setOwner(name);
		is.setItemMeta(meta);
		return is;
	}

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}
}
