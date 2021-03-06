package oasis.worldgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Type3 generation of a Minecraft structure. This has requirements of:
 * 
 * A constructor: c(World,Random,int,int) and inherits from StructureStart.
 * 
 * A method: a(World,Random,StructureBoundingBox)
 * 
 * Example: WorldGenMonumentStart
 * 
 * @author Huttinger
 * 
 */
class GenType7 {

	/**
	 * Type3 generation of a Minecraft structure. This has requirements of:
	 * 
	 * A constructor: c(World,Random,int,int) and inherits from StructureStart.
	 * 
	 * A method: a(World,Random,StructureBoundingBox)
	 * 
	 * @param player
	 *            Player object
	 * @param radius
	 *            Radius to limit creation to
	 * @param namePerm
	 *            Name of permission required (i.e. worldgen.command.simple)
	 * @param nameDisplay
	 *            Name of structure being built as displayed to the user/logger
	 * @param nameClass
	 *            Name of class for structure to build.
	 */
	public static void generate(WorldGen plugin, Player player, int radius, String namePerm, String nameDisplay, String nameClass) {

		if (!(player.hasPermission(namePerm))) {

			player.sendMessage(ChatColor.RED + "You do not have permissions for generating that!");
			return;
		}

		Utility.log.info("Generating " + nameDisplay + "...");

		try {

			// Get Crafting packages
			WorldInterface i = new WorldInterface(plugin, player, nameClass);
			if(i.oCraftWorldHandle == null)
			{
				player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
				return;
			}

			// Get current block position
			Block block = player.getLocation().getBlock();
			int x = block.getChunk().getX();
			int z = block.getChunk().getZ();
			
			int blockx = block.getLocation().getBlockX();
			int blockz = block.getLocation().getBlockZ();
			int blocky = block.getLocation().getBlockY();

			// Get the generation start object via our current block's chunk
			Constructor<?> cGen[] = i.clObjGenerator.getConstructors();
			cGen[0].setAccessible(true);
			Object oGen = cGen[0].newInstance();
			
			if (nameDisplay!="dungeon") {
				Field mob = i.clObjGenerator.getField("b");
				mob.setAccessible(true);
				String [] things = {nameDisplay, nameDisplay, nameDisplay, nameDisplay};
				mob.set(i.clObjGenerator, things);
				mob.setAccessible(false);
			}
			// Move to middle of chunk
			x = (x << 4) + 8;
			z = (z << 4) + 8;

			Constructor<?> cBlockPos = i.clObjBlockPos.getConstructor(int.class,int.class,int.class);
			Object oBlock = cBlockPos.newInstance(blockx,blocky,blockz);
			
//			// Prep the bounding box
//			Constructor<?> cBox = i.clObjStrucBox.getConstructor(int.class, int.class, int.class, int.class);
//			Object oBox = cBox.newInstance(x - radius, z - radius, x + radius, z + radius);

			Method generate = i.clObjGenerator.getDeclaredMethod("generate", i.clObjWorld, Random.class,i.clObjBlockPos);
			generate.invoke(oGen, i.oCraftWorldHandle, i.oRandom, oBlock);

			// All done!
			Utility.log.info("Generated " + nameDisplay + " at: (" + x + "," + z + ")");
			player.sendMessage("Generated a new " + nameDisplay + "!");

		} catch (Exception e) {
			e.printStackTrace();
			player.sendMessage(ChatColor.RED + "Failed to generate " + nameDisplay + ". Please check server log.");
		}

	}

}