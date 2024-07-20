package me.meibion.PhysicalCommands;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Setup {

    private static HashMap<String, Setup> playerStates = new HashMap<>();

    private String playerName;
    private Block block;
    private List<AbstractMap.SimpleEntry<String, Boolean>> commands = new ArrayList<>(); // Command, console/not console
    private String pendingCommand = "";
    private String state;
    private String type;

    public Setup(Player player, String type) {

        Main.msgSend(player, ChatColor.YELLOW, "Setup started. Write 'cancel' into chat to exit.");

        playerName = player.getName();
        this.type = type.toLowerCase();
        this.setSetupState("BlockSelect");
        playerStates.put(playerName, this);
    }

    private void finaliseSetup() {
        new PhysicalCommand(block, commands, true);
        playerStates.remove(this.playerName);
    }

    public void setSetupState(String state) {

        Player player = Main.server.getPlayer(playerName);
        if(player == null) { return; }

        state = state.toLowerCase();
        switch (state) {
            case "cancel":
                Main.msgSend(player, "You have quit the setup process.");
                playerStates.remove(this.playerName);
                break;
            case "finish":
                Main.msgSend(player, ChatColor.GREEN, "You have completed the setup process.");
                finaliseSetup();
                break;
            case "blockselect":
                Main.msgSend(player, ChatColor.YELLOW, "Block Selection:");
                Main.msgSend(player, "Right click the block you are referencing.");
                break;
            case "commandselect":
                Main.msgSend(player, ChatColor.YELLOW, "Command Entries:");
                Main.msgSend(player, "Run the command you wish to assign. The command will not be executed.");
                break;
            case "executorselect":
                Main.msgSend(player, ChatColor.YELLOW, "Executor Selection:");
                Main.msgSend(player, "Enter either 'Player' or 'Console' into chat to make your selection.");
                break;
            case "askmorecmd":
                Main.msgSend(player, "Would you like to add additional commands? Write 'yes' or 'no' into chat to make your selection.");
                break;
            case "quietend":
                playerStates.remove(this.playerName);
                break;
        }

        this.state = state;
    }

    public void makeExecutorConsole(boolean choice) {
        if(pendingCommand.equals("")) { return; }

        // Create single entry key:value pair object for command
        AbstractMap.SimpleEntry<String, Boolean> cmdData = new AbstractMap.SimpleEntry<>(pendingCommand, choice);

        // Add command to commands list
        commands.add(cmdData);

        // Clear pending command
        pendingCommand = "";

        // Send message to the player
        Player player = Main.server.getPlayer(playerName);
        if(player == null) { finaliseSetup(); return; }
        Main.msgSend(player, ChatColor.GREEN, "Executor specified successfully.");

        // Change setup state
        this.setSetupState("askmorecmd");
    }

    public void setBlock(Block b) throws IllegalArgumentException {
        Main.server.getLogger().info("test");
        if(Main.pluginConfig.isMaterialPermitted(b.getType())) {
            this.block = b;
            return;
        }

        throw new IllegalArgumentException();
    }

    public static Setup getPlayerSetup(Player player) {
        return playerStates.get(player.getName());
    }
    public void setPendingCommand(String cmd) { pendingCommand = cmd; }

    public String getSetupState() { return state; }
    public String getSetupType() { return type; }
    public String getPendingCommand() { return pendingCommand; }
}
