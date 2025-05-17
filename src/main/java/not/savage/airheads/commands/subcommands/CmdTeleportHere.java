package not.savage.airheads.commands.subcommands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import not.savage.airheads.AirHeadsPlugin;
import not.savage.airheads.commands.SubCommand;
import not.savage.airheads.config.AirHeadConfig;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CmdTeleportHere implements SubCommand {

    // /airheads tp <name>
    //          [0] [1]
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
            AirHeadConfig airHead = plugin.getAirHeadsConfig().getAirHeads().get(name);
            if (!(sender instanceof Player)) {
                sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <red>This command can only be used by players!"));
                return;
            }

            ((Player) sender).teleport(airHead.getLocation());
            sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#2185da:#cee8fd><bold>AirHeads </bold><white>» <green>Teleported to " + name + "'s location!"));
        }
    }

    @Override
    public String[] aliases() {
        return new String[] { "tp", "teleport" };
    }

    @Override
    public List<String> onTabComplete(String[] args, CommandSender sender, AirHeadsPlugin plugin) {
        if (args.length != 2) {
            return new ArrayList<>();
        }
        return AIRHEADS_TAB_COMPLETER.apply(plugin, args, sender);
    }
}