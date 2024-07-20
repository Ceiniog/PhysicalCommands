package me.meibion.PhysicalCommands.Commands;

import me.meibion.PhysicalCommands.Main;
import me.meibion.PhysicalCommands.PhysicalCommand;
import me.meibion.PhysicalCommands.Setup;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        // Check if the sender is a player
        if (!Main.isPlayer(commandSender)) {
            return true;
        }

        // Get event details
        Player player = (Player) commandSender;

        // Check if the player has permissions
        if (!Main.hasPerms(player, "PhysicalCommands.Admin")) {
            return true;
        }

        // Check args
        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /physicalcmd [Create|Delete|Inspect|List]");
            return true;
        }

        // Check if the player is already in the setup event
        Setup setup = Setup.getPlayerSetup(player);
        if (setup != null) {
            Main.msgSend(player, ChatColor.RED, "You are already in the setup event.");
            return true;
        }

        // Check setup type
        switch(args[0].toLowerCase()) {
            case "create":
            case "delete":
            case "inspect":
                // Create setup object
                new Setup(player, args[0]);
                break;
            case "list":

                // Parse page num
                int pageNum = 1;
                if(args.length >= 2) {
                    try { pageNum = Integer.parseInt(args[1]); }
                    catch(NumberFormatException ignore) { }
                }

                PhysicalCommand.outputPCs(player, pageNum);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /physicalcmd [Create|Delete|Inspect|List]");
                break;
        }

        return true;
    }
}
