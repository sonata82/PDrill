package me.devcom.pdrill;


import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class PDrill extends JavaPlugin {
	public static PDrill plugin;
	
	public BukkitScheduler sheduler;
	public final DrillManager drillManager = new DrillManager( this );
	public final DrillBlockListener blockListener = new DrillBlockListener( this );
	public final DrillPlayerListener playerListener = new DrillPlayerListener( this );
	public final Logger logger = Logger.getLogger("Minecraft");
	public final String prefix = "[PDrill] ";

	public final ConfigurationManager configManager = new ConfigurationManager(this, "config.yml", "plugins/PDrill/");

	@Override 
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info( pdfFile.getName() + " [ver: " + pdfFile.getVersion() + "] is disabled!");
	}
	
	@Override 
	public void onEnable(){
		plugin = this;
		
		sheduler = plugin.getServer().getScheduler();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent( Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Normal, this);
		pm.registerEvent( Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Normal, this);
		
		File dir = new File( "plugins/PDrill" );
		if(!dir.exists()){
			dir.mkdirs();
		}
		
		Runnable runnable = new Runnable(){
			public void run(){
				plugin.drillManager.updateDrills();
			}
		};
		sheduler.scheduleSyncRepeatingTask( plugin, runnable, 2, 2);
		
		PluginDescriptionFile pdfFile = this.getDescription();
		
		configManager.load();
		this.logger.info( prefix + pdfFile.getName() + " [ver: " + pdfFile.getVersion() + "] is enabled!");
	}
	
	//@Override
	public boolean onCommand( CommandSender sender, Command cmd, String commandLabel, String[] args){
		if( commandLabel.equalsIgnoreCase("pdscript") || commandLabel.equalsIgnoreCase("pdload")){

			ArrayList<Integer> drillIds = new ArrayList<Integer>();
			ArrayList<String> script = new ArrayList<String>();
			
			String[] idArgs = args[0].split( "," );
			Integer argCount = idArgs.length;
			for(Integer i = 0; i < argCount; i++){
				if(argCount >= 2 && i <= argCount - 3 && idArgs[i + 1].equals( "..." ) ){
					Integer start = Integer.parseInt( idArgs[i] );
					Integer end = Integer.parseInt( idArgs[i + 2] );
					for(Integer j = start; j <= end; j++){
						drillIds.add( j  );
					}
					i += 2;
				}else{
					Integer id;
					try {
						id = Integer.parseInt( idArgs[i] );
						drillIds.add( id  );
					} catch (NumberFormatException e) {
						sender.sendMessage(idArgs[i] + " is not a valid number");
						break;
					}
				}
			}
			
			if(commandLabel.equalsIgnoreCase("pdscript")){
				for(Integer i = 1; i < args.length; i++){
					script.add( args[i] );
				}
			}else if(commandLabel.equalsIgnoreCase("pdload")){
				String scriptName = args[1];
					
				String scriptString = configManager.getScript( scriptName );
				if(scriptString != ""){
					String[] scriptArgs = scriptString.split( " " );
	
					for(Integer i = 0; i < scriptArgs.length; i++){
						script.add( scriptArgs[i] );
					}
				}else{
					logger.info( prefix + "No such script!");
				}
			}
			
			sendScripts(sender, drillIds, script);
		}else if( commandLabel.equalsIgnoreCase( "pdcreate" ) ){
			String what = args[0];
			
			if(args.length < 2){
				return false;
			}
			
			if(what.equalsIgnoreCase( "script" )){
				
				String name = args[1];
				String script = "";
				
				if(configManager.getScript( name ) != ""){
					sender.sendMessage( prefix + "Script with name [" + name + "] allready exists!" );
					return true;
				}
				
				configManager.config.load();
				
				if(args.length > 2){
					for(Integer i = 2; i < args.length; i++){
						script += args[i];
						if( i < args.length - 1 ){
							script += " ";
						}
					}
					
					configManager.config.setProperty("script." + name + ".script", script);
				}else{
					configManager.config.setProperty("script." + name + ".script", "");
				}
				
				configManager.scripts.put(name, script);
				configManager.config.save();
			}else if( what.equalsIgnoreCase("fuel") ){
				String name = args[1];
				Integer id, airSpeed, blockSpeed, blockCount, fuelCount;
				
				if( args.length >= 3){
					id = Integer.parseInt( args[2] );
					
					if(args.length >= 4){
						airSpeed = Integer.parseInt( args[3] );
						
						if( args.length >= 5){
							blockSpeed = Integer.parseInt( args[4] );
							
							if(args.length >= 6){
								blockCount = Integer.parseInt( args[5] );
								
								if(args.length >= 7){
									fuelCount = Integer.parseInt( args[6] );
								}else{
									fuelCount = 1;
								}
							}else{
								blockCount = 1;
								fuelCount = 1;
							}
						}else{
							blockSpeed = 1;
							blockCount = 1;
							fuelCount = 1;
						}
					}else{
						airSpeed = 1;
						blockSpeed = 1;
						blockCount = 1;
						fuelCount = 1;
					}
				}else{
					id = -1;
					airSpeed = 1;
					blockSpeed = 1;
					blockCount = 1;
					fuelCount = 1;
				}
				
				configManager.config.load();
				
				configManager.config.setProperty("fuel." + name + ".fuelId", id);
				configManager.config.setProperty("fuel." + name + ".drillAirSpeed", airSpeed);
				configManager.config.setProperty("fuel." + name + ".drillBlockSpeed", blockSpeed);
				configManager.config.setProperty("fuel." + name + ".fuelConsumptionBlockCount",blockCount);
				configManager.config.setProperty("fuel." + name + ".fuelConsumptionFuelCount", fuelCount);
				
			    Fuel fuel = new Fuel(id, airSpeed, blockSpeed, blockCount, fuelCount);
			    configManager.fuels.put(id, fuel);
			    
				configManager.config.save();
			}
		}else if(commandLabel.equalsIgnoreCase( "pdmake" )){
			String what = args[0];
			
			Toggler fbToggler = new Toggler( "f", "b");
			Toggler rlToggler = new Toggler( "r", "l");
			Toggler udToggler = new Toggler( "u", "d");
			
			if(what.equalsIgnoreCase( "room" )){
				ArrayList<Integer> drillIds = new ArrayList<Integer>();
				ArrayList<String> script = new ArrayList<String>();
				
				String[] idArgs = args[1].split( "," );
				Integer argCount = idArgs.length;
				for(Integer i = 0; i < argCount; i++){
					if(argCount >= 2 && i <= argCount - 3 && idArgs[i + 1].equals( "..." ) ){
						Integer start = Integer.parseInt( idArgs[i] );
						Integer end = Integer.parseInt( idArgs[i + 2] );
						for(Integer j = start; j <= end; j++){
							drillIds.add( j  );
						}
						i += 2;
					}else{
						Integer id = Integer.parseInt( idArgs[i] );
						drillIds.add( id  );
					}
				}
				
				Integer xS,yS,zS;
				Integer x,y,z;
				
				x = Integer.parseInt( args[2] );
				y = Integer.parseInt( args[3] );
				z = Integer.parseInt( args[4] );
				
				if( x < 0){
					fbToggler.toggle();
					x *= -1;
				}
				if( y < 0){
					udToggler.toggle();
					y *= -1;
				}
				if( z < 0){
					rlToggler.toggle();
					z *= -1;
				}
				
				xS = x - 1;
				yS = y;
				zS = z - 1;
				
				y = 1;
				while( y <= yS ){
					
					z = zS;
					while( z >= 0 ){
						
						script.add( fbToggler.data() + xS);
						if( z > 0 )
							script.add( rlToggler.data() + 1);
						
						fbToggler.toggle();
						z--;
					}
					if( y < yS )
						script.add( udToggler.data() + 1);
					
					rlToggler.toggle();
					y++;
				}
				
				sendScripts(sender, drillIds, script);
			}
		}
		
		return true;
	}

	private void sendScripts(CommandSender sender, ArrayList<Integer> drillIds, ArrayList<String> script) {
		Integer ret;
		for(Integer drillId : drillIds){
			Drill drill = drillManager.getDrillFromId( drillId );
			ret = -1;
			
			if( drill != null){
				if(drill.enabled){
					ret = drill.JobMG.addScript( script );
				} else {
					sender.sendMessage(prefix + "["+ drillId +"] Drill is disabled!");
				}
			} else {
				sender.sendMessage(prefix + "["+ drillId +"] No such drill!");
			}
				
			if(ret == 1){
				sender.sendMessage(prefix + "["+ drillId +"] Script compiling passed!");
			}else if(ret == 0){
				sender.sendMessage(prefix + "["+ drillId +"] Script compiling failed!");
			}else if(ret == 2){
				sender.sendMessage(prefix + "["+ drillId +"] Drill is allready running a script!");
			}
		}
	}
}