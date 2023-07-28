package milk.milk.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import static milk.milk.Milk.MILK_TAG;

@Mixin(FluidBlock.class)
public class FluidBlockMixin {
    @Shadow
    @Final
    protected FlowableFluid fluid;

    @Shadow
    @Final
    private static ImmutableList<Direction> FLOW_DIRECTIONS;


    @Shadow
    private void playExtinguishSound(WorldAccess world, BlockPos pos) {}


    /**
     * @author UnsignedByte
     * @reason lol
     */
    @Overwrite
    private boolean receiveNeighborFluids(World world, BlockPos pos, BlockState state) {
        if (this.fluid.isIn(FluidTags.LAVA)) {
            boolean bl = world.getBlockState(pos.down()).isOf(Blocks.SOUL_SOIL);
            UnmodifiableIterator var5 = FLOW_DIRECTIONS.iterator();

            while(var5.hasNext()) {
                Direction direction = (Direction)var5.next();
                BlockPos blockPos = pos.offset(direction.getOpposite());
                if (world.getFluidState(blockPos).getFluid().isIn(MILK_TAG)) {
                    Block block = world.getFluidState(pos).isStill() ? Blocks.POLISHED_DIORITE : Blocks.DIORITE;
                    world.setBlockState(pos, block.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }

                if (world.getFluidState(blockPos).isIn(FluidTags.WATER)) {
                    Block block = world.getFluidState(pos).isStill() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    world.setBlockState(pos, block.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }

                if (bl && world.getBlockState(blockPos).isOf(Blocks.BLUE_ICE)) {
                    world.setBlockState(pos, Blocks.BASALT.getDefaultState());
                    this.playExtinguishSound(world, pos);
                    return false;
                }
            }
        }

        return true;
    }
}
