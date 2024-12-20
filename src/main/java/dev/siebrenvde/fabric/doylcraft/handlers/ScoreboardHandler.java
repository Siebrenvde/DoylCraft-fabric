package dev.siebrenvde.fabric.doylcraft.handlers;

import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;

public class ScoreboardHandler {

    private final Scoreboard board;
    private final ScoreboardObjective objective;

    public ScoreboardHandler(MinecraftServer server) {
        board = server.getScoreboard();
        if(!board.getObjectiveNames().contains("deaths")) {
            objective = board.addObjective(
                "deaths",
                ScoreboardCriterion.DEATH_COUNT,
                Text.literal("Deaths"),
                ScoreboardCriterion.RenderType.INTEGER,
                true,
                null
            );
        } else {
            objective = board.getObjectives().stream().filter(obj -> obj.getName().equals("deaths")).findFirst().get();
        }
        board.setObjectiveSlot(ScoreboardDisplaySlot.LIST, objective);
    }

    public void initPlayer(ServerPlayerEntity player) {
        ScoreAccess scoreAccess = board.getOrCreateScore(player, objective);
        int deaths = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
        scoreAccess.setScore(deaths);
    }

}
