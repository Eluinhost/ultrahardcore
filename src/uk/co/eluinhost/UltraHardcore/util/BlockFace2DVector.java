package uk.co.eluinhost.UltraHardcore.util;

import org.bukkit.block.BlockFace;

public enum BlockFace2DVector{
    NORTH(BlockFace.NORTH),
    EAST(BlockFace.EAST),
    SOUTH(BlockFace.SOUTH),
    WEST(BlockFace.WEST),
    NORTH_EAST(BlockFace.NORTH_EAST),
    NORTH_NORTH_EAST(BlockFace.NORTH_NORTH_EAST),
    EAST_NORTH_EAST(BlockFace.EAST_NORTH_EAST),
    NORTH_WEST(BlockFace.NORTH_WEST),
    NORTH_NORTH_WEST(BlockFace.NORTH_NORTH_WEST),
    WEST_NORTH_WEST(BlockFace.WEST_NORTH_WEST),
    SOUTH_EAST(BlockFace.SOUTH_EAST),
    SOUTH_SOUTH_EAST(BlockFace.SOUTH_SOUTH_EAST),
    EAST_SOUTH_EAST(BlockFace.EAST_SOUTH_EAST),
    SOUTH_WEST(BlockFace.SOUTH_WEST),
    SOUTH_SOUTH_WEST(BlockFace.SOUTH_SOUTH_WEST),
    WEST_SOUTH_WEST(BlockFace.WEST_SOUTH_WEST);

    private final BlockFace bf;

    private BlockFace2DVector(BlockFace bf) {
        this.bf = bf;
    }

    public int getX() {
        return -bf.getModX();
    }

    public int getZ() {
        return bf.getModZ();
    }

    public BlockFace getBlockFace(){
        return bf;
    }

    public double getAngle(){
        return Math.atan2(this.getX(), this.getZ());
    }

    public static BlockFace getClosest(double look_angle){
        BlockFace2DVector[] vectors = BlockFace2DVector.values();
        BlockFace2DVector best = vectors[0];
        double angle = Math.abs(best.getAngle());
        for(BlockFace2DVector bfv : BlockFace2DVector.values()){
            double a = look_angle-bfv.getAngle();
            if(a > Math.PI*2){
                a -= Math.PI*2;
            }else if(a < 0){
                a += Math.PI*2;
            }
            if(Math.abs(a) < angle){
                best = bfv;
                angle = Math.abs(a);
            }
        }
        return best.getBlockFace();
    }
}