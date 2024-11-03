package graphmctwo.graph;

import graphmctwo.calculator.MathPlus;
import graphmctwo.calculator.NumberParser;
import graphmctwo.main.Main;
import graphmctwo.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class DefiniteIntegralHandler {
    private DefiniteIntegralHandler() {

    }
    private static final float PAINTED_AREA_PARTICLE_SIZE = 2.25F;

    public static void startHandlingDefiniteIntegralSticks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                    String s;
                    PlayerData d = PlayerData.getPlayerData(p);
                    Graph g = d.getSelectedGraph();
                    if (g == null) {
                        s = "§c지정된 그래프 없음";
                    } else {
                        int index = GraphHandler.getGraphs().indexOf(g);
                        String s1 = index == 0 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index - 1).getUserExpression();
                        String s2 = index == GraphHandler.getGraphs().size() - 1 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index + 1).getUserExpression();
                        String s3 = d.getSecondGraph() == null ? "" : "§2① ";
                        String s4 = d.getSecondGraph() == null ? "" : "       §8|       §2② §dy§f=" + d.getSecondGraph().getUserExpression();
                        s = s1 + "  §e◀    " + s3 + "§dy§f=" + g.getUserExpression() + "    §e▶  " + s2 + s4;
                        if (g.isGraphVisible()) {
                            processDefiniteIntegral(p, g);
                        }
                    }
                    p.sendActionBar(Component.text(s));
                }
            }
        }, 0, 7);
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 그래프 정적분 막대기 처리 프로세스를 시작했습니다.");
    }

    private static void processDefiniteIntegral(@NotNull Player p, Graph g) {
        Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(p.getEyeLocation().distance(p.getTargetBlock(null, 500).getLocation()))).add(-0.5, -0.5, 1.25);
        double x;
        double firstPoint = PlayerData.getPlayerData(p).getSelectedDefiniteIntegralPoint();
        double d;
        String s1;
        String s2;
        if (loc.getX() > firstPoint) {
            for (x = firstPoint; x <= loc.getX(); x = MathPlus.roundToNthDecimal(x + 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
                processY(p, g, x);
            }
            s2 = NumberParser.toSubScript(Double.toString(firstPoint)) + NumberParser.toSuperScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + " ";
        } else {
            for (x = firstPoint; x >= loc.getX(); x = MathPlus.roundToNthDecimal(x - 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
                processY(p, g, x);
            }
            s2 = NumberParser.toSubScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + NumberParser.toSuperScript(Double.toString(firstPoint)) + " ";
        }
        String s3;
        String e = g.getExpression();
        Graph s = PlayerData.getPlayerData(p).getSecondGraph();
        if (s == null || s.equals(g)) {
            s3 = g.getUserExpression();
        } else {
            e = e + "-" + s.getExpression();
            s3 = "§c{" + g.getUserExpression() + "§c} §b- §c{" + s.getUserExpression() + "§c}";
        }
        d = MathPlus.roundToNthDecimal(MathPlus.definiteIntegral(e, "x", firstPoint, loc.getX()));
        s1 = d == 0.0 ? "§c계산 불가" : Double.toString(d);
        p.showTitle(Title.title(Component.text("§a" + s1), Component.text("§e색칠한 구역의 넓이 §5(§6∫§e" + s2 + s3 + " §4dx§5)"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
    }

    private static void processY(Player p, @NotNull Graph g, double x) {
        Color c = Color.fromRGB(255 - g.getGraphColor().getRed(), 255 - g.getGraphColor().getGreen(), 255 - g.getGraphColor().getBlue());
        Graph s = PlayerData.getPlayerData(p).getSecondGraph();
        if (s == null || s.equals(g)) {
            if (g.getValues().getOrDefault(x, 0.0) > 0) {
                for (double y = 0; y <= g.getValues().get(x); y = MathPlus.roundToNthDecimal(y + 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
                    p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), x + 0.5, y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, PAINTED_AREA_PARTICLE_SIZE), true);
                }
            } else if (g.getValues().getOrDefault(x, 0.0) < 0) {
                for (double y = 0; y >= g.getValues().get(x); y = MathPlus.roundToNthDecimal(y - 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
                    p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), x + 0.5, y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, PAINTED_AREA_PARTICLE_SIZE), true);
                }
            }
        } else if (s.isGraphVisible()) {
            if (g.getValues().getOrDefault(x, 0.0) > s.getValues().getOrDefault(x, 0.0)) {
                showSurroundedArea(p, x, g, s);
            } else if (g.getValues().getOrDefault(x, 0.0) < s.getValues().getOrDefault(x, 0.0)) {
                showSurroundedArea(p, x, s, g);
            }
        }
    }

    private static void showSurroundedArea(Player p, double x, @NotNull Graph g1, @NotNull Graph g2) {
        Color c = Color.fromRGB(255 - ((g1.getGraphColor().getRed() + g2.getGraphColor().getRed()) / 2), 255 - ((g1.getGraphColor().getGreen() + g2.getGraphColor().getGreen()) / 2), 255 - ((g1.getGraphColor().getBlue() + g2.getGraphColor().getBlue()) / 2));
        for (double y = g2.getValues().getOrDefault(x, 0.0); y <= g1.getValues().get(x); y = MathPlus.roundToNthDecimal(y + 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
            p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), GraphHandler.getGraphOrigin().getX() + x + 0.5, GraphHandler.getGraphOrigin().getY() + y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, PAINTED_AREA_PARTICLE_SIZE), true);
        }
    }
}
