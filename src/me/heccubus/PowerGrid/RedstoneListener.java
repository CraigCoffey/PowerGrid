package me.heccubus.PowerGrid;

import java.util.logging.Logger;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneListener extends BlockListener {

	Logger log = Logger.getLogger("Minecraft");
	
	public static PowerGrid plugin;

	public RedstoneListener(PowerGrid instance) {
	    plugin = instance;
	}

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block bTarget = event.getBlock();
		int bTargetID = bTarget.getTypeId();
		Block uTarget = bTarget.getRelative(BlockFace.DOWN);
		int uTargetID = uTarget.getTypeId();
		Block bTemp;
		int bTempID = 0;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;

		// If the block UNDER the powered block is of the correct type...
		if (uTargetID == 22) {
			// ...check all directions (NORTH, SOUTH, EAST and WEST)...
			for (int i=0; i<=3; i++) {
				bFace = bFaces[i];
				bTemp = uTarget.getRelative(bFace);
				bTempID = bTemp.getTypeId();

				// ...for any blocks in those directions of the correct type, 
				// call the toggleTorch method.
				if (bTempID == 22) {
					toggleTorch(bTemp, bTargetID);
				}
			}
		}
	}

	private void toggleTorch(Block tb, int tID) {
		Block bTarget = tb;
		Block bTemp;
		int bTempID = 0;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;

		// If a dead redstone torch ID is passed, use air instead
		int bID = (tID == 75) ? 0 : tID;

		// Get the block above the target block
		bTemp = bTarget.getRelative(BlockFace.UP);
		bTempID = bTemp.getTypeId();
		
		// Set it to the passed-in block type (air or a powered redstone torch) as long as
		// it is not ALREADY the passed-in type, and it IS the OTHER type.  Ignore all other
		// block and material types.
		if (bTempID != bID && (bTempID == 0 || bTempID == 76)) {
			bTemp.setTypeId(bID);
		}
		
		// Call this method recursively for all qualifying blocks all around the target block
		// NORTH, SOUTH, EAST and WEST only.
		for (int i=0; i<=3; i++) {
			bFace = bFaces[i];
			bTemp = bTarget.getRelative(bFace);
			bTempID = bTemp.getTypeId();
			
			// Only call recursively if the block in that direction is the appropriate type
			// ...and the block ABOVE that block has not already been set to the target type
			// (air or a powered redstone torch)
			if (bTempID == 22 && bTemp.getRelative(BlockFace.UP).getTypeId() != bID) {
				toggleTorch(bTemp, bID); 
			}
		}
	}
}