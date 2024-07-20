package me.meibion.PhysicalCommands;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class PhysicalCommand {

    private static final int pageSize = 7;
    private static List<PhysicalCommand> pcList = new ArrayList<>();

    private Block block;
    private List<AbstractMap.SimpleEntry<String, Boolean>> scriptedCommands; // Command, console/not console

    public PhysicalCommand(Block targetBlock, List<AbstractMap.SimpleEntry<String, Boolean>> sc) {
        new PhysicalCommand(targetBlock, sc, false);
    }

    public PhysicalCommand(Block targetBlock, List<AbstractMap.SimpleEntry<String, Boolean>> sc, boolean saveToFile) {
        this.block = targetBlock;
        this.scriptedCommands = sc;

        // Check if there already is a pc at this block
        PhysicalCommand existingPC = PhysicalCommand.getPC(block);
        if (existingPC != null) {
            existingPC.deleteSelf();
        }

        pcList.add(this);
        if (saveToFile) {
            Main.dataUtils.updateFile();
        }
    }

    public void runCommands(Player player) {
        for(AbstractMap.SimpleEntry<String, Boolean> commandData : scriptedCommands) {

            // Get data
            String command = commandData.getKey();
            command = command.replaceAll("(?i)%player%", player.getName());
            boolean useConsole = commandData.getValue();

            // Run as console
            if(useConsole) {
                try { Main.server.dispatchCommand(new ConsoleSender(), command); }
                catch(Exception ignore) {
                    Main.msgSend(player, ChatColor.RED + "Unable to run command.");
                }
            }
            // Run as player
            else {
                try { Main.server.dispatchCommand(player, command); }
                catch(Exception ignore) {
                    Main.msgSend(player, ChatColor.RED + "Unable to run command.");
                }
            }
        }
    }

    public static PhysicalCommand getPC(Block target) {

        if(target == null) { return null; }

        for (PhysicalCommand pc : pcList) {
            if(pc.block == null) { continue; }
            if (pc.block.getLocation().equals(target.getLocation())) {
                return pc;
            }
        }

        return null;
    }

    public void deleteSelf() {
        pcList.remove(this);
        Main.dataUtils.updateFile();
    }

    public static void outputPCs(Player player, int pageNum) {
        final int maxPages = (int) Math.ceil((double) pcList.size() / pageSize);

        // Check if there are any pages to show
        if(maxPages <= 0) {
            Main.msgSend(player, ChatColor.RED, "There are no physical commands registered.");
            return;
        }

        // Check if the page number is valid
        if(pageNum < 1) { pageNum = 1; }
        else if(pageNum > maxPages) { pageNum = maxPages; }

        // Output physical commands in a list
        Main.msgSend(player, ChatColor.YELLOW, "Page " + pageNum + " of " + maxPages + " -");
        for(int i = pageSize * (pageNum -1); i < pageSize * pageNum; i++) {

            // Check if current entry is out of range
            if(i >= pcList.size()) { break; }

            // Get the physical command and block
            Block block = pcList.get(i).block;

            // Send message
            player.sendMessage(
                (ChatColor.GOLD + "[" + ChatColor.YELLOW + (i+1) + ChatColor.GOLD + "] - ") +
                (ChatColor.YELLOW + String.valueOf(block.getX()) + ", " + block.getY() + ", " + block.getZ()) +
                (" (" + block.getWorld().getName() + ChatColor.GOLD + ")")
            );
        }

        // Tell player how to proceed to next page - if not already on last page
        if(pageNum * pageSize >= pcList.size()) { return; }

        player.sendMessage(ChatColor.GOLD + "Use /physicalcmd list " + (pageNum + 1) + " for the next page." );

    }

    public List<AbstractMap.SimpleEntry<String, Boolean>> getScriptedCommands() { return scriptedCommands; }
    public static List<PhysicalCommand> getPCs() { return pcList; }
    public Block getBlock() { return block; }
}
