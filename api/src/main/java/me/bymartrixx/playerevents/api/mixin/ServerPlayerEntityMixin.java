package me.bymartrixx.playerevents.api.mixin;

import me.bymartrixx.playerevents.api.event.PlayerDeathCallback;
import me.bymartrixx.playerevents.api.event.PlayerFirstDeathCallback;
import me.bymartrixx.playerevents.api.event.PlayerKillPlayerCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPrimeAdversary()Lnet/minecraft/entity/LivingEntity;"), method = "onDeath")
    private void onPlayerKilled(DamageSource source, CallbackInfo ci) {
        Entity attacker = source.getAttacker();
        if (attacker instanceof ServerPlayerEntity player) {
            PlayerKillPlayerCallback.EVENT.invoker().killPlayer(player, (ServerPlayerEntity) (Object) this);
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onDeath")
    private void onPlayerDeath(DamageSource source, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        if (player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)) < 1) {
            PlayerFirstDeathCallback.EVENT.invoker().firstDeath(player, source);
        }

        PlayerDeathCallback.EVENT.invoker().kill(player, source);
    }
}
