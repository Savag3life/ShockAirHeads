package not.savage.airheads.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.List;

/**
 * The main command executor for AirHeads. /airheads reload is the only command, currently...
 */
public class CmdAirHeads implements CommandExecutor, TabCompleter {

    // SonarLint says this was used too many times to not be a constant!
    private static final String RELOAD_COMMAND_ALIAS = "reload";
    private final AirHeadsPlugin plugin;

    public CmdAirHeads(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!commandSender.hasPermission("airheads.admin")) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>You don't have permission to use this command!"));
            return false;
        }

        if (strings.length > 0 && strings[0].equalsIgnoreCase(RELOAD_COMMAND_ALIAS)) {
            final Instant start = Instant.now();
            plugin.reloadPlugin();
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Reloaded in " + (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms"));
            return true;
        }


        commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>Invalid usage! <white>Use <underlined><click:suggest_command:\"/airheads reload\">/airheads reload"));
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        return List.of(RELOAD_COMMAND_ALIAS);
    }
}
