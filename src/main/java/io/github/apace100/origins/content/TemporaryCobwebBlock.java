package io.github.apace100.origins.content;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TemporaryCobwebBlock extends WebBlock {

	public TemporaryCobwebBlock(BlockBehaviour.Properties settings) {
		super(settings);
	}

	@Override
	@Deprecated
	public void tick(@NotNull BlockState state, ServerLevel world, @NotNull BlockPos pos, @NotNull Random random) {
		if (!world.isClientSide)
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
	}

	@Override
	@NotNull
	public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	@NotNull
	public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		return Shapes.empty();
	}

	@Override
	public void onPlace(@NotNull BlockState state, Level worldIn, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
		worldIn.scheduleTick(pos, this, 60);
	}
}