package me.meibion.PhysicalCommands.Events;

import me.meibion.PhysicalCommands.PhysicalCommand;
import net.minecraft.server.EntitySheep;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

public class DestructionDetection implements Listener {

    public BlockListener bl = new BlockListener(){
        public void onBlockFade(BlockFadeEvent event) {
            PhysicalCommand pc = PhysicalCommand.getPC(event.getBlock());

            if(pc != null) {
                pc.deleteSelf();
            }
        }

        public void onBlockBreak(BlockBreakEvent event) {
            PhysicalCommand pc = PhysicalCommand.getPC(event.getBlock());

            if(pc != null) {
                pc.deleteSelf();
            }
        }

        public void onBlockBurn(BlockBurnEvent event) {
            PhysicalCommand pc = PhysicalCommand.getPC(event.getBlock());

            if(pc != null) {
                pc.deleteSelf();
            }
        }
    };

    public EntityListener el = new EntityListener(){
        public void onEntityExplode(EntityExplodeEvent event) {
            for(Block block : event.blockList()) {
                PhysicalCommand pc = PhysicalCommand.getPC(block);
                if(pc != null) {
                    pc.deleteSelf();
                }
            }
        }
    };
}
