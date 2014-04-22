package com.publicuhc.ultrahardcore.commands;

import com.publicuhc.pluginframework.configuration.Configurator;
import com.publicuhc.pluginframework.shaded.inject.Inject;
import com.publicuhc.pluginframework.translate.Translate;

public class SimpleCommand {

    private final Configurator m_configManager;
    private final Translate m_translate;

    /**
     * @param configManager the config manager
     * @param translate the translator
     */
    @Inject
    protected SimpleCommand(Configurator configManager, Translate translate){
        m_configManager = configManager;
        m_translate = translate;
    }

    /**
     * @return the config manager
     */
    public Configurator getConfigurator(){
        return m_configManager;
    }

    /**
     * @return the translator
     */
    public Translate getTranslator() {
        return m_translate;
    }
}
