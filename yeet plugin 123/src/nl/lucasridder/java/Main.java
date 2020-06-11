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
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;


public class Main extends JavaPlugin implements Listener, PluginMessageListener {

	//variabelen
		int all = 0;
		boolean lock;
		String lockreason;
		HashMap<Player, String> PlayerBoolean = new HashMap<>();

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
		PlayerBoolean.put(player, server);
	}

	//playercount
	public void playerCount() {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("vps2.lucasridder.nl", 25565), 1000);

			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			out.write(0xFE);

			StringBuilder str = new StringBuilder();

			int b;
			while ((b = in.read()) != -1) {
				if (b != 0 && b > 16 && b != 255 && b != 23 && b != 24) {
					str.append((char) b);
				}
			}

			String[] data = str.toString().split("§");
			this.all = Integer.parseInt(data[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//inventory
	public void setInventory(Player player) {
		player.getInventory().clear();
		ItemStack stack1 = new ItemStack(Material.GRASS_BLOCK);
		ItemStack stack2 = new ItemStack(Material.DIAMOND_SWORD);
		ItemStack stack3 = new ItemStack(Material.RED_WOOL);
		ItemMeta meta1 = stack1.getItemMeta();
		ItemMeta meta2 = stack2.getItemMeta();
		ItemMeta meta3 = stack3.getItemMeta();
		meta1.setDisplayName(ChatColor.GOLD + "Join survival!");
		meta2.setDisplayName(ChatColor.GOLD + "Join minigames!");
		meta3.setDisplayName(ChatColor.GOLD + "Join kitpvp!");
		stack1.setItemMeta(meta1);
		stack2.setItemMeta(meta2);
		stack3.setItemMeta(meta3);
		player.getInventory().setItem(3, stack1);
		player.getInventory().setItem(4, stack2);
		player.getInventory().setItem(5, stack3);
		player.updateInventory();
	}

	//update scoreboard
	public void updateScoreboard(Player player) {
		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard b = manager.getNewScoreboard();

		String top = this.getConfig().getString("scoreboard.top");
		Objective o = b.registerNewObjective("Gold", "", ChatColor.BOLD + "" + ChatColor.BLUE + top);
		o.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score6 = o.getScore(ChatColor.YELLOW + "");
		score6.setScore(6);

		Score score5 = o.getScore(ChatColor.YELLOW + "Welkom, " + ChatColor.GRAY + player.getName());
		score5.setScore(5);

		Score score4 = o.getScore(ChatColor.BOLD + "");
		score4.setScore(4);

		Score score3 = o.getScore(ChatColor.GOLD + "Totaal aantal spelers: " + ChatColor.RED + this.all);
		score3.setScore(3);

		int spelers = getServer().getOnlinePlayers().size();
		Score score2 = o.getScore(ChatColor.BLUE + " - Hub: " + ChatColor.RED + spelers);
		score2.setScore(2);

		Score score1 = o.getScore("");
		score1.setScore(1);

		String name = this.getConfig().getString("scoreboard.name");
		Score score0 = o.getScore(ChatColor.BOLD + "" + ChatColor.GREEN + name);
		score0.setScore(0);

		player.setScoreboard(b);
	}

	//clear chat
	public void clearChat(Player player) {
		int x = 0;
		while (x < 20){
			player.sendMessage("");
			x = x + 1;
		}
	}

	//motd
	public void motd(Player player) {
		String name = player.getName();
		clearChat(player);
		player.sendMessage("  " + ChatColor.DARK_GRAY + "Welkom, " + ChatColor.GOLD + name);
		player.sendMessage("  " + ChatColor.BLUE + "Beschikbare servers: ");
		player.sendMessage("  " + ChatColor.GOLD + "/survival" + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + "/minigames" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/kitpvp");
		player.sendMessage("");
		player.sendMessage("");
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

        //start counter
		new BukkitRunnable() {
			public void run() {
				if(getServer().getOnlinePlayers().size() != 0) {
					playerCount();
				}

			}
		}.runTaskTimer(this, 20, 100);

		//enable
		System.out.println("[HUB]" + ChatColor.GREEN + " succesfully enabled");
	}
	
	//Power-down
	@Override
	public void onDisable() {
		// shut down plugin
		this.saveConfig();
		//stop scoreboard
		for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
			onlinePlayers.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		//finish
		System.out.println("[HUB]" + ChatColor.GREEN + " succesfully disabled");
	}

	//Join
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		String name = player.getName();
		//lock
		if(!player.isOp()) {
			if (this.lock) {
				player.kickPlayer(ChatColor.GRAY + "De server is momenteel in lockdown vanwege:" + "\n" +
						ChatColor.BLUE + this.lockreason + "\n" +
						ChatColor.YELLOW + "Zie actuele status via: " + ChatColor.AQUA + "https://www.discord.gg/AzVCaQE");
			}
		}

		//spawn loc
		try {
			int x = this.getConfig().getInt("spawn.x");
			int y = this.getConfig().getInt("spawn.y");
			int z = this.getConfig().getInt("spawn.z");
			float yaw = this.getConfig().getInt("spawn.yaw");
			float pitch = this.getConfig().getInt("spawn.pitch");
			Location loc = new Location(player.getWorld(), x, y, z, pitch, yaw);
			player.teleport(loc);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

			//set inv
			setInventory(player);

			//scoreboard
			new BukkitRunnable() {
				public void run() {
					if (!player.isOnline()) this.cancel();
					else {
						updateScoreboard(player);
					}

				}
			}.runTaskTimer(this, 20, 20);

			//join message
			if (player.isOp()) {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + name);

				//motd
				motd(player);

				if (lock) {
					player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Lockdown bypassed: " + lockreason);
				}
			} else if (!player.hasPlayedBefore()) {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "Welkom " + ChatColor.RESET + name);

				motd(player);
			} else {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + name);

				//motd
				motd(player);
			}
		}

	//Leave
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		String name = player.getName();
		//bungee check
		String server = PlayerBoolean.get(player);
		if (server != null) {
			if (player.hasPermission("survival.admin")) {
				e.setQuitMessage(ChatColor.RED + name + ChatColor.DARK_RED + " -> " + ChatColor.GRAY + server);
			} else {
				e.setQuitMessage(ChatColor.WHITE + name + ChatColor.DARK_RED + " -> " + ChatColor.GRAY + server);
			}
			PlayerBoolean.remove(player);
		} else {
			//leave message
			if (player.hasPermission("survival.admin")) {
				e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + name);
			} else {
				e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET + name);
			}
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
		if(cmd.getName().equalsIgnoreCase("survival")) { sendServer("survival", (Player) sender); }
		
		//minigames
		if(cmd.getName().equalsIgnoreCase("minigames")) { sendServer("minigames", (Player) sender); }

		//kitpvp
		if(cmd.getName().equalsIgnoreCase("kitpvp")) { sendServer("kitpvp", (Player) sender); }

		//lock
		if(cmd.getName().equalsIgnoreCase("lock")) {
			if(!this.lock) {
				lock = true;
				if (args.length == 0) {
					this.lockreason = "onbekend";
					sender.sendMessage(ChatColor.GREEN + "Lockdown geactiveerd!");
				} else {
					this.lockreason = args[0];
					sender.sendMessage(ChatColor.GREEN + "Lockdown geactiveerd!");
					sender.sendMessage(ChatColor.GRAY + "Reden: " + ChatColor.GOLD + this.lockreason);
				}
				//kick all players
				sender.sendMessage(ChatColor.GREEN + "Kicking all players...");
				for(Player players : this.getServer().getOnlinePlayers()) {
					if(!players.isOp()) {
						players.kickPlayer(ChatColor.GRAY + "De server is momenteel in lockdown vanwege:" + "\n" +
								ChatColor.BLUE + this.lockreason + "\n" +
								ChatColor.YELLOW + "Zie actuele status via: " + ChatColor.AQUA + "https://www.discord.gg/AzVCaQE");
					} else {
						players.sendMessage(ChatColor.GREEN + "Lockdown geactiveerd | Kick Bypassed");
					}
				}
				System.out.println("[HUB]" + ChatColor.DARK_RED + " Lockdown activated by " + ChatColor.GREEN + sender.getName() + ChatColor.RED + "!");
			} else {
				this.lock = false;
				sender.sendMessage(ChatColor.GREEN + "Lockdown opgeheven");
			}
		}

		//motd
		if(cmd.getName().equalsIgnoreCase("motd")) {
			if(!(sender instanceof Player)) {
				//zeg het
				sender.sendMessage(ChatColor.RED + "Je bent geen speler");
				return true;
			} else {
				motd((Player) sender);
			}
		}

		//stop
		if(cmd.getName().equalsIgnoreCase("stop")) {
			sender.sendMessage(ChatColor.GREEN + "Kicking all players...");
			for(Player players : this.getServer().getOnlinePlayers()) {
				if(!players.equals(sender)) {
					players.kickPlayer(ChatColor.GRAY + "De server wordt momenteel herstart" + "\n" +
							ChatColor.BLUE + "wacht even met opnieuw joinen" + "\n" +
							ChatColor.YELLOW + "Zie actuele status via: " + ChatColor.AQUA + "https://www.discord.gg/AzVCaQE");
				}
				sender.sendMessage(ChatColor.GREEN + "Stopping server...");
				System.out.println("[HUB]" + ChatColor.DARK_RED + " stopping server...");
				Bukkit.shutdown();
			}

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
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/survival" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de survival server!" );
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/minigames" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de minigames server!" );
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/pixelmon" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de pixelmon server!" );
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
			e.setFormat(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " » " + ChatColor.RESET + message);
		} else {
			e.setFormat(ChatColor.GRAY + name + ChatColor.DARK_GRAY + " » " + ChatColor.RESET + message);
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
		meta3.setDisplayName(ChatColor.GOLD + "Join kitpvp!");
		stack1.setItemMeta(meta1);
		stack2.setItemMeta(meta2);
		stack3.setItemMeta(meta3);

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
			if(player.getInventory().getItemInMainHand().equals(stack1)) {
				sendServer("survival", player);
				} else if(player.getInventory().getItemInMainHand().equals(stack2)) {
				sendServer("minigames", player);
				} else if(player.getInventory().getItemInMainHand().equals(stack3)) {
				sendServer("kitpvp", player);
			} else {
				if (!player.isOp()) { e.setCancelled(true); }
			}
		}
		}

	//Crop break
	public void onEntityInteract(EntityInteractEvent e) {
		if (e.getBlock().getType() == Material.FARMLAND && e.getEntity() instanceof Player) e.setCancelled(true);
	}

	//tab stop
		//soon

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
	@SuppressWarnings({"NullableProblems", "UnstableApiUsage"})
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
	}
}
