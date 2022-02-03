package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.PlayerKillEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(LivingEntity.class)
public class PlayerKillEntityMixin {
    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"),
            method = "onDeath",
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void onEntityKilledEntity(DamageSource source, CallbackInfo ci, Entity entity) {
        if (entity instanceof ServerPlayerEntity player) {
            PlayerKillEntityCallback.EVENT.invoker().killEntity(player, (Entity) (Object) this);
            io.github.bymartrixx.playerevents.api.event.PlayerKillEntityCallback.EVENT.invoker().killEntity(player, (Entity) (Object) this);
        }
    }
}
