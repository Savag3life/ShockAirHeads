package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import not.savage.airheads.config.AirHead;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdMove implements SubCommand {

    private final AirHeadsPlugin plugin;

    public CmdMove(AirHeadsPlugin plugin) {
        this.plugin = plugin;
    }

    // /airheads movehere <name>
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
            AirHead airHead = plugin.getAirHeadsConfig().getAirHeads().get(name);
            airHead.setLocation(((Player) sender).getLocation());
            plugin.getAirHeadsConfig().getAirHeads().put(name, airHead);
            plugin.saveUpdates();
            plugin.reloadPlugin();
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Moved AirHead " + name + " to your location!"));
        }
    }

    @Override
    public String[] aliases() {
        return new String[] { "move", "movehere", "tphere" };
    }

    @Override
    public List<String> onTabComplete(String[] args, CommandSender sender) {
        if (args.length != 2) {
            return new ArrayList<>();
        }
        return AIRHEADS_TAB_COMPLETER.apply(plugin, args, sender);
    }
}