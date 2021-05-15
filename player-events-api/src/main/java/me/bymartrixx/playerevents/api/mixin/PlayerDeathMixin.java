package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.PlayerDeathCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
    @Inject(at = @At(value = "TAIL"), method = "onDeath", cancellable = true)
    private  void onPlayerDeath(DamageSource source, CallbackInfo info) {
        PlayerDeathCallback.EVENT.invoker().kill((ServerPlayerEntity) (Object) this, source);

        ActionResult result1 = io.github.bymartrixx.playerevents.api.event.PlayerDeathCallback.EVENT.invoker().kill((ServerPlayerEntity) (Object) this, source);

        if (result1 == ActionResult.FAIL) {
            info.cancel();
        }
    }
}
