package oasis.worldgen;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Random;

import org.bukkit.WorldType;
import org.bukkit.entity.Player;


public class WorldInterface {

	// Package identifiers
	public String pckCraft;
	public String pckMinecraft;
	
	// Object references
	public Object oCraftWorldHandle;
	public Random oRandom;
	public Object oNoiseGenerator;
	private WorldGen plugin;
	
	// Class references
	Class<?> clObjGenerator;
	Class<?> clObjWorld;
	Class<?> clObjWorldServer;
	Class<?> clObjWorldProvider;
	Class<?> clObjStrucBox;
	Class<?> clObjStruc;
	Class<?> clObjBlockPos;

	public WorldInterface(WorldGen plugin, Player player, String nameClass) {
		this.plugin = plugin;
		// Initialize our member variables to work with our nameClass
		// constructor type
		try {

			pckCraft = Utility.FindPackage("CraftWorld");
			Class<?> clObjCraftWorld = Class.forName(pckCraft + ".CraftWorld");

			// Get the full class path minecraft class objects
			pckMinecraft = Utility.FindPackage(nameClass);
			clObjGenerator = Class.forName(pckMinecraft + "." + nameClass);
			clObjWorld = Class.forName(pckMinecraft + ".World");
			clObjWorldServer = Class.forName(pckMinecraft + ".WorldServer");
			clObjWorldProvider = Class.forName(pckMinecraft + ".WorldProvider");
			clObjStrucBox = Class.forName(pckMinecraft + ".StructureBoundingBox");
			clObjStruc = Class.forName(pckMinecraft + ".StructureStart");
			clObjBlockPos = Class.forName(pckMinecraft + ".BlockPosition");

			// Generate object references we will need later
			Object oCraftWorld = player.getWorld();
			Method mGetHandle = clObjCraftWorld.getMethod("getHandle");
			oCraftWorldHandle = mGetHandle.invoke(oCraftWorld);

			Field fWorldProvider = clObjWorldServer.getField("worldProvider");
			Object oWorldProvider = fWorldProvider.get(oCraftWorldHandle);

			Method mGetChunkProvider = clObjWorldProvider.getMethod("getChunkProvider");
			Object oChunkProvider = mGetChunkProvider.invoke(oWorldProvider);

			Field randField = getChunkProvider(player);
			oRandom = (Random) randField.get(oChunkProvider);
			
		} catch (Exception e) {
			plugin.getLogger().info(pckCraft);
			plugin.getLogger().info(pckMinecraft);
			plugin.getLogger().info(nameClass);
			plugin.getLogger().info(clObjGenerator.getName());
			plugin.getLogger().info(clObjWorld.getName());
			plugin.getLogger().info(clObjWorldServer.getName());
			plugin.getLogger().info(clObjWorldProvider.getName());
			plugin.getLogger().info(clObjStrucBox.getName());
			plugin.getLogger().info(clObjStruc.getName());
			oCraftWorldHandle = null;
			oRandom = null;
			e.printStackTrace();
		}
	}

	/**
	 * Get the chunk provider object for the current world.
	 * 
	 * @param player
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchFieldException
	 */
	private Field getChunkProvider(Player player) throws ClassNotFoundException, NoSuchFieldException {

		int envType = player.getWorld().getEnvironment().getId();
		Class<?> clObjChunkGen;
		Field randField;
		if (envType == -1) {
			clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderHell");
			randField = clObjChunkGen.getDeclaredField("j");
		} else if (envType == 1) {
			clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderTheEnd");
			randField = clObjChunkGen.getDeclaredField("h");
		} else {
			// Normal world has FLAT type generator
			if (player.getWorld().getWorldType() == WorldType.FLAT) {
				clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderFlat");
				randField = clObjChunkGen.getDeclaredField("b");
			} else {
				clObjChunkGen = Class.forName(pckMinecraft + ".ChunkProviderGenerate");
				randField = clObjChunkGen.getDeclaredField("h");
			}
		}

		randField.setAccessible(true);
		return randField;
	}

}