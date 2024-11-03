package graphmctwo.graph;

import graphmctwo.main.GraphMC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

/**
 * 스코어보드를 처리하는 클래스입니다.
 */
public class ScoreboardHandler {
    /**
     * 스코어보드를 처리하는 프로세스를 시작합니다. 모든 그래프의 식을 표시하며,
     * 현재 존재하는 {@link Marker} 수와 그래프 설정을 표시합니다.
     */
    public static void startScoreboard() {
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 스코어보드를 등록했습니다.");
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(GraphMC.class), () -> {
            Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = board.registerNewObjective("Main", Criteria.DUMMY, Component.text("§cGraphMC§4-§d2"));
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score score0 = objective.getScore("§eSize§7: §a" + GraphHandler.getGraphSize() + "§7, §eAccuracy§7: §a" + GraphHandler.getGraphAccuracy() + "§eP§7/§2Block");
            score0.setScore(0);
            Score score1 = objective.getScore("§eGraphs§7: §a" + GraphHandler.getGraphs().size() + "§7/§c12§7, " +"§eRadius§7: §a" + GraphHandler.getGraphRadius());
            score1.setScore(1);
            Score score2 = objective.getScore("§eMarkers§7: §a" + GraphHandler.getGraphOrigin().getWorld().getEntitiesByClass(Marker.class).size() + "§7/§c25000");
            score2.setScore(2);
            for (int i = 0; i < GraphHandler.getGraphs().size(); i++) {
                Graph g = GraphHandler.getGraphs().get(i);
                String t = g.isGraphVisible() ? "§f⬛" : "§7⬜";
                Score s = objective.getScore(t + " §dy§f=" + g.getDisplayExpression());
                s.setScore(i + 3);
            }
            Bukkit.getOnlinePlayers().forEach(p -> p.setScoreboard(board));
        }, 0, 20);
    }
    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private ScoreboardHandler() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
