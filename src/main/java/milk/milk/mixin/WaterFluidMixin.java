package milk.milk.mixin;

import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.WaterFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;

import static milk.milk.Milk.MILK_TAG;

@Mixin(WaterFluid.class)
public abstract class WaterFluidMixin extends FlowableFluid {
    @Override
    public boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction direction) {
        FluidState otherstate = world.getFluidState(pos.offset(direction.getOpposite()));
        return !fluid.matchesType(state.getFluid()) && (
                direction == Direction.DOWN
                || (fluid.isIn(MILK_TAG) && state.getLevel() <= otherstate.getLevel())
                );
    }
}
