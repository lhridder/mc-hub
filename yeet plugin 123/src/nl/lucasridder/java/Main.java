package nl.lucasridder.java;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;


public class Main extends JavaPlugin implements Listener, PluginMessageListener {
	
	//Start-up
	@Override
	public void onEnable() {
		// start up plugin
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		
		//stop tijd
		World world = Bukkit.getServer().getWorld("world");
	    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
	    
		//register events
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
		//config
		this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
		
		//enable
		System.out.println("[HUB]" + ChatColor.GREEN + " succesfully enabled");
	}
	
	//Power-down
	@Override
	public void onDisable() {
		this.saveConfig();
		System.out.println("[HUB]" + ChatColor.GREEN + " succesfully disabled");
		// shut down plugin
	}
	
	//Join
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		//player info
		Player player = e.getPlayer();
		String name = player.getName();
		
		//spawn loc
			try {
			int x = (int) this.getConfig().get("spawn.x");
			int y = (int) this.getConfig().get("spawn.y");
			int z = (int) this.getConfig().get("spawn.z");
			float yaw = (float) this.getConfig().get("spawn.yaw");
			float pitch = (float) this.getConfig().get("spawn.pitch");
			Location loc = new Location(player.getWorld(), x, y, z, yaw, pitch);
			player.teleport(loc);
			} catch(Exception e1){
				e1.printStackTrace();
			}
			
		//set inv
			player.getInventory().clear();
			ItemStack stack1 = new ItemStack(Material.GRASS_BLOCK);
			ItemStack stack2 = new ItemStack(Material.DIAMOND_SWORD);
			ItemStack stack3 = new ItemStack(Material.RED_WOOL);
			ItemMeta meta1 = stack1.getItemMeta();
			ItemMeta meta2 = stack2.getItemMeta();
			ItemMeta meta3 = stack3.getItemMeta();
			meta1.setDisplayName(ChatColor.GOLD + "Join survival!");
			meta2.setDisplayName(ChatColor.GOLD + "Join minigames!");
			meta3.setDisplayName(ChatColor.GOLD + "Join pixelmon!");
			stack1.setItemMeta(meta1);
			stack2.setItemMeta(meta2);
			stack3.setItemMeta(meta3);
			player.getInventory().setItem(3, stack1);
			player.getInventory().setItem(4, stack2);
			player.getInventory().setItem(5, stack3);
			player.updateInventory();

		//scoreboard
			ScoreboardManager manager = Bukkit.getScoreboardManager();
			Scoreboard b = manager.getNewScoreboard();
			
			Objective o = b.registerNewObjective("Gold", "", "Tutorial");
			o.setDisplaySlot(DisplaySlot.SIDEBAR);
			
			Score score2 = o.getScore(ChatColor.YELLOW + "Welkom, " + ChatColor.GRAY + player.getName());
			score2.setScore(2);
			
			Score score1 = o.getScore("");
			score1.setScore(1);
			
			Score score0 = o.getScore(ChatColor.GOLD + "IP: " + ChatColor.GREEN + "VPS.LucasRidder.NL");
			score0.setScore(0);
			
			player.setScoreboard(b);
			
		//join message
		if(player.isOp()) {
			//stop melding
			e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + name);
			//zeg hoi
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage(ChatColor.DARK_GRAY + "Welkom, " + ChatColor.GOLD + name);
			player.sendMessage(ChatColor.DARK_GRAY + "Jij bent een " + ChatColor.GOLD + "Admin" );
			player.sendMessage(ChatColor.BLUE + "Beschikbare servers: ");
			player.sendMessage(ChatColor.GOLD + "/survival" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/minigames");
			player.sendMessage("");
		} else if(!player.hasPlayedBefore()) {
			e.setJoinMessage(ChatColor.DARK_GRAY + "Welkom " + ChatColor.RESET + name);
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage(ChatColor.DARK_GRAY + "Welkom, " + ChatColor.GOLD + name);
			player.sendMessage(ChatColor.BLUE + "Beschikbare servers: ");
			player.sendMessage(ChatColor.GOLD + "/survival" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/minigames");
			player.sendMessage("");
			player.sendMessage("");
		} else {
			e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + name);
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage("");
			player.sendMessage(ChatColor.DARK_GRAY + "Welkom, " + ChatColor.GOLD + name);
			player.sendMessage(ChatColor.BLUE + "Beschikbare servers: ");
			player.sendMessage(ChatColor.GOLD + "/survival" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/minigames");
			player.sendMessage("");
			player.sendMessage("");
		}
		
	}

	//Leave
	public void onPlayerLeave(PlayerQuitEvent e ) {
		Player player = e.getPlayer();
		String name = player.getName();
		
		if(player.isOp()) {
			//stop melding
			e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + ChatColor.BOLD + name);
			//zeg hoi

		} else {
			
			e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + ChatColor.BOLD + name);
		}
	}
	
	//Admin Commands
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(cmd.getName().equalsIgnoreCase("gamemode")) {
			
			//check of sender speler is
			if(!(sender instanceof Player)) {
			//zeg het
				sender.sendMessage(ChatColor.RED + "Je bent geen speler");
				return true;
			} else {
			
			//te weinig argumenten
			if(args.length == 0) {
				sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
				return true;
			}
			
			//teveel argumenten
			if(args.length > 2) {
				sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
				return true;
			}
			
			//goede aantal argumenten
			if(args.length == 1 ) {
				//pak speler
				Player player = (Player) sender;
				
				if(args[0].equalsIgnoreCase("creative") | args[0].equalsIgnoreCase("1")) {
					player.setGameMode(GameMode.CREATIVE);
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("survival") | args[0].equalsIgnoreCase("0")) {
					player.setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("spectator") | args[0].equalsIgnoreCase("3")) {
					player.setGameMode(GameMode.SPECTATOR);
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("adventure") | args[0].equalsIgnoreCase("2")) {
					player.setGameMode(GameMode.ADVENTURE);
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
				}
				
			}

			//andere speler
			if(args.length == 2 ) {
				//pak speler
				Player target = Bukkit.getServer().getPlayer(args[2]);
				if(target == null) {
					sender.sendMessage(ChatColor.RED + "Doel is niet online");
				} else {
				
				if(args[0].equalsIgnoreCase("creative") | args[0].equalsIgnoreCase("1")) {
					target.setGameMode(GameMode.CREATIVE);
					target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("survival") | args[0].equalsIgnoreCase("0")) {
					target.setGameMode(GameMode.SURVIVAL);
					target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("spectator") | args[0].equalsIgnoreCase("3")) {
					target.setGameMode(GameMode.SPECTATOR);
					target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else if(args[0].equalsIgnoreCase("adventure") | args[0].equalsIgnoreCase("2")) {
					target.setGameMode(GameMode.ADVENTURE);
					target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
					sender.sendMessage(ChatColor.GREEN + "Gedaan.");
					return true;
				} else {
					sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
				}
				}
			}
			}
		}
		
		//setspawn command
		if(cmd.getName().equalsIgnoreCase("setspawn")) {
			if(!(sender instanceof Player)) {
				//zeg het
					sender.sendMessage(ChatColor.RED + "Je bent geen speler");
					return true;
				} else {
					Player player = (Player) sender;
					int x = player.getLocation().getBlockX();
					int y = player.getLocation().getBlockY();
					int z = player.getLocation().getBlockZ();
					float yaw = player.getLocation().getYaw();
					float pitch = player.getLocation().getPitch();
					this.getConfig().set("spawn.x", x);
					this.getConfig().set("spawn.y", y);
					this.getConfig().set("spawn.z", z);
					this.getConfig().set("spawn.yaw", yaw);
					this.getConfig().set("spawn.pitch", pitch);
					this.saveConfig();
					sender.sendMessage(ChatColor.GREEN + "Spawn set");
				}
		}
		
		//fly
		if(cmd.getName().equalsIgnoreCase("fly")) {
			if(!(sender instanceof Player)) {
				//zeg het
					sender.sendMessage(ChatColor.RED + "Je bent geen speler");
					return true;
				} else {
					if(args.length == 0) {
					Player player = (Player) sender;
					player.setFlying(true); 
					sender.sendMessage(ChatColor.GREEN + "Vliegen ingeschakeld.");
					}
					if(args.length > 0) {
						Player player = (Player) sender;
						player.setFlying(false); 
						sender.sendMessage(ChatColor.GREEN + "Vliegen uitgeschakeld.");
						}
				}
		}
		
		//survival
		if(cmd.getName().equalsIgnoreCase("survival")) {
			if(!(sender instanceof Player)) {
				//zeg het
					sender.sendMessage(ChatColor.RED + "Je bent geen speler");
					return true;
				} else {
			
			sender.sendMessage(ChatColor.DARK_GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + "survival");
			Player player = (Player) sender;
			//BUNGEE
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			  out.writeUTF("Connect");
			  out.writeUTF("survival");
			player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
				}
		}
		
		//minigames
		if(cmd.getName().equalsIgnoreCase("minigames")) {
			if(!(sender instanceof Player)) {
				//zeg het
					sender.sendMessage(ChatColor.RED + "Je bent geen speler");
					return true;
				} else {
			
			sender.sendMessage(ChatColor.DARK_GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + "minigames");
			Player player = (Player) sender;
			//BUNGEE
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			  out.writeUTF("Connect");
			  out.writeUTF("minigames");
			player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		}
		}
		return false;
	}
	
	//Command
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		
		String message = e.getMessage();
		Player player = e.getPlayer();
		
		if(player.isOp()) {
			//laat door
			e.setCancelled(false);
		} else {
			// get string
			
			//help
			if(message.startsWith("/help")) {
				player.sendMessage(ChatColor.DARK_GRAY + "Zie hier de beschikbare commando's: ");
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/server survival" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de survival server!" );
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/server minigames" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de minigames server!" );
				e.setCancelled(true);
			} else if(message.equalsIgnoreCase("/survival")) {
				e.setCancelled(false);
			} else if(message.equalsIgnoreCase("/minigames")) {
				e.setCancelled(false);
			} else {
				e.setCancelled(true);
			}
		}
		
	}

	//Chat
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();
		String name = e.getPlayer().getName();
		String message = e.getMessage();
		if(player.isOp()) {
			e.setFormat(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " >> " + ChatColor.RESET + message);
		} else {
			e.setFormat(ChatColor.GRAY + name + ChatColor.DARK_GRAY + " >> " + ChatColor.RESET + message);
		}
	}
	
	//Drop
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		Player player = (Player) e.getPlayer();
		if(player.isOp()) {
			//niks
		} else {
			e.setCancelled(true);
		}
	}
	
	//Interact
	@EventHandler
	@SuppressWarnings("deprecation")
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		ItemStack stack1 = new ItemStack(Material.GRASS_BLOCK);
		ItemStack stack2 = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack stack3 = new ItemStack(Material.RED_WOOL);
		if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
			if(player.getItemInHand().equals(stack1)) {
				player.sendMessage(ChatColor.GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + "survival");
				//BUNGEE
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				  out.writeUTF("Connect");
				  out.writeUTF("survival");
				player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} else if(player.getItemInHand().equals(stack2)) {
				player.sendMessage(ChatColor.GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + "minigames");
				//BUNGEE
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				  out.writeUTF("Connect");
				  out.writeUTF("minigames");
				player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
			} else if(player.getItemInHand().equals(stack3)) {
				player.sendMessage(ChatColor.GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + "pixelmon");
				//BUNGEE
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				  out.writeUTF("Connect");
				  out.writeUTF("sponge");
				player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
		} else {
			if(player.isOp()) {
				//niks
			} else {
				e.setCancelled(true);
			}
		}
		}
		}

	//Inv click
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if(player.isOp()) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
	}
	
	//No Damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		e.setCancelled(true);
	}
	
	//Block break
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		//speler
		Player player = e.getPlayer();
		
		if(player.isOp()) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
		
	}
	
	//Block place
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		//speler
		Player player = e.getPlayer();
				
		if(player.isOp()) {
			e.setCancelled(false);
		} else {
			e.setCancelled(true);
		}
	}
	
	//Weer
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
	  e.setCancelled(e.toWeatherState());
	}

	//Listener bungee
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
	    if (!channel.equals("BungeeCord")) {
	      return;
	    }
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("SomeSubChannel")) {
	      // Use the code sample in the 'Response' sections below to read
	      // the data.
	    }
	  }
	
	
}
