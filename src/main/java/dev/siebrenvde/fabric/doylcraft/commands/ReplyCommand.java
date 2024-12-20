package dev.siebrenvde.fabric.doylcraft.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.siebrenvde.fabric.doylcraft.DoylCraft;
import dev.siebrenvde.fabric.doylcraft.mixin.MessageCommandInvoker;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class ReplyCommand {

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(CommandManager.literal("reply").requires(ServerCommandSource::isExecutedByPlayer).then(CommandManager.argument("message", MessageArgumentType.message()).executes((context) -> {
            ServerCommandSource sender = context.getSource();
            ServerPlayerEntity recipient = DoylCraft.REPLY_PLAYERS.get(sender.getPlayer());

            if(recipient == null) {
                sender.sendError(Text.literal("No player to reply to"));
                return Command.SINGLE_SUCCESS;
            }

            MessageArgumentType.getSignedMessage(context, "message", (message) -> {
                MessageCommandInvoker.sendMessage(context.getSource(), Collections.singleton(recipient), message);
            });
            return Command.SINGLE_SUCCESS;
        })));

        dispatcher.register(CommandManager.literal("r").redirect(literalCommandNode));
    }

}
