package milk.milk.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static milk.milk.Milk.MILK_TAG;

@Mixin(LavaFluid.class)
public abstract class LavaFluidMixin extends FlowableFluid {
    @Shadow
    private void playExtinguishEvent(WorldAccess world, BlockPos pos) {};

    @Override
    protected void flow(WorldAccess world, BlockPos pos, BlockState state, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidState2 = world.getFluidState(pos);
            if (this.isIn(FluidTags.LAVA)) {
                if (fluidState2.isIn(MILK_TAG)) {
                    if (state.getBlock() instanceof FluidBlock) {
                        world.setBlockState(pos, Blocks.WHITE_CONCRETE.getDefaultState(), 3);
                    }

                    this.playExtinguishEvent(world, pos);
                    return;

                } else if (fluidState2.isIn(FluidTags.WATER)){
                    if (state.getBlock() instanceof FluidBlock) {
                        world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
                    }

                    this.playExtinguishEvent(world, pos);
                    return;
                }
            }
        }

        super.flow(world, pos, state, direction, fluidState);
    }
}
