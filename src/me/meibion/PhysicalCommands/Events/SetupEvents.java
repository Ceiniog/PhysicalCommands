package me.meibion.PhysicalCommands.Events;

import me.meibion.PhysicalCommands.Main;
import me.meibion.PhysicalCommands.PhysicalCommand;
import me.meibion.PhysicalCommands.Setup;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import java.util.AbstractMap;
import java.util.List;

public class SetupEvents implements Listener {

    public PlayerListener pl = new PlayerListener(){

        // Used for entering chat actions
        public void onPlayerChat(PlayerChatEvent event) {

            // Get event details
            Player player = event.getPlayer();
            String message = event.getMessage().toLowerCase().trim().replaceAll("[^a-zA-Z0-9]", "");
            Setup setup = Setup.getPlayerSetup(player);

            // Check if the player is in the setup process
            if(setup == null) { return; }

            switch(message) {
                case "cancel":
                    setup.setSetupState("Cancel");
                    break;

                case "yes":
                    if(!setup.getSetupState().equalsIgnoreCase("askmorecmd")) {
                        Main.msgSend(player, ChatColor.RED, "Invalid option at this time.");
                        return;
                    }
                    setup.setSetupState("commandselect");
                    break;

                case "no":
                    if(!setup.getSetupState().equalsIgnoreCase("askmorecmd")) {
                        Main.msgSend(player, ChatColor.RED, "Invalid option at this time.");
                        return;
                    }
                    setup.setSetupState("finish");
                    break;

                case "player":
                    // Validate setup state
                    if(!setup.getSetupState().equalsIgnoreCase("ExecutorSelect")) {
                        Main.msgSend(player, ChatColor.RED, "You are unable to specify the executor at this time.");
                        return;
                    }
                    // Confirm choice
                    setup.makeExecutorConsole(false);
                    break;

                case "console":
                    // Validate setup state
                    if(!setup.getSetupState().equalsIgnoreCase("ExecutorSelect")) {
                        Main.msgSend(player, ChatColor.RED, "You are unable to specify the executor at this time.");
                        return;
                    }
                    // Confirm choice
                    setup.makeExecutorConsole(true);
                    break;

                default:
                    Main.msgSend(player, ChatColor.RED,"Unknown action. To cancel, enter the word 'cancel' into chat.");
                    break;

            }

            // Stop message from sending to chat
            event.setCancelled(true);
        }

        // Used for selecting a block to modify
        public void onPlayerInteract(PlayerInteractEvent event) {

            // Get event details
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            Action action = event.getAction();

            // Check action type
            if(action != Action.RIGHT_CLICK_BLOCK) { return; }

            // Check if the player is in the setup process
            Setup setup = Setup.getPlayerSetup(player);
            if(setup == null) { return; }


            // Check setup state
            if(!setup.getSetupState().equalsIgnoreCase("BlockSelect")) { return; }
            PhysicalCommand pc = PhysicalCommand.getPC(block);

            // Check setup type
            switch(setup.getSetupType().toLowerCase()) {
                case "create":
                    // Check if the block is valid
                    try { setup.setBlock(block); }
                    catch(Exception ignore) {
                        Main.msgSend(player, ChatColor.RED, "Block is not of a valid type.");
                        return;
                    }

                    // Proceed with next step
                    Main.msgSend(player, ChatColor.GREEN, "Block selected (" + block.getType().name().toLowerCase() + ").");
                    setup.setSetupState("CommandSelect");
                    break;
                case "delete":

                    // Check if there is a physical command associated with the selected block
                    if(pc == null) {
                        Main.msgSend(player, ChatColor.RED, "This block has no commands assigned to it.");
                        return;
                    }

                    // Delete the pc
                    pc.deleteSelf();
                    Main.msgSend(player, ChatColor.GREEN, "Physical command unassigned successfully.");
                    setup.setSetupState("finish");
                    break;
                case "inspect":
                    // Check if there is a physical command associated with the selected block
                    if(pc == null) {
                        Main.msgSend(player,"This block has no commands assigned to it.");
                        return;
                    }

                    Main.msgSend(player, ChatColor.YELLOW,"Assigned commands for this block:");
                    List<AbstractMap.SimpleEntry<String, Boolean>> scriptedCommands = pc.getScriptedCommands();
                    for(AbstractMap.SimpleEntry<String, Boolean> command : scriptedCommands) {
                        // Get the executor type
                        String executorType = command.getValue() ? "[Console]" : "[Player]";

                        // Send message
                        Main.msgSend(player, executorType + " /" + command.getKey());
                    }

                    // Finish setup
                    setup.setSetupState("quietend");

                default:
                    return;
            }
        }

        // Used for specifying what commands should run
        public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

            // Get event details
            Player player = event.getPlayer();
            String command = event.getMessage().substring(1);
            Setup setup = Setup.getPlayerSetup(player);

            // Check if the player is in the setup process
            if(setup == null) { return; }

            // Check setup state
            if(!setup.getSetupState().equalsIgnoreCase("CommandSelect")) { return; }

            // Safe from this point to cancel command
            event.setCancelled(true);

            // Check if the command exists
            /*
            if (Main.server.getPluginCommand(command.split(" ")[0]) == null) {
                Main.msgSend(player, ChatColor.RED, "Command not found. Check your spelling.");
                return;
            }
             */

            // Proceed with next step
            setup.setPendingCommand(command);
            Main.msgSend(player, ChatColor.GREEN, "Command specified successfully.");
            setup.setSetupState("ExecutorSelect");
            return;
        }


    };
}
