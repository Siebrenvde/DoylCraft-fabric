package dev.siebrenvde.fabric.doylcraft;

import dev.siebrenvde.fabric.doylcraft.commands.GroupCommand;
import dev.siebrenvde.fabric.doylcraft.commands.ReplyCommand;
import dev.siebrenvde.fabric.doylcraft.handlers.ScoreboardHandler;
import dev.siebrenvde.fabric.doylcraft.voicechat.SVCPlugin;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;

public class DoylCraft implements ModInitializer {

	public static final HashMap<ServerPlayerEntity, ServerPlayerEntity> REPLY_PLAYERS = new HashMap<>();

	private ScoreboardHandler scoreboardHandler;

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(
			(dispatcher, dedicated, environment) -> {
				GroupCommand.register(dispatcher);
				ReplyCommand.register(dispatcher);
			}
		);
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			scoreboardHandler = new ScoreboardHandler(server);
		});
		ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
			scoreboardHandler.initPlayer(handler.getPlayer());
			SVCPlugin.checkVoicechatInstalled(handler.getPlayer());
		}));
		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
			REPLY_PLAYERS.remove(handler.getPlayer());
			for(ServerPlayerEntity key : REPLY_PLAYERS.keySet()) {
				if(REPLY_PLAYERS.get(key) == handler.getPlayer()) {
					REPLY_PLAYERS.remove(key);
				}
			}
		}));
	}

}