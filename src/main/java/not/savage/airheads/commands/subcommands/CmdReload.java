package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import org.bukkit.command.CommandSender;

import java.time.Instant;

public class CmdReload implements SubCommand {

    @Override
    public void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin) {
        final Instant start = Instant.now();
        plugin.reloadPlugin(true);
        sender.sendMessage(
                MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Reloaded in " +
                        (Instant.now().toEpochMilli() - start.toEpochMilli()) + "ms")
        );
    }

    @Override
    public String[] aliases() {
        return new String[] { "reload" };
    }
}
