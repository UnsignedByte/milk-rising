package milk.milk.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.SpawnHelper;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import static milk.milk.Milk.MILK_TAG;
import static net.minecraft.world.SpawnHelper.isClearForSpawn;

@Mixin(SpawnHelper.class)
public class SpawnHelperMixin {
    /**
     * @author UnsignedByte
     * @reason lol
     */
    @Overwrite
    public static boolean canSpawn(SpawnRestriction.Location location, WorldView world, BlockPos pos, @Nullable EntityType<?> entityType) {
        BlockState blockState = world.getBlockState(pos);
        FluidState fluidState = world.getFluidState(pos);
        BlockPos blockPos = pos.up();
        BlockPos blockPos2 = pos.down();
        BlockState blockState2 = world.getBlockState(blockPos2);
        if (fluidState.isIn(MILK_TAG)) {
            return true;
        } else if (!blockState2.allowsSpawning(world, blockPos2, entityType)) {
            return false;
        } else {
            return isClearForSpawn(world, pos, blockState, fluidState, entityType) && isClearForSpawn(world, blockPos, world.getBlockState(blockPos), world.getFluidState(blockPos), entityType);
        }
    }
}
