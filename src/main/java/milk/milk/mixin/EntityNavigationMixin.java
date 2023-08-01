package milk.milk.mixin;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static milk.milk.Milk.MILK_TAG;

@Mixin(EntityNavigation.class)
public class EntityNavigationMixin {
    @Shadow
    @Final
    protected MobEntity entity;

    @Inject(method = "isInLiquid", at = @At("HEAD"), cancellable = true)
    private void addmilk(CallbackInfoReturnable<Boolean> cir) {
        if (this.entity.getFluidHeight(MILK_TAG) > 0.0D) {
            cir.setReturnValue(true);
        }
    }
}
