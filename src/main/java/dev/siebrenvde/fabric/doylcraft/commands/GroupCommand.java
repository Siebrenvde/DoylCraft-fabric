package dev.siebrenvde.fabric.doylcraft.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.siebrenvde.fabric.doylcraft.handlers.LuckPermsHandler;
import dev.siebrenvde.fabric.doylcraft.utils.Colours;
import dev.siebrenvde.fabric.doylcraft.utils.Messages;
import dev.siebrenvde.fabric.doylcraft.utils.Utils;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.luckperms.api.model.group.Group;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class GroupCommand {

    public static void register(@NotNull CommandDispatcher<ServerCommandSource> dispatcher) {

        LiteralCommandNode<ServerCommandSource> node = dispatcher.register(literal("group")
            .requires(Permissions.require("doylcraft.group", 4))
            .then(argument("player", EntityArgumentType.player())
                .executes(ctx -> {
                    ServerCommandSource source = ctx.getSource();
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                    try {
                        LuckPermsHandler.getPlayerGroup(player).thenAcceptAsync(group -> {

                            if(group != null) {
                                source.sendMessage(
                                    Text.empty()
                                        .append(Utils.entityComponent(player.getName().copy().formatted(Colours.DATA), player))
                                        .append(Text.literal(" is a member of ").formatted(Colours.GENERIC))
                                        .append(Text.literal(group).formatted(Colours.DATA))
                                        .append(Text.literal(".").formatted(Colours.GENERIC))
                                );
                            } else {
                                source.sendMessage(
                                    Text.empty()
                                        .append(Utils.entityComponent(player.getName().copy().formatted(Colours.DATA), player))
                                        .append(Text.literal(" is not a member of any group.").formatted(Colours.GENERIC))
                                );
                            }

                        });

                        return 1;
                    } catch(Exception exception) {
                        source.sendMessage(Messages.error(
                            Text.literal("Failed to get ").formatted(Colours.ERROR)
                                .append(player.getName().copy().formatted(Colours.DATA))
                                .append(Text.literal("'s group.").formatted(Colours.ERROR)), exception
                        ));
                        exception.printStackTrace();
                        return 0;
                    }
                })
                .then(argument("group", StringArgumentType.word()).suggests(new GroupSuggestionProvider())
                    .executes(ctx -> {
                        ServerCommandSource sender = ctx.getSource();
                        ServerPlayerEntity player = EntityArgumentType.getPlayer(ctx, "player");

                        try {
                            String group = StringArgumentType.getString(ctx, "group");
                            if(!LuckPermsHandler.groupExists(group)) {
                                sender.sendMessage(
                                    Text.literal("Group ").formatted(Colours.ERROR)
                                        .append(Text.literal(group).formatted(Colours.DATA))
                                        .append(Text.literal(" does not exist.").formatted(Colours.ERROR))
                                );
                                return 0;
                            }

                            LuckPermsHandler.setPlayerGroup(player, group);
                            sender.sendMessage(
                                Text.literal("Changed ").formatted(Colours.GENERIC)
                                    .append(Utils.entityComponent(player.getName().copy().formatted(Colours.DATA), player))
                                    .append(Text.literal("'s group to ").formatted(Colours.GENERIC))
                                    .append(Text.literal(group).formatted(Colours.DATA))
                                    .append(Text.literal(".").formatted(Colours.GENERIC))
                            );
                            return 1;
                        } catch(Exception exception) {
                            sender.sendMessage(Messages.error(
                                Text.literal("Failed to change ").formatted(Colours.ERROR)
                                    .append(player.getName().copy().formatted(Colours.DATA))
                                    .append(Text.literal("'s group.").formatted(Colours.ERROR)), exception
                            ));
                            exception.printStackTrace();
                            return 0;
                        }
                    })
        )));

        dispatcher.register(literal("rank")
            .requires(Permissions.require("doylcraft.group", 4))
            .redirect(node)
        );

    }

    private static class GroupSuggestionProvider implements SuggestionProvider<ServerCommandSource> {
        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
            return CommandSource.suggestMatching(LuckPermsHandler.getGroups().stream().map(Group::getName), builder);
        }
    }

}
