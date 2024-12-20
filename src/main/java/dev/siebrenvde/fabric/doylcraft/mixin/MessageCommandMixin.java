package dev.siebrenvde.fabric.doylcraft.mixin;

import dev.siebrenvde.fabric.doylcraft.DoylCraft;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MessageCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(MessageCommand.class)
public class MessageCommandMixin {

    @Inject(method = "execute", at = @At(value = "HEAD"))
    private static void onMessage(ServerCommandSource source, Collection<ServerPlayerEntity> targets, SignedMessage message, CallbackInfo ci) {
        if(!source.isExecutedByPlayer()) return;
        ServerPlayerEntity sender = source.getPlayer();
        for(ServerPlayerEntity recipient : targets) {
            if(recipient != sender) {
                DoylCraft.REPLY_PLAYERS.put(recipient, sender);
            }
        }
    }

}
