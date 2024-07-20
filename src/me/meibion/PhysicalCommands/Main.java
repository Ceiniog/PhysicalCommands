package me.meibion.PhysicalCommands;

import me.meibion.PhysicalCommands.Commands.SetupCommand;
import me.meibion.PhysicalCommands.Events.DestructionDetection;
import me.meibion.PhysicalCommands.Events.InteractEvent;
import me.meibion.PhysicalCommands.Events.SetupEvents;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin {

    public static Main plugin;
    public static Server server = Bukkit.getServer();
    private static File configFile;
    private static File dataFile;
    public static PluginConfig pluginConfig;
    public static DataUtils dataUtils;

    @Override
    public void onDisable() {
        server.getLogger().info("[PhysicalCommands] - Plugin Disabled");
    }

    @Override
    public void onEnable() {

        // Main attributes
        plugin = this;
        configFile = new File(getDataFolder().getAbsolutePath() + "/config.yml");
        dataFile = new File(getDataFolder().getAbsolutePath() + "/plugin_data.yml");
        pluginConfig = new PluginConfig(configFile);
        dataUtils = new DataUtils(dataFile);

        // COMMANDS
        getCommand("physicalcommand").setExecutor(new SetupCommand()); // Creates a new physical command

        // EVENTS
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, new SetupEvents().pl, Event.Priority.High, this); // Chat input control
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, new SetupEvents().pl, Event.Priority.High, this); // Block selection
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, new SetupEvents().pl, Event.Priority.Highest, this); // Command selection
        Bukkit.getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, new InteractEvent().pl, Event.Priority.Normal, this); // Run physical command
        Bukkit.getPluginManager().registerEvent(Event.Type.BLOCK_FADE, new DestructionDetection().bl, Event.Priority.Normal, this); // Detect block destruction
        Bukkit.getPluginManager().registerEvent(Event.Type.BLOCK_BREAK, new DestructionDetection().bl, Event.Priority.Normal, this); // Detect block destruction
        Bukkit.getPluginManager().registerEvent(Event.Type.BLOCK_BURN, new DestructionDetection().bl, Event.Priority.Normal, this); // Detect block destruction
        Bukkit.getPluginManager().registerEvent(Event.Type.ENTITY_EXPLODE, new DestructionDetection().el, Event.Priority.Normal, this); // Detect block explosion

        server.getLogger().info("[PhysicalCommands] - Plugin Enabled");
    }

    // Checks if a player has permissions
    public static boolean hasPerms(Player player, String perm) {
        boolean result = player.hasPermission(perm);
        if(!result) { player.sendMessage(ChatColor.RED + "You do not have permission to use this command!"); }
        return result;
    }

    // Checks if a CommandSender is a player
    public static boolean isPlayer(CommandSender sender) {
        boolean result = sender instanceof Player;
        if(!result) { server.getLogger().info("You must be a player to run this command!"); }
        return result;
    }

    public static void msgSend(Player player, String msg) {
        msgSend(player, ChatColor.GRAY, msg);
    }

    public static void msgSend(Player player, ChatColor color, String msg) {
        player.sendMessage(color + "[PhysicalCmds] - " + msg);
    }
}
