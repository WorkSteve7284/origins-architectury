package io.github.apace100.origins.entity;

import io.github.apace100.origins.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EnderianPearlEntity extends ThrownEnderpearl {
	public EnderianPearlEntity(EntityType<? extends EnderianPearlEntity> entityType, Level world) {
		super(entityType, world);
	}

	public EnderianPearlEntity(EntityType<? extends EnderianPearlEntity> entityType, LivingEntity owner, Level world) {
		super(entityType, world);
		this.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
		this.setOwner(owner);
	}

	public EnderianPearlEntity(Level world, LivingEntity owner) {
		this(ModEntities.ENDERIAN_PEARL.get(), owner, world);
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult entityHitResult) {}

	protected @NotNull Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	protected void onHit(HitResult result) {
		HitResult.Type type = result.getType();
		if (type == HitResult.Type.ENTITY)
			this.onHitEntity((EntityHitResult) result);
		else if (type == HitResult.Type.BLOCK)
			this.onHitBlock((BlockHitResult) result);

		if (type != HitResult.Type.MISS)
			this.gameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());


		for (int i = 0; i < 32; ++i) {
			this.level.addParticle(ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0D, this.getZ(), this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
		}

		if (!this.level.isClientSide && !this.isRemoved()) {
			Entity entity = this.getOwner();
			if (entity instanceof ServerPlayer serverplayer) {
				//Origins: No damage from enderian pearls.
				//Origins: Disable Endermite spawning.
				if (serverplayer.connection.getConnection().isConnected() && serverplayer.level == this.level && !serverplayer.isSleeping()) {
					EntityTeleportEvent.EnderPearl event = ForgeEventFactory.onEnderPearlLand(serverplayer, this.getX(), this.getY(), this.getZ(), this, 0.0F);
					if (!event.isCanceled()) {
						if (entity.isPassenger())
							serverplayer.dismountTo(this.getX(), this.getY(), this.getZ());
						else
							entity.teleportTo(this.getX(), this.getY(), this.getZ());

						entity.teleportTo(event.getTargetX(), event.getTargetY(), event.getTargetZ());
						entity.fallDistance = 0.0F;
						if (event.getAttackDamage() > 0)
							entity.hurt(DamageSource.FALL, event.getAttackDamage());
					}
				}
			} else if (entity != null) {
				entity.teleportTo(this.getX(), this.getY(), this.getZ());
				entity.fallDistance = 0.0F;
			}

			this.discard();
		}
	}

	@Override
	public @NotNull Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}
}
