package com.mactso.hostilewatermobs.util;

import java.util.Random;

import javax.annotation.Nullable;

import com.mactso.hostilewatermobs.config.MyConfig;
import com.mactso.hostilewatermobs.entities.WaterSnakeEntity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class TwoGuysLib {

	public int helperCountBlocksBB(Block searchBlock, int maxCount, World w, BlockPos bPos, int boxSize) {
		return helperCountBlocksBB(searchBlock, maxCount, w, bPos, boxSize, boxSize); // "square" box subcase
	}

	public int helperCountBlocksBB(Block searchBlock, int maxCount, World w, BlockPos bPos, int boxSize, int ySize) {
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
					MyConfig.debugMsg(2, "dx:" + dx + ", dz:" + dz + ", dy:" + dy + "  Block:"
							+ b.getRegistryName().toString() + ", count:" + count);
					if (w.getBlockState(new BlockPos(dx, dy, dz)).getBlock() == searchBlock)
						count++;
					if (count >= maxCount)
						return count;
				}
			}
		}

		MyConfig.debugMsg(1, bPos,
				searchBlock.getRegistryName().toString() + " Sparse count:" + count + " countBlockBB ");

		return count;
	}

	public int helperCountBlocksBB(Class<? extends Block> searchBlock, int maxCount, World w, BlockPos bPos,
			int boxSize) {
		return helperCountBlocksBB(searchBlock, maxCount, w, bPos, boxSize, 0);
	}

	public int helperCountBlocksBB(Class<? extends Block> searchBlock, int maxCount, World w, BlockPos bPos,
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
					MyConfig.debugMsg(2, "dx:" + dx + ", dz:" + dz + ", dy:" + dy + "  Block:"
							+ b.getRegistryName().toString() + ", count:" + count);
					if (searchBlock.isInstance(w.getBlockState(new BlockPos(dx, dy, dz)).getBlock())) {
						count++;
					}
					if (count >= maxCount) {
						return count;
					}
				}
			}
		}

		MyConfig.debugMsg(1, bPos, searchBlock.getSimpleName() + " Sparse count:" + count + " countBlockBB ");

		return count;
	}

	@Nullable
	public static boolean findWaterBlocks(EntityType<? extends MobEntity> entityIn, IWorld world, BlockPos blockPos,
			int maxXZ, int maxY, int MinWaterCount) {

		int waterCount = 0;
		for (int iX = -maxXZ; iX < maxXZ; iX++) {
			for (int iY = -maxY; iY < maxY; iY++) {
				for (int iZ = -maxXZ; iZ < maxXZ; iZ++) {
					if (world.isWaterAt(blockPos.east(iX).above(iY).south(iZ))) {
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
