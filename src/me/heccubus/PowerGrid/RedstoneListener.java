package me.heccubus.PowerGrid;

import java.util.logging.Logger;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Wool;

/**
 * RedstoneListener for PowerGrid
 * 
 * @author Craig Coffey (aka. Heccubus)
 *
 */
public class RedstoneListener extends BlockListener {
	Logger log = Logger.getLogger("Minecraft");
	
	// Used to limit the depth of the recursion to avoid crushing 
	// the server if common blocks are chosen accidentally.
	private int maxDepth = -1;
	private int currentDepth = 0;

	// Grid blocks use for powered and unpowered states
	private String GridOffColor = "";
	private String GridOnColor = "";
	
	public static PowerGrid plugin;

	public RedstoneListener(PowerGrid instance) {
	    plugin = instance;
	}

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block gridPowerBlock = event.getBlock();
		int gridPowerBlockID = gridPowerBlock.getTypeId();		
		Block gridBaseBlock = gridPowerBlock.getRelative(BlockFace.DOWN);
		BlockState gridBaseBlockState = gridBaseBlock.getState();
		Wool woolBlock = null;
		DyeColor woolBlockColor = null;
		Material gridBaseBlockType = gridBaseBlock.getType();

		int targetPowerBlockID = -1;
		DyeColor targetBaseBlockColor = null;

		currentDepth = 0;
		
		// Load config settings
		maxDepth = PowerGrid.CONFIG.getInt("MaxGridSize",1000);
		GridOffColor = PowerGrid.CONFIG.getString("GridOffColor","WHITE");
		GridOnColor = PowerGrid.CONFIG.getString("GridOnColor","YELLOW");

		// As long as the block UNDER the block in question is a WOOL block...
		if (gridBaseBlockType.equals(Material.WOOL)) {
			// Fetch the wool block, and its color
			woolBlock = (org.bukkit.material.Wool)gridBaseBlockState.getData();
			woolBlockColor = woolBlock.getColor();

			// If the torch is un-powered, then set the target type to AIR, otherwise set it to a powered torch
			if (gridPowerBlockID == 75) { targetPowerBlockID = 0; } else { targetPowerBlockID = 76; }
			
			// Toggle the base powergrid block color
			if (woolBlockColor.toString().equals(GridOffColor)) { targetBaseBlockColor = DyeColor.valueOf(GridOnColor); }
			if (woolBlockColor.toString().equals(GridOnColor)) { targetBaseBlockColor = DyeColor.valueOf(GridOffColor); }

			// If the base block is of the correct type/color, and the power block is too... then start the recursion.
			if ((woolBlockColor.toString().equals(GridOffColor) && gridPowerBlockID == 76) || (woolBlockColor.toString().equals(GridOnColor) && gridPowerBlockID == 75)) {
				toggleTorch(gridBaseBlock, targetBaseBlockColor, targetPowerBlockID);
			}

			//log.info("Dye Color: " + woolBlockColor);
		}		
	}

	private void toggleTorch(Block tb, DyeColor gbbColor, int gpbID) {
		Block targetBaseBlock = tb;
		Material targetBaseBlockType = targetBaseBlock.getType();		
		//int targetBaseBlockID = targetBaseBlock.getTypeId();

		Block tempBlock = null;
		int tempBlockID = -1;
		BlockFace bFaces[] = BlockFace.values();
		BlockFace bFace = null;

		if (targetBaseBlockType.equals(Material.WOOL)) {
			// If the base block is not already of the proper color
			if (!gbbColor.equals(getDyeColor(tb))) {
			
				// Set it to be of the proper color
				setDyeColor(tb, gbbColor);

				// Check the blocks around the base block, N,S,E,W and UP.
				for (int i=0; i<=4; i++) {
					bFace = bFaces[i];
					tempBlock = targetBaseBlock.getRelative(bFace);
					tempBlockID = tempBlock.getTypeId();

					// If the current block is a powered or un-powered redstone torch, or air...
					if ( (gpbID!=0 && (tempBlockID == 0 || tempBlockID == 75)) || (gpbID!=76 && tempBlockID == 76) ) {
					
						// If the base block is not near a redstone wire, set the target block to the proper type.
						if (!nearGridBlocks(tempBlock)) { tempBlock.setTypeId(gpbID); }
						//tempBlock.setTypeId(gpbID);

						// Update the temp block ID variable now that it may have changed.
						tempBlockID = tempBlock.getTypeId();

						// If the temp block is a powered torch...
						if (tempBlockID == 76) {
							// Make sure it is placed on the proper face of the base block
							BlockState state = tempBlock.getState();
							((org.bukkit.material.RedstoneTorch)state.getData()).setFacingDirection(bFace);
							state.update();
						}
					}
				}
				currentDepth++;
		
				for (int i=0; i<=5; i++) {
					bFace = bFaces[i];
					tempBlock = targetBaseBlock.getRelative(bFace);
			
					//if ((GridOffColor.equals(getDyeColor(tempBlock)) || GridOnColor.equals(getDyeColor(tempBlock))) && currentDepth <= maxDepth) {
					if (tempBlock.getType().equals(Material.WOOL) && currentDepth <= maxDepth) {
						if (!gbbColor.equals(getDyeColor(tempBlock))) {
							toggleTorch(tempBlock, gbbColor, gpbID); 
						}
					}
				}
			}
		}
	}

	private DyeColor getDyeColor(Block b) {
		BlockState bState = b.getState();
		Wool woolBlock = null;
		DyeColor woolBlockColor = null;
		
		if (b!=null && b.getType().equals(Material.WOOL)) {
			woolBlock = (org.bukkit.material.Wool)bState.getData();
			woolBlockColor = woolBlock.getColor();
			return woolBlockColor;
		}
		
		return null;
	}

	private void setDyeColor(Block b, DyeColor dc) {
		BlockState bState = b.getState();
		Wool woolBlock = null;

		if (b!=null && b.getType().equals(Material.WOOL)) {
			woolBlock = (org.bukkit.material.Wool)bState.getData();
			woolBlock.setColor(dc);
			bState.update();
		}
		
	}
	
	private boolean nearGridBlocks(Block b) {
		Block tempBlock = b.getRelative(BlockFace.UP);

		if (tempBlock.getType().equals(Material.WOOL)) { return true; }
		return false;
	}
	
}