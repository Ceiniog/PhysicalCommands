package me.meibion.PhysicalCommands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;

import java.io.File;
import java.util.*;

public class DataUtils extends Configuration {
    public DataUtils(File file) {
        super(file);

        // Check if the file exists
        if(!file.exists()) {

            // Attempt to create file path
            final File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new RuntimeException("Failed to create directory: " + file.getAbsolutePath());
            }
        }

        // Load data from file
        loadFromFile();
        save();
    }

    private void loadFromFile() {
        try { load(); }
        catch(Exception e) { e.printStackTrace(); }

        // Get parsed data
        List<Object> parsedPCData = this.getList("pcData");
        if(parsedPCData == null) { return; }

        // Convert parsed data to objects
        for(Object obj : parsedPCData) {
            HashMap<String,Object> pcData = (HashMap<String,Object>) obj;

            // Unparse attributes

            // Block
            double blockX, blockY, blockZ;
            World world;
            try {
                blockX = Double.parseDouble(pcData.get("blockX").toString());
                blockY = Double.parseDouble(pcData.get("blockY").toString());
                blockZ = Double.parseDouble(pcData.get("blockZ").toString());
                world = Main.server.getWorld(pcData.get("world").toString());
            }
            catch(Exception error) { error.printStackTrace(); continue; }

            // Commands
            List<AbstractMap.SimpleEntry<String, Boolean>> commands = new ArrayList<>();
            try {
                List<LinkedHashMap<String, Object>> parsedCommands = (List<LinkedHashMap<String, Object>>) pcData.get("commands");
                for(String key : parsedCommands.get(0).keySet()) {
                    AbstractMap.SimpleEntry<String, Boolean> cmdData = new AbstractMap.SimpleEntry<>(key, (boolean) parsedCommands.get(0).get(key));
                    commands.add(cmdData);
                }
            }
            catch(NullPointerException error) { error.printStackTrace(); continue; }

            // Create new PhysicalCommand object
            new PhysicalCommand(world.getBlockAt(new Location(world, blockX, blockY, blockZ)), commands);
        }
    }

    public void updateFile() {
        List<Object> parsedPCList = new ArrayList<>();
        for(PhysicalCommand pc : PhysicalCommand.getPCs()) {

            // Check if block exists
            Block block = pc.getBlock();
            if(block == null) { continue; }

            // Parse block data
            HashMap<String,Object> pcData = new HashMap<>();
            pcData.put("blockX", block.getLocation().getX());
            pcData.put("blockY", block.getLocation().getY());
            pcData.put("blockZ", block.getLocation().getZ());
            pcData.put("world", block.getLocation().getWorld().getName());

            // Parse commands
            List<HashMap<String,Object>> parsedCommands = new ArrayList<>();
            for(AbstractMap.SimpleEntry<String, Boolean> cmd : pc.getScriptedCommands()) {
                HashMap<String,Object> cmdData = new HashMap<>();
                cmdData.put(cmd.getKey(), cmd.getValue());
                parsedCommands.add(cmdData);
            }
            pcData.put("commands", parsedCommands);

            // Add parsed data to list
            parsedPCList.add(pcData);
        }

        this.setProperty("pcData", parsedPCList);
        save();
    }


}
