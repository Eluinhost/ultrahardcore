package uk.co.eluinhost.UltraHardcore.config;

public class ConfigNodes {

/////////////
//main nodes
/////////////

/**
* Main node for all core features in the config file
*/
public static final String FEATURE_NODE = "features";

/////////////////////
//sub feature nodes of FEATURE_NODE
/////////////////////

/**
* Whether or not the health regen feature is enabled by default
*/
public static final String NO_HEALTH_REGEN = FEATURE_NODE+".regenChanges";

/**
* The base node of the ghast drop changes
*/
public static final String GHAST_DROP_CHANGES_NODE = FEATURE_NODE+".ghastDropChanges";

/**
* Whether or not the ghast drops feature is enabled by default
*/
public static final String GHAST_DROP_CHANGES = GHAST_DROP_CHANGES_NODE+".enabled";

/**
 * Whether or not the player list health is enabled by default
 */
public static final String PLAYER_LIST_HEALTH = FEATURE_NODE+".playerListHealth.enabled";
public static final String PLAYER_LIST_DELAY = FEATURE_NODE+".playerListHealth.delay";
public static final String PLAYER_LIST_COLOURS = FEATURE_NODE+".playerListHealth.colours";
public static final String PLAYER_LIST_UNDER_NAME = FEATURE_NODE+".playerListHealth.belowName";
public static final String PLAYER_LIST_SCALING = FEATURE_NODE+".playerListHealth.scaling";
public static final String PLAYER_LIST_ROUND_HEALTH = FEATURE_NODE+".playerListHealth.roundHealth";
public static final String PLAYER_LIST_HEALTH_NAME = FEATURE_NODE+".playerListHealth.belowNameUnit";

/**
 * Whether or not the recipe changes are enabled by default
 */
public static final String RECIPE_CHANGES = FEATURE_NODE+".recipeChanges.enabled";


/**
 * Whether or not the enderperl no damage feature is enabled by default
 */
public static final String NO_ENDERPEARL_DAMAGE = FEATURE_NODE+".noEnderpearlDamage";

public static final String PLAYER_HEAD_NODE = FEATURE_NODE+".headDrop";
public static final String DROP_PLAYER_HEAD = PLAYER_HEAD_NODE+".enabled";
public static final String PLAYER_HEAD_DROP_CHANCE = PLAYER_HEAD_NODE+".percentChance";
public static final String PLAYER_HEAD_DROP_STAKE = PLAYER_HEAD_NODE+".onStake";
public static final String PLAYER_HEAD_PVP_ONLY = PLAYER_HEAD_NODE+".pvp.pvponly";
public static final String PLAYER_HEAD_PVP_NON_TEAM = PLAYER_HEAD_NODE+".pvp.nonteamonly";

public static final String DEATH_LIGHTNING = FEATURE_NODE+".deathLightning";

public static final String FREEZE_NODE = "freeze";
public static final String FREEZE_REAPPLY_TIME = FREEZE_NODE+".delay";
public static final String FREEZE_TIME = FREEZE_NODE+".time";
public static final String FREEZE_EFFECTS = FREEZE_NODE+".effects";

public static final String SCATTER_MAX_TRIES = "scatter.maxtries";
public static final String SCATTER_MAX_ATTEMPTS = "scatter.maxattempts";
public static final String SCATTER_DELAY = "scatter.delay";
public static final String SCATTER_ALLOWED_BLOCKS = "scatter.allowedBlocks";
public static final String SCATTER_DEFAULT_NODE = "scatter.default";
public static final String SCATTER_DEFAULT_RADIUS = SCATTER_DEFAULT_NODE+".radius";
public static final String SCATTER_DEFAULT_MINRADIUS = SCATTER_DEFAULT_NODE+".minradius";
public static final String SCATTER_DEFAULT_TYPE = SCATTER_DEFAULT_NODE+".type";
public static final String SCATTER_DEFAULT_TEAMS = SCATTER_DEFAULT_NODE+".useteams";
public static final String SCATTER_DEFAULT_WORLD = SCATTER_DEFAULT_NODE+".world";
public static final String SCATTER_DEFAULT_X = SCATTER_DEFAULT_NODE+".x";
public static final String SCATTER_DEFAULT_Z= SCATTER_DEFAULT_NODE+".z";
public static final String SCATTER_DEFAULT_PLAYERS = SCATTER_DEFAULT_NODE+".players";

public static final String BORDER_NODE = "border";
public static final String BORDER_BLOCK = BORDER_NODE+".id";
public static final String BORDER_BLOCK_META = BORDER_NODE+".meta";

public static final String DEATH_MESSAGES_NODE = FEATURE_NODE+".deathMessages";
public static final String DEATH_MESSAGES_ENABLED = DEATH_MESSAGES_NODE+".enabled";
public static final String DEATH_MESSAGES_FORMAT = DEATH_MESSAGES_NODE+".message";
public static final String DEATH_MESSAGES_SUPPRESSED = DEATH_MESSAGES_NODE+".remove";

public static final String DEATH_DROPS_NODE = FEATURE_NODE+".deathDrops";
public static final String DEATH_DROPS_ENABLED = DEATH_DROPS_NODE+".enabled";
public static final String DEATH_DROPS_ITEMS = DEATH_DROPS_NODE+".items";

public static final String ANON_CHAT_ENABLED = FEATURE_NODE+".anonChat.enabled";

public static final String GOLDEN_HEADS_ENABLED = FEATURE_NODE+".goldenHeads.enabled";
public static final String GOLDEN_HEADS_HEAL_FACTOR = FEATURE_NODE+".goldenHeads.amountExtra";

@SuppressWarnings("unused")
public static final String MORE_FOOD_ENABLED = FEATURE_NODE+".moreFood.enabled";

public static final String HARDCORE_HEARTS_ENABLED = FEATURE_NODE+".hardcoreHearts.enabled";

public static final String FOOTPRINTS_NODE = FEATURE_NODE+".footprints";
public static final String FOOTPRINTS_RENDER_DISTANCE = FOOTPRINTS_NODE+".renderdistance";
public static final String FOOTPRINTS_MIN_DISTANCE = FOOTPRINTS_NODE+".mindistance";
public static final String FOOTPRINTS_TIME_TO_LAST = FOOTPRINTS_NODE+".time";
public static final String FOOTPRINTS_ENABLED = FOOTPRINTS_NODE+".enabled";

public static final String DEATH_BANS_NODE = FEATURE_NODE+".deathbans";
public static final String DEATH_BANS_CLASSES = DEATH_BANS_NODE+".bans";
public static final String DEATH_BANS_DELAY = DEATH_BANS_NODE+".delay";
public static final String DEATH_BANS_ENABLED = DEATH_BANS_NODE+".enabled";


public static final String POTION_NERFS = FEATURE_NODE+"potionNerfs";
public static final String POTION_NERFS_ENABLED = POTION_NERFS+".enabled";
public static final String RECIPE_CHANGES_SPLASH = POTION_NERFS+".disableSplash";
public static final String DISABLE_ABSORB = POTION_NERFS+".disableAbsorb";
    public static final String RECIPE_CHANGES_IMPROVED = POTION_NERFS+".disableGlowstone";
}
