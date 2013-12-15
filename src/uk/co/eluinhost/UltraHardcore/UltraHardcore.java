package uk.co.eluinhost.UltraHardcore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.eluinhost.UltraHardcore.bans.DeathBan;
import uk.co.eluinhost.UltraHardcore.borders.BorderCreator;
import uk.co.eluinhost.UltraHardcore.commands.*;
import uk.co.eluinhost.UltraHardcore.commands.inter.UHCCommand;
import uk.co.eluinhost.UltraHardcore.config.ConfigHandler;
import uk.co.eluinhost.UltraHardcore.config.ConfigNodes;
import uk.co.eluinhost.UltraHardcore.config.PermissionNodes;
import uk.co.eluinhost.UltraHardcore.features.FeatureManager;
import uk.co.eluinhost.UltraHardcore.features.UHCFeature;
import uk.co.eluinhost.UltraHardcore.features.core.*;
import uk.co.eluinhost.UltraHardcore.game.GameManager;
import uk.co.eluinhost.UltraHardcore.metrics.MetricsLite;

/**
 * UltraHardcore
 * 
 * Main plugin class, init
 * 
 * @author ghowden
 *
 */
public class UltraHardcore extends JavaPlugin implements Listener{
	
	private static UltraHardcore uhc_instance;
	
	//get the current plugin
	public static UltraHardcore getInstance(){return uhc_instance;}
	
	//When the plugin gets started
	public void onEnable(){
        ConfigurationSerialization.registerClass(DeathBan.class);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		uhc_instance = this;

        FileConfiguration config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();


        loadDefaultModules();
        setupCommands();
        	
		//Load the game manager
		@SuppressWarnings("unused")
		GameManager gm = new GameManager();
				
		//Load all the metric infos
		try {
			MetricsLite met = new MetricsLite(this);
			met.start();
		} catch (IOException ignored){}
	}

	private void setExecutor(String commandName, UHCCommand ce){
		PluginCommand pc = getCommand(commandName);
		if(pc == null){
			getLogger().warning("Plugin failed to register the command "+commandName+", is the command already taken?");
		}else{
			pc.setExecutor(ce);
			pc.setTabCompleter(ce);
		}
	}
	
	private void setupCommands(){
		setExecutor("heal",new HealCommand());
		setExecutor("feed",new FeedCommand());
		setExecutor("tpp",new TPCommand());
		setExecutor("ci",new ClearInventoryCommand());
        setExecutor("deathban",new DeathBanCommand());
		
		TeamCommands tc = new TeamCommands();
		setExecutor("randomteams",tc);
		setExecutor("clearteams",tc);
		setExecutor("listteams",tc);
		setExecutor("createteam",tc);
		setExecutor("removeteam",tc);
		setExecutor("jointeam",tc);
		setExecutor("leaveteam",tc);
		setExecutor("emptyteams",tc);
		
		setExecutor("scatter",new ScatterCommandConversational());
		setExecutor("freeze",new FreezeCommand());
		setExecutor("feature",new FeatureCommand());
		setExecutor("generateborder",new BorderCreator());
        setExecutor("givedrops",new GiveDropCommand());

        for(Field field : PermissionNodes.class.getDeclaredFields()){
            try {
                Object o = field.get(PermissionNodes.class);
                if(o instanceof Permission){
                    getServer().getPluginManager().addPermission((Permission) o);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
	}
	
	private void loadDefaultModules(){
         Logger log = Bukkit.getLogger();
		 log.info("Loading UHC feature modules...");
         //Load the default features with settings in config
         FileConfiguration config = ConfigHandler.getConfig(ConfigHandler.MAIN);
		 ArrayList<UHCFeature> features = new ArrayList<UHCFeature>();
		 features.add(new DeathLightningFeature(config.getBoolean(ConfigNodes.DEATH_LIGHTNING)));
         features.add(new EnderpearlsFeature(config.getBoolean(ConfigNodes.NO_ENDERPEARL_DAMAGE)));
         features.add(new GhastDropsFeature(config.getBoolean(ConfigNodes.GHAST_DROP_CHANGES)));
         features.add(new PlayerHeadsFeature(config.getBoolean(ConfigNodes.DROP_PLAYER_HEAD)));
         features.add(new PlayerListFeature(config.getBoolean(ConfigNodes.PLAYER_LIST_HEALTH)));
         features.add(new RecipeFeature(config.getBoolean(ConfigNodes.RECIPE_CHANGES)));
         features.add(new RegenFeature(config.getBoolean(ConfigNodes.NO_HEALTH_REGEN)));
         features.add(new DeathMessages(config.getBoolean(ConfigNodes.DEATH_MESSAGES_ENABLED)));
         features.add(new DeathDrops(config.getBoolean(ConfigNodes.DEATH_DROPS_ENABLED)));
         features.add(new AnonChat(config.getBoolean(ConfigNodes.ANON_CHAT_ENABLED)));
         features.add(new GoldenHeads(config.getBoolean(ConfigNodes.GOLDEN_HEADS_ENABLED)));
         features.add(new DeathBansFeature(config.getBoolean(ConfigNodes.DEATH_BANS_ENABLED)));
         features.add(new PotionNerfs(config.getBoolean(ConfigNodes.POTION_NERFS_ENABLED)));
         features.add(new NetherFeature(config.getBoolean(ConfigNodes.NETHER_DISABLE_ENABELD)));
         features.add(new WitchSpawnsFeature(config.getBoolean(ConfigNodes.WITCH_SPAWNS_ENABLED)));
         try{
        	 features.add(new HardcoreHearts(config.getBoolean(ConfigNodes.HARDCORE_HEARTS_ENABLED)));
         }catch(NoClassDefFoundError e){
        	 log.severe("Cannot find a class for HardcoreHearts, ProtocolLib is needed for this feature to work, disabling...");
         }
         try{
        	 features.add(new FootprintFeature(config.getBoolean(ConfigNodes.FOOTPRINTS_ENABLED)));
         }catch(NoClassDefFoundError e){
        	 log.severe("Cannot find a class for Footprints, ProtocolLib is needed for this feature to work, disabling...");
         }
         
         for(UHCFeature f : features){
        	 try {
				FeatureManager.addFeature(f);
				log.info("Loaded feature module: "+f.getFeatureID());
			} catch (Exception e) {
				log.severe("Failed to load a module "+(f==null?"null":f.getFeatureID()));
				e.printStackTrace();
			}
         }
	}
		
}