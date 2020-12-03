package io.github.bymartrixx.playerevents.api.mixin;

import io.github.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerKillPlayerMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPrimeAdversary()Lnet/minecraft/entity/LivingEntity;"), method = "onDeath")
    private void onEntityKilledPlayer(DamageSource source, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof PlayerEntity) {
            PlayerKillPlayerCallback.EVENT.invoker().killPlayer((ServerPlayerEntity) attacker, (ServerPlayerEntity) (Object) this);
        }
    }
}
