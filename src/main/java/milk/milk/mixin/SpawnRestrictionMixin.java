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

import static milk.milk.Milk.MILK_TAG;

@Mixin(SpawnRestriction.class)
public class SpawnRestrictionMixin {
    @ModifyVariable(method = "register", at = @At(value = "HEAD"), argsOnly = true)
    private static SpawnRestriction.Location cow(SpawnRestriction.Location location) {
        return SpawnRestriction.Location.NO_RESTRICTIONS;
    }

    /**
     * @author UnsignedByte
     * @reason funny
     */
    @Overwrite
    public static <T extends Entity> boolean canSpawn(EntityType<T> type, ServerWorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
        return  world.getFluidState(pos).isIn(MILK_TAG)
                || AnimalEntity.isValidNaturalSpawn((EntityType<? extends AnimalEntity>) type, world, spawnReason, pos, random);
    }
}
