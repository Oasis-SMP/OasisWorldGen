package oasis.worldgen;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class BlockGen extends ChunkGenerator{
	public int id;
	public BlockGen(int id) {
		this.id = id;	
	}
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
	{
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int height = getHeight(world, chunkX + x * 0.0625, chunkZ + z * 0.0625, 2) + 60;
				for (int y = 1; y < height; y++) {
					setBlock(result,x,y,z,(byte)id);
				}
			}
		}
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++) {
				setBlock(result,x,0,z,(byte) Material.BEDROCK.getId());
			}
		}
		return result;
	}
	
	private NoiseGenerator generator;
	private NoiseGenerator getGenerator(World world) {
		if (generator == null) {
			generator = new SimplexNoiseGenerator(world);
		}
		return generator;
	}

	private int getHeight(World world, double x, double y, int variance) {
		NoiseGenerator gen = getGenerator(world);
		double result = gen.noise(x, y);
		result *= variance;
		return NoiseGenerator.floor(result);
	}
	
	void setBlock(byte[][] result, int x, int y, int z, byte blkid) {
		// is this chunk part already initialized?
		if (result[y >> 4] == null) {
			// Initialize the chunk part
			result[y >> 4] = new byte[4096];
		}
		// set the block (look above, how this is done)
		result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blkid;
	}
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 64, 0);
	}
}
