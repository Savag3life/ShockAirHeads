package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import not.savage.airheads.config.AirHeadConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdCreate implements SubCommand {

    // /airheads create <name>
    //          [0]     [1]
    @Override
    public void execute(String[] args, CommandSender sender, AirHeadsPlugin plugin) {
        if (args.length != 2) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>Usage: /airheads create <name>"));
            return;
        }

        String name = args[1];
        if (plugin.getAirHeadsConfig().getAirHeads().containsKey(args[1])) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>That AirHead already exists!"));
        } else {
            plugin.getAirHeadsConfig().getAirHeads().put(name, new AirHeadConfig(((Player) sender).getLocation()));
            plugin.saveUpdates();
            plugin.reloadPlugin(false);
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Created AirHead " + name));
        }
    }

    @Override
    public String[] aliases() {
        return new String[] { "create" };
    }
}
