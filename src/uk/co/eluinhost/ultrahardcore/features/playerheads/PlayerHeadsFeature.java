package uk.co.eluinhost.ultrahardcore.features.playerheads;

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
import uk.co.eluinhost.ultrahardcore.services.ConfigManager;
import uk.co.eluinhost.ultrahardcore.config.ConfigNodes;
import uk.co.eluinhost.ultrahardcore.config.PermissionNodes;
import uk.co.eluinhost.ultrahardcore.features.UHCFeature;
import uk.co.eluinhost.ultrahardcore.util.ServerUtil;


/**
 * PlayerHeadsFeature
 * Handles the dropping of a player head on death
 *
 * @author ghowden
 */
public class PlayerHeadsFeature extends UHCFeature {

    public static final String PLAYER_HEAD_STAKE = BASE_PERMISSION + "headStake";
    public static final String DROP_SKULL = BASE_PERMISSION + "dropSkull";

    private static final Random RANDOM = new Random();

    public PlayerHeadsFeature() {
        super("PlayerHeads","Players can drop their heads on death");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent pde) {
        //if we're enabled
        if (isEnabled()) {
            //skip if the player isnt allowed to drop a skull
            if (!pde.getEntity().hasPermission(DROP_SKULL)) {
                return;
            }
            //if it wasn't a valid kill dont drop
            if(!isValidKill(pde.getEntity())) {
                return;
            }
            //do a random chance based on the config options
            if (RANDOM.nextInt(100) >= 100 - ConfigManager.getInstance().getConfig().getInt(ConfigNodes.PLAYER_HEAD_DROP_CHANCE)) {
                //drop the head with the loot if no stake was made
                if (!putHeadOnStake(pde.getEntity())) {
                    pde.getDrops().add(playerSkullForName(pde.getEntity().getName()));
                }
            }
        }
    }

    /**
     * Checks if the kill was a valid PVP kill
     * @param deadPlayer the dead player
     * @return boolean
     */
    private static boolean isValidKill(Player deadPlayer){
        if (ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_HEAD_PVP_ONLY)) {
            //get the killer and if there isn't one it wasn't a PVP kill
            Player killer = deadPlayer.getKiller();
            if (killer == null) {
                return false;
            }
            //if we're checking that teammember kills don't count
            if (ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_HEAD_PVP_NON_TEAM)) {
                //get the scoreboard and get the teams of both players
                Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
                Team team1 = sb.getPlayerTeam(deadPlayer);
                Team team2 = sb.getPlayerTeam(killer);
                //if they're both in valid teams and its the same team it wasn't a valid kill
                if (team1 != null && team2 != null && team1.getName().equals(team2.getName())) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean putHeadOnStake(Player p) {
        if(!ConfigManager.getInstance().getConfig().getBoolean(ConfigNodes.PLAYER_HEAD_DROP_STAKE)){
            return false;
        }
        //head location
        Location head = p.getEyeLocation();
        //block the player's head is in
        Block headBlock = head.getBlock();
        //get the closest non air block below the players feet
        Block ground = getClosestGround(headBlock.getRelative(BlockFace.DOWN, 2));
        if (ground == null) {
            return false;
        }

        //get the block 2 above the ground
        Block skullBlock = ground.getRelative(BlockFace.UP, 2);

        //if it's not empty we can't place the block
        if (skullBlock == null || !skullBlock.isEmpty()) {
            return false;
        }

        //teleport the player to the skull location
        p.teleport(skullBlock.getLocation());

        //check the player's permission for the stake here, used for position based permissions
        //TODO this doesn't appear to work as a valid way to check permissions for a coordinate
        if (!p.hasPermission(PLAYER_HEAD_STAKE)) {
            return false;
        }

        //set the skull block to an actual skull block
        setBlockAsHead(p, skullBlock);

        //get the space for a fence and set it if there's nothing there
        Block fenceBlock = ground.getRelative(BlockFace.UP);
        if (fenceBlock != null && fenceBlock.isEmpty()) {
            fenceBlock.setType(Material.NETHER_FENCE);
        }
        //made successfully
        return true;
    }

    private static void setBlockAsHead(Player p, Block headBlock) {
        //set the type to skull
        headBlock.setType(Material.SKULL);
        //noinspection deprecation
        headBlock.setData((byte) 1); //TODO depreacted but no alternative yet
        //get the state to be a player skull for the player and set its rotation based on where the player was looking
        Skull state = (Skull) headBlock.getState();
        state.setSkullType(SkullType.PLAYER);
        state.setOwner(p.getName());
        state.setRotation(ServerUtil.getCardinalDirection(p));
        state.update();
    }

    /**
     * Gets the closest non empty block under the block supplied or null if none found
     *
     * @param block Block
     * @return Block
     */
    private static Block getClosestGround(Block block) {
        Block loopBlock = block;
        //recurse until found
        while (true) {
            //if there is no block return null
            if (loopBlock == null) {
                return null;
            }
            //if it's not empty return this block
            if (!loopBlock.isEmpty()) {
                return block;
            }
            loopBlock = loopBlock.getRelative(BlockFace.DOWN);
        }
    }

    /**
     * Generates a player skull itemstack for the given name
     * @param name the player name
     * @return ItemStack
     */
    private static ItemStack playerSkullForName(String name) {
        //1 skull item
        ItemStack is = new ItemStack(Material.SKULL_ITEM, 1);
        //3 is a player skull
        is.setDurability((short) 3);
        //set the metadata for the owner
        SkullMeta meta = (SkullMeta) is.getItemMeta();
        meta.setOwner(name);
        is.setItemMeta(meta);
        return is;
    }
}
