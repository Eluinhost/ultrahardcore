package uk.co.eluinhost.testing.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import uk.co.eluinhost.features.FeatureManager;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FeatureManager.class,Bukkit.class,PluginCommand.class})
public class CommandHandlerTest {

}
