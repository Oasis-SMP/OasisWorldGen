package oasis.worldgen;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.World.Environment;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;

public class WorldGen extends JavaPlugin implements CommandExecutor {
	
	@Override
	public void onEnable() {

		// Init commandshh
		getCommand("worldgen");
		getCommand("generate");
		getCommand("addglobal");
		getCommand("deal");

		// Save our logger for utility work later
		Utility.log = getLogger();
		Utility.log.info("Plugin has been enabled!");
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		if(id.equalsIgnoreCase("lavagen")){
			return new LavaGen();
		}
		if(id.equalsIgnoreCase("oceangen")){
			return new OceanGen();
		}
		if(getConfig().contains(worldName)){
			return new BlockGen(getConfig().getInt(worldName));
		}
		if(id.equalsIgnoreCase("skyblockworld")){
			return new SkyBlockWorld();
		}
		return new BlockGen(19);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		// Get player reference
		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		}

		if (cmd.getName().equalsIgnoreCase("addglobal")){
			getWorldGuard().getRegionManager(player.getWorld()).addRegion(new GlobalProtectedRegion("__Global__"));
			sender.sendMessage(ChatColor.AQUA + "Global region added for: " + player.getWorld().getName());
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("deal")){
			new BukkitRunnable() {
				int count = 1;
				String msg = ChatColor.translateAlternateColorCodes('&', "&a[&bOasis&cSMP&a] &c(â€¢_â€¢)");
				@Override
				public void run() {
					if(count==2){msg = ChatColor.translateAlternateColorCodes('&', "&a[&bOasis&cSMP&a] &c( â€¢_â€¢)>âŒ�â– -â– ");}
					if(count==3){msg = ChatColor.translateAlternateColorCodes('&', "&a[&bOasis&cSMP&a] &c(âŒ�â– _â– )");}
					if(count==4){
						msg = ChatColor.translateAlternateColorCodes('&', "&a[&bOasis&cSMP&a] &c(âŒ�â– _â– ) &bDeal with it!");
					}
					getServer().broadcastMessage(msg);
					if(count==4){this.cancel();}
					count++;
				}

			}.runTaskTimer(this, 30L, 30L);
		}

		if (cmd.getName().equalsIgnoreCase("generate")){
			if(args.length==1){
				if((args[0].equalsIgnoreCase("8")) || (args[0].equalsIgnoreCase("9"))){
					int count = 0;
					String ext = "";
					while (true) {
						if(count>0){ext=String.valueOf(count);}
						if (getServer().getWorld("Ocean" + ext)==null) {
							getMultiverseCore().getMVWorldManager().addWorld("Ocean" + ext, Environment.NORMAL, "34134525656",WorldType.NORMAL, false,"OasisWorldGen:OceanGen");
							getWorldGuard().getRegionManager(getServer().getWorld("Ocean" + ext)).addRegion(new GlobalProtectedRegion("__Global__"));
							return true;
						}
						count++;
					}
				}
				if((args[0].equalsIgnoreCase("10")) || (args[0].equalsIgnoreCase("11"))){
					int count = 0;
					String ext = "";
					while (true) {
						if(count>0){ext=String.valueOf(count);}
						if (getServer().getWorld("Lava" + ext)==null) {
							getMultiverseCore().getMVWorldManager().addWorld("Lava" + ext, Environment.NORMAL, "34134525656",WorldType.NORMAL, false,"OasisWorldGen:LavaGen");
							getWorldGuard().getRegionManager(getServer().getWorld("Lava" + ext)).addRegion(new GlobalProtectedRegion("__Global__"));
							return true;
						}
						count++;
					}
				}
				int id = 0;
				try {
					id = Integer.parseInt(args[0]);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					sender.sendMessage(ChatColor.RED + args[0] + " is not a number!");
					return true;
				}

				int count = 0;
				String ext = "";
				String name = Material.getMaterial(id).toString();
				while (true) {
					if(count>0){ext=String.valueOf(count);}
					if (getServer().getWorld(name + "_world" + ext)==null) {
						new WorldCreator(name + "_world" + ext).environment(Environment.NORMAL).generateStructures(false).type(WorldType.NORMAL).generator(new BlockGen(id)).createWorld();
						getMultiverseCore().getMVWorldManager().addWorld(name + "_world" + ext,Environment.NORMAL, "34134525656",WorldType.NORMAL, false, "OasisWorldGen:BlockGen");
						getWorldGuard().getRegionManager(getServer().getWorld(name + "_world" + ext)).addRegion(new GlobalProtectedRegion("__Global__"));
						this.getConfig().set(name + "_world" + ext, id);
						this.saveConfig();
						return true;
					}
					count++;
				}
			}
		}

		if (cmd.getName().equalsIgnoreCase("worldgen")) {

			// Check for valid args
			if (args.length > 2)
				return false;

			String type = "";
			int radius = 200;

			if (args.length >= 1)
				type = args[0].toLowerCase();
			if (args.length == 2)
				radius = Integer.parseInt(args[1]);

			// Check if this is a player, or if we are asking for help
			if (!(sender instanceof Player)) {
				if (!type.equals("") && !type.equals("help")) {
					sender.sendMessage(ChatColor.RED + "You need to be in-game to use WorldGen commands!");
					return false;
				}
				type = "help";
			}

			// Generate based on given type
			if (type.equals("village")) {
				// This ONLY works outside of the NETHER.
				if (player.getWorld().getEnvironment().getId() == -1) {
					player.sendMessage(ChatColor.RED + "Villages can not be generated in the Nether.");
					return true;
				}
				GenType2.generate(this, player, radius, "worldgen.command.village", "village", "WorldGenVillageStart");
			} else if (type.equals("dungeon") || type.equals("Blaze") || type.equals("Skeleton")) { GenType7.generate(this, player, radius, "worldgen.command.dungeon", type, "WorldGenDungeons");
			} else if (type.equals("wtemple")) {
				GenType3.generate(this, player, radius, "worldgen.command.watertemple", "water temple", "WorldGenMonumentStart");
			} else if (type.equals("witch") || type.equals("witchhut")) {
				GenType1.generate(this, player, radius, "worldgen.command.witch", "witch's hut", "WorldGenWitchHut");
			} else if (type.equals("jtemple") || type.equals("jungletemple")) {
				GenType1.generate(this, player, radius, "worldgen.command.jungletemple", "jungle temple", "WorldGenJungleTemple");
			} else if (type.equals("dtemple") || type.equals("deserttemple")) {
				GenType1.generate(this, player, radius, "worldgen.command.deserttemple", "desert temple", "WorldGenPyramidPiece");
			} else if (type.equals("well") || type.equals("desertwell")) {
				// You must be on sand for a well to generate
				GenType4.generate(this, player, "worldgen.command.simple", "well", "WorldGenDesertWell");
			} else if (type.equals("mushroom") || type.equals("shroom")) {
				// You must be ON grass, dirt, or mycelium for a shroom to
				// generate. Seems to be a bit picky as to location, so try
				// around the area to get one to generate.
				GenType4.generate(this, player, "worldgen.command.simple", "huge mushroom", "WorldGenHugeMushroom");
			} else if (type.equals("reed")) {
				// Note -- you must be ON grass, dirt, or sand next to water
				// for reeds to generate
				GenType4.generate(this, player, "worldgen.command.simple", "reeds", "WorldGenReed");
			} else if (type.equals("lily")) {
				// You must be over water for lilies to generate
				GenType4.generate(this, player, "worldgen.command.simple", "water lilies", "WorldGenWaterLily");
			} else if (type.equals("pumpkin")) {
				// Needs to be ON grass to work.
				GenType4.generate(this, player, "worldgen.command.simple", "pumpkins", "WorldGenPumpkin");
			} else if (type.equals("swamptree")) {
				// Generate on grass or dirt (can be in shallow water)
				GenType4.generate(this, player, "worldgen.command.simple", "swamp tree", "WorldGenSwampTree");
			} else if (type.equals("stronghold")) {
				GenType3.generate(this, player, radius, "worldgen.command.stronghold", "stronghold", "WorldGenStronghold2Start");
			} else if (type.equals("mineshaft")) {
				GenType3.generate(this, player, radius, "worldgen.command.mineshaft", "mineshaft", "WorldGenMineshaftStart");
			} else if (type.equals("shportal") || type.equals("strongholdportal")) {
				GenType5.generate(this, player, 10, "worldgen.command.stronghold", "stronghold portal room", "WorldGenStrongholdPortalRoom");
			} else if (type.equals("netherfortress") || type.equals("fortress")) {
				GenType3.generate(this, player, radius, "worldgen.command.nether", "nether fortress", "WorldGenNetherStart");
			} else if (type.equals("vblack")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village blacksmith", "WorldGenVillageBlacksmith", "b", 0);
			} else if (type.equals("vbutcher")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village butcher", "WorldGenVillageButcher", "a", 0);
			} else if (type.equals("vfarm")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village farm", "WorldGenVillageFarm", "a", -1);
			} else if (type.equals("vfarm2")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village farm (type2)", "WorldGenVillageFarm2", "a", -1);
			} else if (type.equals("vhouse")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village house", "WorldGenVillageHouse", "a", 0);
			} else if (type.equals("vhouse2")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village house (type2)", "WorldGenVillageHouse2", "a", 0);
			} else if (type.equals("vhut")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village hut", "WorldGenVillageHut", "a", 0);
			} else if (type.equals("vlibrary") ){
				GenType6.generate(this, player, 10, "worldgen.command.village", "village library", "WorldGenVillageLibrary", "a", 0);
			} else if (type.equals("vlight")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village light pole", "WorldGenVillageLight", "a", -1);
			} else if (type.equals("vtemple")) {
				GenType6.generate(this, player, 10, "worldgen.command.village", "village temple", "WorldGenVillageTemple", "a", 0);
			} else if (type.equals("help")) {

				// Get page number (next parameter)
				if (radius == 200)
					radius = 1;
				if (radius > 3)
					radius = 3;

				if (sender instanceof Player)
					sender.sendMessage(ChatColor.YELLOW + "-----" + ChatColor.WHITE + " WorldGen Commands (" + radius + "/3) " + ChatColor.YELLOW + "-----");
				else
					sender.sendMessage(ChatColor.YELLOW + "-----" + ChatColor.WHITE + " WorldGen Commands " + ChatColor.YELLOW + "-----");

				if (radius == 1 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen village [radius]" + ChatColor.WHITE + " - Generate a village");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen stronghold [radius]" + ChatColor.WHITE + " - Generate a stronghold");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen mineshaft [radius]" + ChatColor.WHITE + " - Generate a mineshaft");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen fortress [radius]" + ChatColor.WHITE + " - Generate a nether fortress");
				}
				if (radius == 2 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen witch" + ChatColor.WHITE + " - Generate a witch's hut");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen jtemple" + ChatColor.WHITE + " - Generate a jungle temple");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen dtemple" + ChatColor.WHITE + " - Generate a desert temple");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen well" + ChatColor.WHITE + " - Generate a desert well");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen shroom" + ChatColor.WHITE + " - Generate a giant mushroom");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen reed" + ChatColor.WHITE + " - Generate sugar cane field (near water)");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen lily" + ChatColor.WHITE + " - Generate a lily patch (on water)");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen pumpkin" + ChatColor.WHITE + " - Generate a pumpkin patch");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen swamptree" + ChatColor.WHITE + " - Generate a swamp tree");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen shportal" + ChatColor.WHITE + " - Generate a stronghold portal room");
				}
				if (radius == 3 || !(sender instanceof Player)) {
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vblack" + ChatColor.WHITE + " - Generate a village blacksmith");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vbutcher" + ChatColor.WHITE + " - Generate a village butcher");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vfarm" + ChatColor.WHITE + " - Generate a village farm");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vfarm2" + ChatColor.WHITE + " - Generate a village farm x2");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhouse" + ChatColor.WHITE + " - Generate a village house");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhouse2" + ChatColor.WHITE + " - Generate a village house x2");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vhut" + ChatColor.WHITE + " - Generate a village hut");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vlibrary" + ChatColor.WHITE + " - Generate a village library");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vlight" + ChatColor.WHITE + " - Generate a village light pole");
					sender.sendMessage(ChatColor.YELLOW + "/worldgen vtemple" + ChatColor.WHITE + " - Generate a village temple");
				}
				return true;
			} else {
				// Invalid type specified. Show valid list and exit.
				sender.sendMessage(ChatColor.RED + "Invalid WorldGen command specified.");
				return false;
			}

			// Complete!
			return true;
		}

		// Bad command given .. return help
		return false;
	}

	public MultiverseCore getMultiverseCore() {
		Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");

		if (plugin instanceof MultiverseCore) {
			return (MultiverseCore) plugin;
		}

		throw new RuntimeException("MultiVerse not found!");
	}

	public WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null; // Maybe you want throw an exception instead
		}

		return (WorldGuardPlugin) plugin;
	}
}