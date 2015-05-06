package oasis.worldgen;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class LavaGen extends ChunkGenerator{
	
	public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid)
	{
		byte[][] result = new byte[world.getMaxHeight() / 16][]; //world height / chunk part height (=16, look above)
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++) {
				setBlock(result,x,0,z,(byte) Material.BEDROCK.getId());
			}
		}
		for(int x=0; x<16; x++){
			for(int z=0; z<16; z++) {
				setBlock(result,x,1,z,(byte) Material.NETHERRACK.getId());
			}
		}
		for(int x = 0; x < 16; x++){
			for(int y = 2; y <= 63; y++){
				for (int z = 0; z < 16; z++){
					setBlock(result,x,y,z,(byte) Material.LAVA.getId());
				}
			}
		}
		return result;
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