package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import org.bukkit.command.CommandSender;

public class CmdDelete implements SubCommand {

    // /airheads delete <name>
    //          [0]       [1]
    @Override
    public void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin) {
        if (args.length != 2) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>Usage: /airheads move <name>"));
            return;
        }

        String name = args[1];
        if (!plugin.getAirHeadsConfig().getAirHeads().containsKey(args[1])) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>No Airhead by that name!"));
        } else {
            plugin.getAirHeadsConfig().getAirHeads().remove(args[1]);
            plugin.saveUpdates();
            plugin.reloadPlugin();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Deleted AirHead " + name + "!"));
        }
    }

    @Override
    public String[] aliases() {
        return new String[] { "delete", "remove" };
    }
}