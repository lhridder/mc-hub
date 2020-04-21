package nl.lucasridder.java;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;


public class Main extends JavaPlugin implements Listener, PluginMessageListener {

	//variabelen
		int survival = 0;
		int minigames = 0;
		int lobby = 0;
		int pixelmon = 0;
		int all = 0;
		boolean lock;
		String lockreason = "";

	//send server
	public void sendServer(String server, Player player) {
		player.sendMessage(ChatColor.DARK_GRAY + "Je wordt nu doorverbonden naar: " + ChatColor.GOLD + server);
		//BUNGEE
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			e.printStackTrace();
		}
		player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
	}

	//Start-up
	@Override
	public void onEnable() {
		// start up plugin
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	    this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		
		//stop tijd
		World world = Bukkit.getServer().getWorld("world");
		if (world != null) {
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
		}

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
		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
			Scoreboard scoreboard = null;
			onlinePlayers.setScoreboard(scoreboard);
		}
	}
	
	//Join
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		//player info
		Player player = e.getPlayer();
		String name = player.getName();

		//lock
		if(!player.isOp()) {
			if (lock) {
				player.kickPlayer(ChatColor.GRAY + "De server is momenteel in lockdown vanwege:" +
						ChatColor.BLUE + lockreason +
						ChatColor.YELLOW + "Zie actuele status via: " + ChatColor.AQUA + "https://www.discord.gg/AzVCaQE");
				return;
			}


			//spawn loc
			try {
				int x = this.getConfig().getInt("spawn.x");
				int y = this.getConfig().getInt("spawn.y");
				int z = this.getConfig().getInt("spawn.z");
				Location loc = new Location(player.getWorld(), x, y, z);
				player.teleport(loc);
			} catch (Exception e1) {
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
			new BukkitRunnable() {
				public void run() {
					if (!player.isOnline()) {
						this.cancel();
					} else {
						ScoreboardManager manager = Bukkit.getScoreboardManager();
						Scoreboard b = manager.getNewScoreboard();
						int spelers = getServer().getOnlinePlayers().size();

						Objective o = b.registerNewObjective("Gold", "", ChatColor.BOLD + "" + ChatColor.BLUE + "Lobby");
						o.setDisplaySlot(DisplaySlot.SIDEBAR);

						Score score5 = o.getScore(ChatColor.YELLOW + "");
						score5.setScore(5);

						Score score4 = o.getScore(ChatColor.YELLOW + "Welkom, " + ChatColor.GRAY + player.getName());
						score4.setScore(4);

						Score score3 = o.getScore(ChatColor.BOLD + "");
						score3.setScore(3);

						Score score2 = o.getScore(ChatColor.GOLD + "Aantal spelers online: " + ChatColor.RED + spelers);
						score2.setScore(2);

						Score score1 = o.getScore("");
						score1.setScore(1);

						Score score0 = o.getScore(ChatColor.BOLD + "" + ChatColor.GREEN + "VPS.LucasRidder.NL");
						score0.setScore(0);

						player.setScoreboard(b);
					}

				}
			}.runTaskTimer(this, 20, 20);

			//join message
			if (player.isOp()) {
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
				player.sendMessage(ChatColor.WHITE + "   Welkom, " + ChatColor.GOLD + name);
				player.sendMessage(ChatColor.WHITE + "   Jij bent een " + ChatColor.GOLD + "Admin");
				player.sendMessage(ChatColor.BLUE + "   Beschikbare servers: ");
				player.sendMessage(ChatColor.GOLD + "   /survival" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/minigames");
				player.sendMessage("");
				if (lock) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Lockdown bypassed: " + lockreason);
				}
			} else if (!player.hasPlayedBefore()) {
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
	}

	//Leave
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
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
	@SuppressWarnings("NullableProblems")
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
					} else sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");

				}

				//andere speler
				if(args.length == 2 ) {
					//pak speler
					Player target = Bukkit.getServer().getPlayer(args[1]);
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
			sendServer("survival", (Player) sender);
				}
		}
		
		//minigames
		if(cmd.getName().equalsIgnoreCase("minigames")) {
			if(!(sender instanceof Player)) {
				//zeg het
					sender.sendMessage(ChatColor.RED + "Je bent geen speler");
					return true;
				} else {
				sendServer("minigames", (Player) sender);
			}
		}

		//pixelmon
		if(cmd.getName().equalsIgnoreCase("pixelmon")) {
			if(!(sender instanceof Player)) {
				//zeg het
				sender.sendMessage(ChatColor.RED + "Je bent geen speler");
				return true;
			} else {
				sendServer("sponge", (Player) sender);
			}
		}

		//lock
		if(cmd.getName().equalsIgnoreCase("lock")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++){
				sb.append(args[i]).append(" ");
			}
			String lockreason = sb.toString().trim();
			sender.sendMessage(ChatColor.GREEN + "Gelukt met reden: " + ChatColor.GOLD + lockreason);
			this.lockreason = lockreason;
		}
		return true;
	}
	
	//Command
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		String message = e.getMessage();
		Player player = e.getPlayer();
		if (!player.isOp()) {
			//help
			if(message.startsWith("/help")) {
				player.sendMessage(ChatColor.DARK_GRAY + "Zie hier de beschikbare commando's: ");
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/server survival" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de survival server!" );
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/server minigames" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de minigames server!" );
				e.setCancelled(true);
			} else if(message.equalsIgnoreCase("/survival")) { e.setCancelled(false);
			} else e.setCancelled(!message.equalsIgnoreCase("/minigames"));
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

	//Interact
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();
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

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(player.getInventory().getItemInMainHand().equals(stack1)) {
				sendServer("survival", player);
				} else if(player.getInventory().getItemInMainHand().equals(stack2)) {
				sendServer("minigames", player);
				} else if(player.getInventory().getItemInMainHand().equals(stack3)) {
				sendServer("sponge", player);
			} else {
				if (!player.isOp()) { e.setCancelled(true); }
			}
		}
		}

	//Drop
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) { e.setCancelled(!e.getPlayer().isOp()); }

	//Inv click
	@EventHandler
	public void onClick(InventoryClickEvent e) { e.setCancelled(!e.getWhoClicked().isOp()); }
	
	//No Damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) { e.setCancelled(true); }

	//Armorstand
	@EventHandler
	public void armorStand(PlayerArmorStandManipulateEvent e) { e.setCancelled(!e.getPlayer().isOp()); }

	//Block break
	@EventHandler
	public void onBreak(BlockBreakEvent e) { e.setCancelled(!e.getPlayer().isOp()); }
	
	//Block place
	@EventHandler
	public void onPlace(BlockPlaceEvent e) { e.setCancelled(!e.getPlayer().isOp()); }
	
	//Weer
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){ e.setCancelled(e.toWeatherState()); }

	//Listener bungee
	@SuppressWarnings("NullableProblems")
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) return;
		if (!player.isOnline()) {
			@SuppressWarnings("UnstableApiUsage") ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			if (subchannel.equals("PlayerCount")) {
				@SuppressWarnings("unused")
				String server = in.readUTF();
				if (server.equalsIgnoreCase("lobby")) this.lobby = in.readInt();
				if (server.equalsIgnoreCase("ALL")) this.all = in.readInt();
				if (server.equalsIgnoreCase("survival")) this.survival = in.readInt();
				if (server.equalsIgnoreCase("sponge")) this.pixelmon = in.readInt();
				if (server.equalsIgnoreCase("minigames")) this.minigames = in.readInt();

				System.out.println("lobby:" + lobby + " survival: " + survival + " pixelmon: " + pixelmon + " minigames: " + minigames + " ALL: " + all);
			}
		}
	}

}
