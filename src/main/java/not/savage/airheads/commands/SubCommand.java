package not.savage.airheads.commands;

import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.command.CommandSender;

public interface SubCommand {

    void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin);

    String[] aliases();

    default boolean matches(String argument) {
        for (String alias : aliases()) {
            if (alias.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

}
