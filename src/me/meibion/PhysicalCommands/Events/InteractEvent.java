package me.meibion.PhysicalCommands.Events;

import me.meibion.PhysicalCommands.PhysicalCommand;
import me.meibion.PhysicalCommands.Setup;
import net.minecraft.server.EntitySheep;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class InteractEvent implements Listener {
    public PlayerListener pl = new PlayerListener(){
        public void onPlayerInteract(PlayerInteractEvent event) {

            // Get event details
            Player player = event.getPlayer();
            Action action = event.getAction();
            Block block = event.getClickedBlock();

            // Check if the player has perms
            if(!player.hasPermission("PhysicalCommands.Use")) { return; }

            // Check the action type
            if(action == Action.RIGHT_CLICK_BLOCK) {
                if(block.getType() == Material.STONE_PLATE || block.getType() == Material.WOOD_PLATE) {
                    return;
                }
            }
            else if(action == Action.PHYSICAL) {
                if(block.getType() != Material.STONE_PLATE && block.getType() != Material.WOOD_PLATE) {
                    return;
                }
            }
            else { return; }

            // Check if there's a physical command associated with the block
            PhysicalCommand pc = PhysicalCommand.getPC(block);
            if(pc == null) { return; }

            // Check if there's an ongoing setup
            Setup setup = Setup.getPlayerSetup(player);
            if(setup != null) {
                if(setup.getSetupType().equalsIgnoreCase("inspect")) {
                    return;
                }
            }

            // Run the commands
            pc.runCommands(player);
        }
    };

}
