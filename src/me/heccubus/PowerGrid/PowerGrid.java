package me.heccubus.PowerGrid;

import java.util.logging.Logger;
import me.heccubus.PowerGrid.RedstoneListener;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PowerGrid extends JavaPlugin{

	Logger log = Logger.getLogger("Minecraft");
	private final RedstoneListener redstonelistener = new RedstoneListener(this);

	public void onEnable() {
		log.info("PowerGrid has been enabled.");
		
        //PM For Listeners
        PluginManager pm = getServer().getPluginManager();
        //Block Register
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, redstonelistener, Event.Priority.Normal, this);
	}
	
	public void onDisable() {
		log.info("PowerGrid has been disabled.");			
	}
	
}