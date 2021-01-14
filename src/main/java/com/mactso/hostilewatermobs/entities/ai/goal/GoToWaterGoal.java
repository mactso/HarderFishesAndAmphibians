package com.mactso.hostilewatermobs.entities.ai.goal;

import java.util.EnumSet;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;

public class GoToWaterGoal extends Goal {
	private final CreatureEntity creature;
	private double cX;
	private double cY;
	private double cZ;
	private final double speed;
	private final World world;

	public GoToWaterGoal(final CreatureEntity creatureIn, final double speedIn) {
		this.creature = creatureIn;
		this.speed = speedIn;
		this.world = creatureIn.world;
		this.setMutexFlags((EnumSet) EnumSet.of(Goal.Flag.MOVE));
	}

	public boolean shouldExecute() {
		long time = this.creature.world.getDayTime()%24000;
		
		if (this.creature.isInWater()) {
			return false;
		}

		// false in night.  don't wander water.
		if (!this.creature.world.isDaytime()) {
			return false;
		}

		final Vector3d rndWaterLoc = this.tryFindWaterBlock();
		if (rndWaterLoc == null) {
			return false;
		}
		this.cX = rndWaterLoc.x;
		this.cY = rndWaterLoc.y;
		this.cZ = rndWaterLoc.z;
		return true;
	}

	public boolean shouldContinueExecuting() {
		return !this.creature.getNavigator().noPath();
	}

	public void startExecuting() {
		this.creature.getNavigator().tryMoveToXYZ(this.cX, this.cY,
				this.cZ, this.speed);
	}

	@Nullable
	private Vector3d tryFindWaterBlock() {
		final Random rand = this.creature.getRNG();
		final BlockPos creaturePos = this.creature.getPosition();
		for (int i = 0; i < 10; ++i) {
			final BlockPos rndPos = creaturePos.add(rand.nextInt(31) - 15, 2 - rand.nextInt(8),
					rand.nextInt(31) - 15);
			if (this.world.getBlockState(rndPos).isIn(Blocks.WATER)) {
				return Vector3d.copyCenteredHorizontally((Vector3i) rndPos);
			}
		}
		return null;
	}
}
