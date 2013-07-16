package uk.co.eluinhost.UltraHardcore.features.core;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;

import java.util.Random;

public class RidiculousMode extends UHCFeature {

	public RidiculousMode(boolean enabled) {
		super(enabled);
		setFeatureID("RidiculousMode");
		setDescription("Wouldn't you like to know...");
	}

    private Random r = new Random();

    private PotionEffect getRandomPotionEffect(){
        PotionEffectType[] pet = PotionEffectType.values();
        return new PotionEffect(pet[r.nextInt(pet.length)],Integer.MAX_VALUE,r.nextInt(2),true);
    }


    @EventHandler
    public void onCreateSpawnEvent(CreatureSpawnEvent cse){
        if(isEnabled()){
            LivingEntity entity = cse.getEntity();
            entity.addPotionEffect(getRandomPotionEffect());

            //health changes
            switch(cse.getEntityType()){
                case PIG_ZOMBIE:
                case ZOMBIE:
                    entity.setMaxHealth(100);
                    entity.setHealth(100);
                    break;
                case GHAST:
                case BLAZE:
                case SILVERFISH:
                    entity.setMaxHealth(50);
                    entity.setHealth(50);
                    break;
            }

            //class changes
            switch (cse.getEntityType()){
                case SKELETON:
                    Skeleton s = (Skeleton) entity;
                    if(cse.getLocation().getWorld().getEnvironment().equals(World.Environment.NORMAL) && r.nextInt(2)==0){
                        s.setSkeletonType(Skeleton.SkeletonType.WITHER);
                    }
                    break;
                case ZOMBIE:
            }
        }
    }

    //TODO always storm

    //TODO don't drop cobble

    //TODO disallow all crafting

    //TODO LULZ

	@Override
	public void enableFeature() {}

	@Override
	public void disableFeature() {}	
}
