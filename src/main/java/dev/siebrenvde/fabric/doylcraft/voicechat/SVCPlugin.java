package dev.siebrenvde.fabric.doylcraft.voicechat;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static net.minecraft.text.Text.literal;

public class SVCPlugin implements VoicechatPlugin {

    private static final String MODRINTH_URL = "https://modrinth.com/plugin/simple-voice-chat";
    private static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/simple-voice-chat";
    private static final String MODPACK_URL = "https://legacy.curseforge.com/minecraft/modpacks/doylttv/files/5442479";

    private static String VOICECHAT_VERSION;

    private static VoicechatServerApi serverApi;

    private static ScheduledExecutorService scheduler;

    @Override
    public String getPluginId() { return "doylcraft"; }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartedEvent.class, this::onServerStart);
    }

    private void onServerStart(VoicechatServerStartedEvent event) {
        serverApi = event.getVoicechat();
        Optional<ModContainer> voicechatContainer = FabricLoader.getInstance().getModContainer("voicechat");
        VOICECHAT_VERSION = voicechatContainer.get().getMetadata().getVersion().getFriendlyString();
        scheduler = Executors.newScheduledThreadPool(1);
    }

    private static MutableText getLink(String text, String url) {
        return literal(text)
            .formatted(Formatting.GOLD, Formatting.UNDERLINE)
            .fillStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)))
            .fillStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, literal(url))));
    }

    public static void checkVoicechatInstalled(ServerPlayerEntity player) {
        scheduler.schedule(() -> {
            VoicechatConnection connection = serverApi.getConnectionOf(player.getUuid());
            if (connection != null && !connection.isInstalled()) {
                player.sendMessage(
                    literal(String.format("Simple Voice Chat (%s) is supported on this server.", VOICECHAT_VERSION)).formatted(Formatting.GOLD)
                        .append(literal("\nYou can download it from "))
                        .append(getLink("Modrinth", MODRINTH_URL))
                        .append(literal(" or "))
                        .append(getLink("CurseForge", CURSEFORGE_URL))
                        .append(literal(".\nOr download our "))
                        .append(getLink("modpack", MODPACK_URL))
                        .append(literal("."))
                );
            }

        }, 2, TimeUnit.SECONDS);
    }

}
