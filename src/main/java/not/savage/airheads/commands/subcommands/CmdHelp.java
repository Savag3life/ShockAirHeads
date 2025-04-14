package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class CmdHelp implements SubCommand {

    public static final String HELP_MESSAGE = """
            <gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Help
            <gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>/airheads create <name>
            <gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>/airheads reload
            """;

    @Override
    public void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin) {
        sender.sendMessage(MiniMessage.miniMessage().deserialize(HELP_MESSAGE));
    }

    @Override
    public String[] aliases() {
        return new String[] { "help" };
    }
}
