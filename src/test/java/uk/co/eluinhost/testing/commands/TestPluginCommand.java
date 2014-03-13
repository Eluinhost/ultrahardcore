package uk.co.eluinhost.testing.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class TestPluginCommand extends Command {
    public TestPluginCommand() {
        super("test");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        return false;
    }
}
