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

		if (uTargetID == 22) {
			toggleTorch(bTarget, bTargetID); 
		}
	}

	private void toggleTorch(Block tb, int tID) {
		Block bTarget = tb;
		Block bTemp;
		int bTempID = 0;
		Block uTemp = null;
		int uTempID = 0;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;

		int bID = tID;
		if (bID == 75) { bID = 0; }

		bTarget.setTypeId(bID);

		for (int i=0; i<=3; i++) {
			bFace = bFaces[i];
			bTemp = bTarget.getRelative(bFace);
			bTempID = bTemp.getTypeId();
			uTemp = bTemp.getRelative(BlockFace.DOWN);
			uTempID = uTemp.getTypeId();
				
			if (bTempID != bID && uTempID == 22) {
				toggleTorch(bTemp, bID);
			}
		}		
	}
}
