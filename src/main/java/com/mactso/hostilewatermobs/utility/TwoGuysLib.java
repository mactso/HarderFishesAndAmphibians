package com.mactso.hostilewatermobs.utility;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;

public class TwoGuysLib {

	private static final BlockPos.MutableBlockPos mbp = new MutableBlockPos();
	private static final BlockPos.MutableBlockPos mcp = new MutableBlockPos();
	
	public int helperCountBlocksBB(Block searchBlock, int maxCount, Level w, BlockPos bPos, int boxSize) {
		return helperCountBlocksBB(searchBlock, maxCount, w, bPos, boxSize, boxSize); // "square" box subcase
	}

	public static int helperCountBlocksBB(Block searchBlock, int maxCount, Level w, BlockPos pos, int boxSize, int ySize) {
		int count = 0;
		int minX = pos.getX() - boxSize;
		int maxX = pos.getX() + boxSize;
		int minZ = pos.getZ() - boxSize;
		int maxZ = pos.getZ() + boxSize;
		int minY = pos.getY() - ySize;
		int maxY = pos.getY() + ySize;

		for (int dx = minX; dx <= maxX; dx++) {
			for (int dz = minZ; dz <= maxZ; dz++) {
				for (int dy = minY; dy <= maxY; dy++) {
					mcp.setX(dx);
					mcp.setY(dy);
					mcp.setZ(dz);
					Block b = w.getBlockState(mcp).getBlock();
//					Utility.debugMsg(2, "dx:" + dx + ", dz:" + dz + ", dy:" + dy + "  Block:"
//							+ b.getDescriptionId().toString() + ", count:" + count);
					if (b == searchBlock)
						count++;
					if (count >= maxCount)
						return count;
				}
			}
		}

//		Utility.debugMsg(1, pos,
//				searchBlock.getDescriptionId().toString() + " Sparse count:" + count + " countBlockBB ");

		return count;
	}

	public int helperCountBlocksBB(Class<? extends Block> searchBlock, int maxCount, Level w, BlockPos bPos,
			int boxSize) {
		return helperCountBlocksBB(searchBlock, maxCount, w, bPos, boxSize, 0);
	}

	public int helperCountBlocksBB(Class<? extends Block> searchBlock, int maxCount, Level w, BlockPos bPos,
			int boxSize, int ySize) {
		int count = 0;
		int minX = bPos.getX() - boxSize;
		int maxX = bPos.getX() + boxSize;
		int minZ = bPos.getZ() - boxSize;
		int maxZ = bPos.getZ() + boxSize;
		int minY = bPos.getY() - ySize;
		int maxY = bPos.getY() + ySize;

		for (int dx = minX; dx <= maxX; dx++) {
			for (int dz = minZ; dz <= maxZ; dz++) {
				for (int dy = minY; dy <= maxY; dy++) {
					Block b = w.getBlockState(new BlockPos(dx, dy, dz)).getBlock();
//					Utility.debugMsg(2, "dx:" + dx + ", dz:" + dz + ", dy:" + dy + "  Block:"
//							+ b.getDescriptionId().toString() + ", count:" + count);
					if (searchBlock.isInstance(w.getBlockState(new BlockPos(dx, dy, dz)).getBlock())) {
						count++;
					}
					if (count >= maxCount) {
						return count;
					}
				}
			}
		}

//		Utility.debugMsg(1, bPos, searchBlock.getSimpleName() + " Sparse count:" + count + " countBlockBB ");

		return count;
	}


	
	public static int fastRandomBlockCount (LevelAccessor level, Block testBlock, BlockPos pos, int numChecks) {

		RandomSource rand = level.getRandom();
		int found = 0;
		numChecks = numChecks > 64 ? 64 : numChecks;

		int range = numChecks >> 1;
		int offset = numChecks;

		int posX = pos.getX();
		int posZ = pos.getZ();
		int posY = pos.below().getY();

		MutableBlockPos mPos = new MutableBlockPos(posX, posY, posZ) ;
		mPos.setY(posY);
		
		for (int i = 0; i<numChecks; i++) {
			int x = posX + rand.nextInt(range) - offset;
			int y = posY - rand.nextInt(5);
			int z = posZ + rand.nextInt(range) - offset;
			mPos.setX(x);
			mPos.setY(y);
			mPos.setZ(z);
			if (level.getBlockState(mPos).getBlock() == testBlock) {
				found++;
			}
		}
		Utility.debugMsg(2,pos, "Found " + found + " blocks " + testBlock.getDescriptionId()+" .");
		return found;
	}


	public static boolean fastRandomBlockCheck (LevelAccessor level, Block testBlock, BlockPos pos, int numChecks) {

		RandomSource rand = level.getRandom();
		numChecks = numChecks > 64 ? 64 : numChecks;

		int range = numChecks >> 1;
		int offset = numChecks;

		int posX = pos.getX();
		int posZ = pos.getZ();
		int posY = pos.below().getY();

		MutableBlockPos mPos = new MutableBlockPos(posX, posY, posZ) ;
		mPos.setY(posY);
		
		for (int i = 0; i<numChecks; i++) {
			int x = posX + rand.nextInt(range) - offset;
			int y = posY - rand.nextInt(5);
			int z = posZ + rand.nextInt(range) - offset;
			mPos.setX(x);
			mPos.setY(y);
			mPos.setZ(z);
			if (level.getBlockState(mPos).getBlock() == testBlock) {
				return true;
			}
		}
		return false;
	}
	
	@Nullable
	public static boolean findWaterBlocks(EntityType<? extends Mob> entityIn, LevelAccessor world, BlockPos pos,
			int maxXZ, int maxY, int MinWaterCount) {
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		int waterCount = 0;
		for (int iX = -maxXZ; iX < maxXZ; iX++) {
			for (int iY = -maxY; iY < maxY; iY++) {
				for (int iZ = -maxXZ; iZ < maxXZ; iZ++) {
					mbp.setX(x + iX);
					mbp.setY(y + iY);
					mbp.setZ(z + iZ);
					if (world.isWaterAt(mbp)) {
						waterCount++;
						if (waterCount > MinWaterCount) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
}
