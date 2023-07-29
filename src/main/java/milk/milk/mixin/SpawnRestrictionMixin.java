package milk.milk.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static milk.milk.Milk.MILK_BLOCK;
import static milk.milk.Milk.MILK_TAG;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {
    /**
     * @author UnsignedByte
     * @reason funny
     */
    @Overwrite
    public static <T extends Entity> boolean canSpawn(EntityType<T> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return world.getFluidState(pos).getFluid().isIn(MILK_TAG) || world.getBlockState(pos).getBlock() == MILK_BLOCK
                || AnimalEntity.isValidNaturalSpawn((EntityType<? extends AnimalEntity>) type, world, spawnReason, pos, random);
    }
}
