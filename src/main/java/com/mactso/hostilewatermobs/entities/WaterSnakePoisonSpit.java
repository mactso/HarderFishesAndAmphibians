package com.mactso.hostilewatermobs.entities;

import com.mactso.hostilewatermobs.utility.Utility;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WaterSnakePoisonSpit extends LlamaSpit {

	public WaterSnakePoisonSpit(EntityType<LlamaSpit> csSpit, Level w) {
		super(csSpit, w);
	}

	public WaterSnakePoisonSpit(Level p_i47273_1_, WaterSnake p_i47273_2_) {
		this(EntityType.LLAMA_SPIT, p_i47273_1_);
		super.setOwner(p_i47273_2_);
		this.setPos(
				p_i47273_2_.getX() - (double) (p_i47273_2_.getBbWidth() + 1.0F) * 0.5D
						* (double) Mth.sin(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)),
				p_i47273_2_.getEyeY() - (double) 0.1F, p_i47273_2_.getZ() + (double) (p_i47273_2_.getBbWidth() + 1.0F)
						* 0.5D * (double) Mth.cos(p_i47273_2_.yBodyRot * ((float) Math.PI / 180F)));
	}

	@OnlyIn(Dist.CLIENT)
	public WaterSnakePoisonSpit(Level p_i47274_1_, double p_i47274_2_, double p_i47274_4_, double p_i47274_6_,
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

	public void tick() {
		super.tick();
		Vec3 vector3d = this.getDeltaMovement();
		// TODO : Suspicious change here.  Confirm method name change
		HitResult raytraceresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		if (raytraceresult != null && raytraceresult.getType() != HitResult.Type.MISS
				&& !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
			this.onHit(raytraceresult);
		}

		double d0 = this.getX() + vector3d.x;
		double d1 = this.getY() + vector3d.y;
		double d2 = this.getZ() + vector3d.z;
		this.updateRotation();
		float f = 0.99F;
		float f1 = 0.06F;
		if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
			this.remove(RemovalReason.DISCARDED);
		} else if (this.isInWaterOrBubble()) {
			this.remove(RemovalReason.DISCARDED);
		} else {
			this.setDeltaMovement(vector3d.scale((double) 0.99F));
			if (!this.isNoGravity()) {
				this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double) -0.06F, 0.0D));
			}

			this.setPos(d0, d1, d2);
		}
	}

	
	
	protected void onHitEntity(EntityHitResult hitResult) {
		super.onHitEntity(hitResult);
		if ((hitResult.getEntity() instanceof LivingEntity targetEntity)) {
			Entity owner = this.getOwner();
            targetEntity.hurt(owner.level().damageSources().mobProjectile(this, targetEntity),0.25F);  // direct damage from attack is small.
            Utility.updateEffect(targetEntity, 1, MobEffects.POISON,160);
		}
	}

	
	
	@SuppressWarnings("resource")
	protected void onHitBlock(BlockHitResult p_230299_1_) {
		super.onHitBlock(p_230299_1_);
		if (!this.level().isClientSide) {
			this.remove(RemovalReason.DISCARDED);
		}

	}

	protected void defineSynchedData() {
	}

	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}
}
