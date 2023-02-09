package com.mactso.hostilewatermobs.entities;

import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WaterSnakePoisonSpitEntity extends LlamaSpitEntity {

	public WaterSnakePoisonSpitEntity(EntityType<LlamaSpitEntity> csSpit, World w) {
		super(csSpit, w);
	}

	@OnlyIn(Dist.CLIENT)
	public WaterSnakePoisonSpitEntity(World p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_,
			double p_i47274_8_, double p_i47274_10_, double p_i47274_12_) {
		this(EntityType.LLAMA_SPIT, p_i47274_1_);
		this.setPos(p_i47274_2_, p_i47274_4_, p_i47274_6_);

		for (int i = 0; i < 7; ++i) {
			double d0 = 0.4D + 0.1D * (double) i;
			p_i47274_1_.addParticle(ParticleTypes.SPIT, p_i47274_2_, p_i47274_4_, p_i47274_6_, p_i47274_8_ * d0,
					p_i47274_10_, p_i47274_12_ * d0);
		}

		this.setDeltaMovement(p_i47274_8_, p_i47274_10_, p_i47274_12_);
	}

	public WaterSnakePoisonSpitEntity(World p_i47273_1_, WaterSnake p_i47273_2_) {
		this(EntityType.LLAMA_SPIT, p_i47273_1_);
		super.setOwner(p_i47273_2_);
		this.setPos(
				p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.5D
						* (double) MathHelper.sin(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)),
				p_i47273_2_.getEyeY() - (double) 0.1F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth() + 1.0F)
						* 0.5D * (double) MathHelper.cos(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)));
	}

	protected void defineSynchedData() {
	}

	public IPacket<?> getAddEntityPacket() {
		return new SSpawnObjectPacket(this);
	}

	protected void onHitBlock(BlockRayTraceResult p_230299_1_) {
		super.onHitBlock(p_230299_1_);
		if (!this.level.isClientSide) {
			this.remove();
		}

	}

	protected void onHitEntity(EntityRayTraceResult targetRayTraceResult) {
		super.onHitEntity(targetRayTraceResult);

		if (!(targetRayTraceResult.getEntity() instanceof LivingEntity)) {
			return;
		}
		LivingEntity targetEntity = (LivingEntity) targetRayTraceResult.getEntity();
		Entity spitOwnerEntity = this.getOwner(); // who owns the projectile (the snake).
		if (spitOwnerEntity instanceof WaterSnake) {
            targetEntity.hurt(DamageSource.indirectMobAttack(this, (LivingEntity) spitOwnerEntity).setProjectile(),
					0.25F);  // direct damage from attack is small.
			EffectInstance ei =  targetEntity.getEffect(Effects.POISON);
    		if (ei != null) {
    			if (ei.getDuration() > 10) return;
    			if (ei.getAmplifier() > 0) return;
    		}
    		targetEntity.removeEffect(Effects.POISON);
			targetEntity.addEffect(new EffectInstance(Effects.POISON, 160, 1));

		}

	}

	public void tick() {
		super.tick();
		Vector3d vector3d = this.getDeltaMovement();
		RayTraceResult raytraceresult = ProjectileHelper.getHitResult(this, this::canHitEntity);
		if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS
				&& !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
			this.onHit(raytraceresult);
		}

		double d0 = this.getX() + vector3d.x;
		double d1 = this.getY() + vector3d.y;
		double d2 = this.getZ() + vector3d.z;
		this.updateRotation();
		float f = 0.99F;
		float f1 = 0.06F;
		if (this.level.getBlockStates(this.getBoundingBox()).noneMatch(AbstractBlock.AbstractBlockState::isAir)) {
			this.remove();
		} else if (this.isInWaterOrBubble()) {
			this.remove();
		} else {
			this.setDeltaMovement(vector3d.scale((double) 0.99F));
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.06F, 0.0D));
			}

			this.setPos(d0, d1, d2);
		}
	}
}
