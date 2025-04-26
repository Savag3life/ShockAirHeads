package not.savage.airheads.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.subcommands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * The main command executor for AirHeads.
 * Handles /airheads root command and subcommands.
 * Also manages tab completions for commands and arguments.
 */
public final class CmdAirHeads implements CommandExecutor, TabCompleter {

    private final List<SubCommand> subCommands = new ArrayList<>();
    private final AirHeadsPlugin plugin;

    public CmdAirHeads(AirHeadsPlugin plugin) {
        this.plugin = plugin;
         this.subCommands.addAll(Arrays.asList(
                 new CmdReload(),
                 new CmdCreate(),
                 new CmdMove(),
                 new CmdDelete(),
                 new CmdTeleport()
         ));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!commandSender.hasPermission("airheads.admin")) {
            commandSender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>You don't have permission to use this command!"));
            return false;
        }

        if (strings.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.matches(strings[0])) {
                    subCommand.execute(strings, commandSender, plugin);
                    return true;
                }
            }
        }
        commandSender.sendMessage(MiniMessage.miniMessage().deserialize(CmdHelp.HELP_MESSAGE));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!commandSender.hasPermission("airheads.admin")) {
            return null;
        }

        if (strings.length == 1) {
            return subCommands.stream()
                    .map(SubCommand::aliases)
                    .map(Arrays::asList)
                    .flatMap(Collection::stream)
                    .toList();
        }

        if (strings.length == 2) {
            for (SubCommand subCommand : subCommands) {
                if (subCommand.matches(strings[0])) {
                    return subCommand.onTabComplete(strings, commandSender, plugin);
                }
            }
        }

        return null;
    }
}
