package not.savage.airheads.commands;

import not.savage.airheads.AirHeadsPlugin;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public interface SubCommand {

    /**
     * Plugin default tab-completer for AirHead names. Shared across all subcommands.
     */
    TriFunction<AirHeadsPlugin, String[], CommandSender, List<String>> AIRHEADS_TAB_COMPLETER = (plugin, args, sender) -> {
        if (args.length == 2) {
            if (args[1].isEmpty()) {
                return new ArrayList<>(plugin.getAirHeadsConfig().getAirHeads().keySet());
            }

            return plugin.getAirHeadsConfig().getAirHeads().keySet().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .toList();
        }
        return List.of();
    };

    /**
     * Executes the subcommand, with the given arguments and sender.
     * @param args Command arguments
     * @param sender The sender of the command
     * @param plugin The plugin instance
     */
    void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin);

    /**
     * Returns the aliases for this subcommand.
     * @return The aliases for this subcommand
     */
    String[] aliases();

    /**
     * Checks if the given argument matches any of the aliases for this subcommand.
     * @param argument The argument to check
     * @return True if the argument matches any of the aliases, false otherwise
     */
    default boolean matches(String argument) {
        for (String alias : aliases()) {
            if (alias.equalsIgnoreCase(argument)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tab-completes the subcommand with the given arguments and sender.
     * @param args Command arguments
     * @param sender The sender of the command
     * @return A list of tab-completion suggestions
     */
    default List<String> onTabComplete(String[] args, CommandSender sender) {
        return List.of();
    }

}
