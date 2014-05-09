package com.publicuhc.ultrahardcore.pluginfeatures.autorespawn;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.shaded.inject.Singleton;
import com.publicuhc.pluginframework.translate.Translate;
import com.publicuhc.ultrahardcore.pluginfeatures.UHCFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;

@Singleton
public class AutoRespawn extends UHCFeature {

    public static final String RESPAWN_PERMISSION = BASE_PERMISSION + "autorespawn";

    private final ProtocolManager m_manager = ProtocolLibrary.getProtocolManager();
    private final PacketContainer m_respawnPacket;
    /**
     * Construct a new feature
     *
     * @param plugin        the plugin
     * @param configManager the config manager to use
     * @param translate     the translator
     */
    @Inject
    protected AutoRespawn(Plugin plugin, Configurator configManager, Translate translate) {
        super(plugin, configManager, translate);

        m_respawnPacket = m_manager.createPacket(PacketType.Play.Client.CLIENT_COMMAND);
        m_respawnPacket.getClientCommands().write(0, EnumWrappers.ClientCommand.PERFORM_RESPAWN);
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent pde) {
        Player player = pde.getEntity();

        if (isEnabled() && player.hasPermission(RESPAWN_PERMISSION)) {
            try {
                m_manager.recieveClientPacket(player, m_respawnPacket);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getFeatureID() {
        return "AutoRespawn";
    }

    @Override
    public String getDescription() {
        return "Players will automatically respawn on death";
    }
}
