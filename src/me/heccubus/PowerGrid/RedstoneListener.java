package me.heccubus.PowerGrid;

import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener extends BlockListener {

	Logger log = Logger.getLogger("Minecraft");
	
	private int maxDepth = -1;
	private int currentDepth = 0;
	
	private int GridOffBlockID = -1;
	private int GridOnBlockID = -1;
	
	public static PowerGrid plugin;

	public RedstoneListener(PowerGrid instance) {
	    plugin = instance;
	}

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block gridPowerBlock = event.getBlock();
		int gridPowerBlockID = gridPowerBlock.getTypeId();		
		Block gridBaseBlock = gridPowerBlock.getRelative(BlockFace.DOWN);
		int gridBaseBlockID = gridBaseBlock.getTypeId();
		int targetPowerBlockID = -1;
		int targetBaseBlockID = -1;
		currentDepth = 0;
		
		maxDepth = PowerGrid.CONFIG.getInt("MaxGridSize",1000);
		GridOffBlockID = PowerGrid.CONFIG.getInt("GridOffBlockID",22);
		GridOnBlockID = PowerGrid.CONFIG.getInt("GridOnBlockID",41);

		if (gridPowerBlockID == 75) { targetPowerBlockID = 0; } else { targetPowerBlockID = 76; }

		if (gridBaseBlockID == GridOffBlockID) { targetBaseBlockID = GridOnBlockID; }
		if (gridBaseBlockID == GridOnBlockID) { targetBaseBlockID = GridOffBlockID; }
		
		if ((gridBaseBlockID == GridOffBlockID && gridPowerBlockID == 76) || (gridBaseBlockID == GridOnBlockID && gridPowerBlockID == 75)) {
			toggleTorch(gridBaseBlock, targetBaseBlockID, targetPowerBlockID);
		}
	}

	private void toggleTorch(Block tb, int gbbID, int gpbID) {
		Block targetBaseBlock = tb;
		int targetBaseBlockID = targetBaseBlock.getTypeId();
		Block tempBlock = null;
		int tempBlockID = -1;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;
		
		if (targetBaseBlockID != gbbID) {
			targetBaseBlock.setTypeId(gbbID);
			
			for (int i=0; i<=4; i++) {
				bFace = bFaces[i];
				tempBlock = targetBaseBlock.getRelative(bFace);
				tempBlockID = tempBlock.getTypeId();
				
				if (tempBlockID == 0 || tempBlockID == 75 || tempBlockID == 76) {					
					if (!nearRedstoneWire(targetBaseBlock)) { tempBlock.setTypeId(gpbID); }
					
					tempBlockID = tempBlock.getTypeId();
					
					if (tempBlockID == 76) {
						BlockState state = tempBlock.getState();
						((org.bukkit.material.RedstoneTorch)state.getData()).setFacingDirection(bFace);
						state.update();
					}
				}
				
			}
			currentDepth++;
		}
		
		for (int i=0; i<=5; i++) {
			bFace = bFaces[i];
			tempBlock = targetBaseBlock.getRelative(bFace);
			tempBlockID = tempBlock.getTypeId();
			
			if ((tempBlockID == GridOffBlockID || tempBlockID == GridOnBlockID) && currentDepth <= maxDepth) {
				if (tempBlockID != gbbID) {
					toggleTorch(tempBlock, gbbID, gpbID); 
				}
			}
		}
	}

	private boolean nearRedstoneWire(Block b) {
		Block tempBlock = null;
		int tempBlockID = -1;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;

		for (int i=0; i<=3; i++) {
			bFace = bFaces[i];
			tempBlock = b.getRelative(bFace);
			tempBlockID = tempBlock.getTypeId();
			
			if (tempBlockID == 55) { return true; }
		}
		return false;
	}
}