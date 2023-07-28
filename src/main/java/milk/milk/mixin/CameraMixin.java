package milk.milk.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static milk.milk.Milk.MILK_TAG;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(method = "getSubmersionType",
        at = @At(value = "RETURN", ordinal = 1),
        locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true
    )
    public void sus(CallbackInfoReturnable<CameraSubmersionType> cir, FluidState fluidState) {
        if (fluidState.isIn(MILK_TAG)) {
            cir.setReturnValue(CameraSubmersionType.POWDER_SNOW);
        }
    }
}
