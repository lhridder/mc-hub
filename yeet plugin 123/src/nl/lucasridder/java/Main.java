package nl.lucasridder.java;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.server.v1_16_R2.IChatBaseComponent;
import net.minecraft.server.v1_16_R2.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.output.ByteArrayOutputStream;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
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
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener, PluginMessageListener {

	//variabelen
		int all;
		HashMap<Player, String> PlayerBoolean = new HashMap<>();
		HashMap<Player, Boolean> Staff = new HashMap<>();
		HashMap<Player, Boolean> Invis = new HashMap<>();

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

		//remove player from PlayerBoolean
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
			@Override
			public void run() {
				PlayerBoolean.remove(player);
			}
		}, 5*20L); //20 Tick (1 Second) delay before run() is called
	}

	//playercount
	public void playerCount() {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress("vps3.lucasridder.nl", 25565), 1000);

			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			DataInputStream in = new DataInputStream(socket.getInputStream());

			out.write(0xFE);

			StringBuilder str = new StringBuilder();

			int b;
			while ((b = in.read()) != -1) {
				if (b > 16 && b != 255 && b != 23 && b != 24) {
					str.append((char) b);
				}
			}

			String[] data = str.toString().split("§");
			this.all = Integer.parseInt(data[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//inventory player
	public void setPlayerInventory(Player player) {
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

	//inventory staff
	public void setStaffInventory(Player player) {
		if(Staff.containsKey(player)) {
			if (Invis.containsKey(player)) {
				//invis on state item
				ItemStack stack5 = new ItemStack(Material.GREEN_DYE);
				ItemMeta meta5 = stack5.getItemMeta();
				meta5.setDisplayName(ChatColor.GOLD + "Disable invis mode.");
				stack5.setItemMeta(meta5);
				player.getInventory().setItem(7, stack5);
			} else {
				//invis off state item
				ItemStack stack4 = new ItemStack(Material.GRAY_DYE);
				ItemMeta meta4 = stack4.getItemMeta();
				meta4.setDisplayName(ChatColor.GOLD + "Enable invis mode.");
				stack4.setItemMeta(meta4);
				player.getInventory().setItem(7, stack4);
			}
			//Leave staffmode item
			ItemStack stack6 = new ItemStack(Material.RED_BED);
			ItemMeta meta6 = stack6.getItemMeta();
			meta6.setDisplayName(ChatColor.GOLD + "Leave staffmode.");
			stack6.setItemMeta(meta6);
			player.getInventory().setItem(8, stack6);
			//finish
			player.updateInventory();
		} else {
			setPlayerInventory(player);
		}
	}

	//update scoreboard
	public void updateScoreboard(Player player) {

		ScoreboardManager manager = Bukkit.getScoreboardManager();
		Scoreboard b = manager.getNewScoreboard();

		Objective o = b.registerNewObjective("Gold", "", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Lobby");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);

		//staff 4 t/m 6
			//check if player has staff permission
			if(player.isOp()) {
				//spacer 7
				o.getScore(ChatColor.RED + "").setScore(7);

				//if staff mode enabled
				if(Staff.containsKey(player)) {
					//staff on 6
					o.getScore(ChatColor.DARK_GREEN + "Staffmode: " + ChatColor.GREEN + "✔").setScore(6);
					//check invis
					if(Invis.containsKey(player)) {
						//invis on 5
						o.getScore(ChatColor.BLUE + "  Invisability: " + ChatColor.GREEN + "✔").setScore(5);
					} else {
						//invis off 5
						o.getScore(ChatColor.BLUE + "  Invisability: " + ChatColor.RED + "✘").setScore(5);
					}
				} else {
					//staff off 6
					o.getScore(ChatColor.DARK_GREEN + "Staffmode: " + ChatColor.RED + "✘").setScore(6);
				}

			} else {
				//spacer 6
				o.getScore(ChatColor.RED + "").setScore(6);

				//Welkom *speler* 5
				o.getScore(ChatColor.YELLOW + "Welkom, " + ChatColor.GRAY + player.getName()).setScore(5);
			}

		//spacer 4
		o.getScore(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "").setScore(4);

		//totaal spelers proxy 3
		o.getScore(ChatColor.YELLOW + "Totaal spelers: " + ChatColor.RED + this.all).setScore(3);

		//totaal spelers hub 2
		int spelers = getServer().getOnlinePlayers().size();
		int invis = Invis.size();
		int hub = spelers - invis;
		o.getScore(ChatColor.BLUE + "  Hub: " + ChatColor.RED + hub).setScore(2);


		//spacer 1
		o.getScore("").setScore(1);

		//server footer 0
		o.getScore(ChatColor.GREEN + "LucasRidder.nl").setScore(0);

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
		//chat
		String name = player.getName();
		clearChat(player);
		player.sendMessage("  " + ChatColor.DARK_GRAY + "Welkom, " + ChatColor.GOLD + name);
		player.sendMessage("  " + ChatColor.BLUE + "Beschikbare servers: ");
		player.sendMessage("  " + ChatColor.GOLD + "/survival" + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + "/minigames" + ChatColor.DARK_GRAY + " en " + ChatColor.GOLD + "/kitpvp");
		player.sendMessage("");
		player.sendMessage("");

		//title
		PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§aWelkom!\"}"), 20, 40, 20);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"§bIn de hub\"}"), 20, 40, 20);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(subtitle);
	}

	//setTablist
	/*
	public void setTablist(Player player) {
		CraftPlayer cplayer = (CraftPlayer) player;
		PlayerConnection connection = cplayer.getHandle().playerConnection;
		String header = ChatColor.GOLD + "Welkom op de LucasRidder server" + ChatColor.GREEN + player.getName();
		IChatBaseComponent top = IChatBaseComponent.ChatSerializer.a("{text: '" + header + "'}");
		PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();
		try {
			Field headerField = packet.getClass().getDeclaredField("a");
			headerField.setAccessible(true);
			headerField.set(packet, top);
			headerField.setAccessible(!headerField.isAccessible());
		} catch (Exception e) {
			e.printStackTrace();
		}
		connection.sendPacket(packet);
	}
	*/

	//invisOn method
	public void invisOn(Player player) {
		if(!Staff.containsKey(player)) {
			//make player staff
			Staff.put(player, true);
			staffOn(player);
		} else {
			for (Player players : Bukkit.getOnlinePlayers()) {
				//check the players if they are staff
				if(!Invis.containsKey(players)) {
					players.hidePlayer(this, player);
				}
			}
			Invis.put(player, true);
			player.sendMessage(ChatColor.GOLD + "Your invisability was: " + ChatColor.GREEN + "enabled.");
			setStaffInventory(player);
		}
	}

	//invisOff method
	public void invisOff(Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.showPlayer(this, player);
		}
		Invis.remove(player);
		player.sendMessage(ChatColor.GOLD + "Your invisability was: " + ChatColor.RED + "disabled.");
		setStaffInventory(player);
		UUID uuid = player.getUniqueId();
		getConfig().set("player." + uuid + ".invis", false);
		saveConfig();
	}

	//staff on method
	public void staffOn(Player player) {

		player.sendMessage(ChatColor.GOLD + "Staff mode has been: " + ChatColor.GREEN + "enabled!");
		Staff.put(player, true);
		setStaffInventory(player);
		UUID uuid = player.getUniqueId();
		if(getConfig().getBoolean("player." + uuid + ".invis")) {
			invisOn(player);
		}
	}

	//staff off method
	public void staffOff(Player player) {
		if(Invis.containsKey(player)) {
			invisOff(player);
		}

		player.sendMessage(ChatColor.GOLD + "Staff mode has been: " + ChatColor.RED + "disabled!");
		Staff.remove(player);
		setStaffInventory(player);
		UUID uuid = player.getUniqueId();
		getConfig().set("player." + uuid + ".staff", false);
		saveConfig();
	}

	//staff error
	public void staffError(Player player) {
		player.sendMessage(ChatColor.RED + "Enable staffmode first: " + ChatColor.AQUA + "/staff");
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

		//check which inv is needed
		UUID uuid = player.getUniqueId();
		if(getConfig().getBoolean("player." + uuid + ".staff")) {
			staffOn(player);
		} else {
			//set inv
			setPlayerInventory(player);
		}

		//scoreboard
		new BukkitRunnable() {
			public void run() {
				if (!player.isOnline()) {
					this.cancel();
				} else {
					updateScoreboard(player);
				}
			}
		}.runTaskTimer(this, 20, 100);

		//tablist
		if(player.isOp()) {
			player.setPlayerListName(ChatColor.RED + player.getName());
		} else {
			player.setPlayerListName(ChatColor.YELLOW + player.getName());
		}

		//check config for player invis info
		if(!Invis.containsKey(player)) {
			//join message
			if (player.isOp()) {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + name);
			} else if (!player.hasPlayedBefore()) {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "Welkom " + ChatColor.RESET + name);
			} else {
				//Join message
				e.setJoinMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "+" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + name);
			}
			motd(player);
		} else {
			motd(player);
			e.setJoinMessage(null);
		}

		//get invis staff and nametag
		for(Player players : this.getServer().getOnlinePlayers()) {
			if(Invis.containsKey(players)) {
				//check the players if they are staff
				if(!Invis.containsKey(players)) {
					players.hidePlayer(this, player);
				}
			}

		}

		//register player in config
		// UUID uuid = player.getUniqueId();
		getConfig().set("player." + uuid + ".name", player.getName());
		saveConfig();
		}

	//Leave
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		String name = player.getName();
		if(!Invis.containsKey(player)) {
			//bungee check
			String server = PlayerBoolean.get(player);
			if (server != null) {
				if (player.isOp()) {
					e.setQuitMessage(ChatColor.RED + name + ChatColor.DARK_RED + " -> " + ChatColor.GRAY + server);
				} else {
					e.setQuitMessage(ChatColor.YELLOW + name + ChatColor.DARK_RED + " -> " + ChatColor.GRAY + server);
				}
				PlayerBoolean.remove(player);
			} else {
				//leave message
				if (player.isOp()) {
					e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.RED + name);
				} else {
					e.setQuitMessage(ChatColor.DARK_GRAY + "[" + ChatColor.DARK_RED + "-" + ChatColor.DARK_GRAY + "] " + ChatColor.YELLOW + name);
				}
			}
		} else {
			//cancel quit message
			e.setQuitMessage(null);
			UUID uuid = player.getUniqueId();
			getConfig().set("player." + uuid + ".invis", true);
			saveConfig();
			Invis.remove(player);
		}
		if(Staff.containsKey(player)) {
			UUID uuid = player.getUniqueId();
			getConfig().set("player." + uuid + ".staff", true);
			saveConfig();
			Staff.remove(player);
		}
	}

	//Admin Commands
	@SuppressWarnings("NullableProblems")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if(cmd.getName().equalsIgnoreCase("gamemode")) {
			if(sender.hasPermission("survival.admin")) {
				//check of sender speler is
				//te weinig argumenten
				if (args.length == 0) {
					sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
					return true;
				}

				//teveel argumenten
				if (args.length > 2) {
					sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
					return true;
				}

				//goede aantal argumenten
				if (args.length == 1) {
					//pak speler
					Player player = (Player) sender;

					if (args[0].equalsIgnoreCase("creative") | args[0].equalsIgnoreCase("1")) {
						player.setGameMode(GameMode.CREATIVE);
						sender.sendMessage(ChatColor.GREEN + "Gedaan.");
						return true;
					} else if (args[0].equalsIgnoreCase("survival") | args[0].equalsIgnoreCase("0")) {
						player.setGameMode(GameMode.SURVIVAL);
						sender.sendMessage(ChatColor.GREEN + "Gedaan.");
						return true;
					} else if (args[0].equalsIgnoreCase("spectator") | args[0].equalsIgnoreCase("3")) {
						player.setGameMode(GameMode.SPECTATOR);
						sender.sendMessage(ChatColor.GREEN + "Gedaan.");
						return true;
					} else if (args[0].equalsIgnoreCase("adventure") | args[0].equalsIgnoreCase("2")) {
						player.setGameMode(GameMode.ADVENTURE);
						sender.sendMessage(ChatColor.GREEN + "Gedaan.");
						return true;
					} else
						sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
					return true;
				}

				//andere speler
				if (args.length == 2) {
					//pak speler
					Player target = Bukkit.getServer().getPlayer(args[1]);
					if (target == null) {
						sender.sendMessage(ChatColor.RED + "Doel is niet online");
						return true;
					} else {

						if (args[0].equalsIgnoreCase("creative") | args[0].equalsIgnoreCase("1")) {
							target.setGameMode(GameMode.CREATIVE);
							target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
							sender.sendMessage(ChatColor.GREEN + "Gedaan.");
							return true;
						} else if (args[0].equalsIgnoreCase("survival") | args[0].equalsIgnoreCase("0")) {
							target.setGameMode(GameMode.SURVIVAL);
							target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
							sender.sendMessage(ChatColor.GREEN + "Gedaan.");
							return true;
						} else if (args[0].equalsIgnoreCase("spectator") | args[0].equalsIgnoreCase("3")) {
							target.setGameMode(GameMode.SPECTATOR);
							target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
							sender.sendMessage(ChatColor.GREEN + "Gedaan.");
							return true;
						} else if (args[0].equalsIgnoreCase("adventure") | args[0].equalsIgnoreCase("2")) {
							target.setGameMode(GameMode.ADVENTURE);
							target.sendMessage(ChatColor.GREEN + "Gamemode aangepast.");
							sender.sendMessage(ChatColor.GREEN + "Gedaan.");
							return true;
						} else {
							sender.sendMessage(ChatColor.RED + "/gamemode (creative/survival/spectator/adventure)/(0/1/2/3) (speler)");
							return true;
						}
					}
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Geen toegang tot dit commando");
				return true;
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
		
		//survival
		if(cmd.getName().equalsIgnoreCase("survival")) { sendServer("survival", (Player) sender); }
		
		//minigames
		if(cmd.getName().equalsIgnoreCase("minigames")) { sendServer("minigames", (Player) sender); }

		//kitpvp
		if(cmd.getName().equalsIgnoreCase("kitpvp")) { sendServer("pvp", (Player) sender); }

		//stop
		if(cmd.getName().equalsIgnoreCase("stop")) {
			sender.sendMessage(ChatColor.GREEN + "Kicking all players...");
			if(this.getServer().getOnlinePlayers().size() != 0) {
				for (Player players : this.getServer().getOnlinePlayers()) {
					if (!players.equals(sender)) {
						players.kickPlayer(ChatColor.GRAY + "De server wordt momenteel herstart" + "\n" +
								ChatColor.BLUE + "wacht even met opnieuw joinen" + "\n" +
								ChatColor.YELLOW + "Zie actuele status via: " + ChatColor.AQUA + "https://www.discord.gg/AzVCaQE");
					}
				}
			}
			sender.sendMessage(ChatColor.GREEN + "Stopping server...");
			System.out.println("[HUB]" + ChatColor.DARK_RED + " stopping server...");
			Bukkit.shutdown();
		}

		//vanish
		if(cmd.getName().equalsIgnoreCase("vanish")) {
			if (Invis.containsKey((Player) sender)) {
				invisOff((Player) sender);
			} else {
				invisOn((Player) sender);
			}
		}

		//staff
		if(cmd.getName().equalsIgnoreCase("staff")) {
			if (Staff.containsKey((Player) sender)) {
				staffOff((Player) sender);
			} else {
				staffOn((Player) sender);
			}
		}

		//playtime
		if(cmd.getName().equalsIgnoreCase("playtime")) {
			//te weinig argumenten
			if (args.length == 0) {
				Player player = (Player) sender;
				String name = player.getName();
				//get source
				int ptt = player.getStatistic(Statistic.PLAY_ONE_MINUTE);
				//calculate
				int pts = ptt / 20; //seconds
				int ptm = pts / 60; //minutes
				int pth = ptm / 60; //hours

				int rm = ptm - (pth*60); //res minutes
				int rs = pts - (ptm*60); //res seconds

				//report
				player.sendMessage(ChatColor.GREEN + "Hub playtime: "
						+ ChatColor.GOLD + pth + ChatColor.GREEN + " uren, "
						+ ChatColor.GOLD + rm + ChatColor.GREEN + " minuten en "
						+ ChatColor.GOLD + rs + ChatColor.GREEN + " seconden");
				return true;
			} else {
				//goed
				Player target = Bukkit.getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage(ChatColor.RED + "Doel is niet online");
					return true;
				}
				//get source
				int ptt = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
				String name = target.getName();
				//calculate
				int pts = ptt / 20; //seconds
				int ptm = pts / 60; //minutes
				int pth = ptm / 60; //hours

				int rm = ptm - (pth*60); //res minutes
				int rs = pts - (ptm*60); //res seconds

				//report
				target.sendMessage(ChatColor.GREEN + "Hub playtime: "
						+ ChatColor.GOLD + name + ChatColor.GREEN + ": "
						+ ChatColor.GOLD + pth + ChatColor.GREEN + " uren, "
						+ ChatColor.GOLD + rm + ChatColor.GREEN + " minuten en "
						+ ChatColor.GOLD + rs + ChatColor.GREEN + " seconden");
				return true;
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
				player.sendMessage(ChatColor.DARK_GRAY + " - " + ChatColor.AQUA + "/kitpvp" + ChatColor.DARK_GRAY + " : " + ChatColor.GOLD + "Ga naar de kitpvp server!" );
				e.setCancelled(true);
			} else {
				e.setCancelled(!(message.equalsIgnoreCase("/survival")
						| message.equalsIgnoreCase("/minigames")
						| message.equalsIgnoreCase("/kitpvp")
						| message.startsWith("/playtime")
						| message.startsWith("/pt") ));
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
			e.setFormat(ChatColor.GOLD + name + ChatColor.DARK_GRAY + " » " + ChatColor.RESET + message);
		} else {
			e.setFormat(ChatColor.GRAY + name + ChatColor.DARK_GRAY + " » " + ChatColor.RESET + message);
		}
	}

	//Interact
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player player = e.getPlayer();

		//standard stacks
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

		//invis on state item
		ItemStack stack5 = new ItemStack(Material.GREEN_DYE);
		ItemMeta meta5 = stack5.getItemMeta();
		meta5.setDisplayName(ChatColor.GOLD + "Disable invis mode.");
		stack5.setItemMeta(meta5);
		//invis off state item
		ItemStack stack4 = new ItemStack(Material.GRAY_DYE);
		ItemMeta meta4 = stack4.getItemMeta();
		meta4.setDisplayName(ChatColor.GOLD + "Enable invis mode.");
		stack4.setItemMeta(meta4);
		//Leave staffmode item
		ItemStack stack6 = new ItemStack(Material.RED_BED);
		ItemMeta meta6 = stack6.getItemMeta();
		meta6.setDisplayName(ChatColor.GOLD + "Leave staffmode.");
		stack6.setItemMeta(meta6);

		if(e.getAction() == Action.RIGHT_CLICK_BLOCK | e.getAction() == Action.RIGHT_CLICK_AIR) {

			//staff items
			if(player.isOp()) {
				if(player.getInventory().getItemInMainHand().equals(stack4)) { invisOn(player); return; }
				if(player.getInventory().getItemInMainHand().equals(stack5)) { invisOff(player); return; }
				if(player.getInventory().getItemInMainHand().equals(stack6)) { staffOff(player); return; }
			}

			//plebs
			if(player.getInventory().getItemInMainHand().equals(stack1)) {
				sendServer("survival", player);
				e.setCancelled(true);
				return;
			}
			if(player.getInventory().getItemInMainHand().equals(stack2)) {
				sendServer("minigames", player);
				e.setCancelled(true);
				return;
			}
			if(player.getInventory().getItemInMainHand().equals(stack3)) {
				sendServer("pvp", player);
				e.setCancelled(true);
				return;
			}

		}
		}

	//Crop break
	@EventHandler
	public void onEntityInteract(EntityInteractEvent e) {
		if (e.getBlock().getType() == Material.FARMLAND && e.getEntity() instanceof Player) e.setCancelled(true);
	}

	//TODO tab stop

	//Portals
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		Location loc = player.getLocation();
		//kijk water
		if(loc.getBlock().getType().equals(Material.WATER)) {
			//hoogte
			if(loc.getBlockY() >= 59 && loc.getBlockY() <= 64) {
				//links
				if(loc.getBlockX() == 16) {
					//survival
					if(loc.getBlockZ() >= 31 && loc.getBlockZ() <= 35) {
						//send server
						sendServer("survival", player);
					}
				}
				//rechts
				if(loc.getBlockX() == -16) {
					//minigames
					if(loc.getBlockZ() >= 31 && loc.getBlockZ() <= 35) {
						//send server
						sendServer("minigames", player);
					}
					//kitpvp
					if(loc.getBlockZ() >= 43 && loc.getBlockZ() <= 47) {
						//send server
						sendServer("kitpvp", player);
					}
				}
			}
		}
	}

	//Drop
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		//cancel if player is not in staff mode
		e.setCancelled(!Staff.containsKey(e.getPlayer()));
	}

	//Inv click
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		//cancel if player is not in staff mode
		if(!Staff.containsKey(player)) {
			e.setCancelled(true);
			if(player.isOp()) {
				staffError(player);
			}
		}
	}
	
	//No Damage
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		//cancel any damage
		e.setCancelled(true);
	}

	//Armorstand cancel
	@EventHandler
	public void armorStand(PlayerArmorStandManipulateEvent e) {
		//cancel if player is not in staff mode
		e.setCancelled(!Staff.containsKey(e.getPlayer()));
	}

	//Block break cancel
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		//cancel if player is not in staff mode
		e.setCancelled(!Staff.containsKey(e.getPlayer()));
		if(e.getPlayer().isOp()) {
			staffError(e.getPlayer());
		}
	}
	
	//Block place cancel
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		//cancel if player is not in staff mode
		e.setCancelled(!Staff.containsKey(e.getPlayer()));
		if(e.getPlayer().isOp()) {
			staffError(e.getPlayer());
		}
	}
	
	//Weer
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent e){
		e.setCancelled(e.toWeatherState());
	}

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
