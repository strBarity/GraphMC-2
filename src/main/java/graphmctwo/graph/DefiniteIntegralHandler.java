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

    public static void startHandlingDefiniteIntegralSticks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                    String s;
                    Graph g = PlayerData.getPlayerData(p).getSelectedGraph();
                    if (g == null) {
                        s = "§c지정된 그래프 없음";
                    } else {
                        int index = GraphHandler.getGraphs().indexOf(g);
                        String s1 = index == 0 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index - 1).getUserExpression();
                        String s2 = index == GraphHandler.getGraphs().size() - 1 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index + 1).getUserExpression();
                        s = s1 + "  §e◀    §dy§f=" + g.getUserExpression() + "    §e▶  " + s2;
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
        if (loc.getX() > firstPoint) {
            for (x = firstPoint; x <= loc.getX(); x = MathPlus.roundToNthDecimal(x + 0.075)) {
                processY(p, g, x);
            }
            double d = MathPlus.roundToNthDecimal(MathPlus.definiteIntegral(g.getExpression(), "x", loc.getX(), firstPoint));
            String s = d == 0.0 ? "§c계산 불가" : Double.toString(d);
            p.showTitle(Title.title(Component.text("§a" + s), Component.text("§e색칠한 구역의 넓이 §5(§6∫§e" + NumberParser.toSubScript(Double.toString(firstPoint)) + NumberParser.toSuperScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + " " + g.getUserExpression() + " §4dx§5)"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
        } else {
            for (x = firstPoint; x >= loc.getX(); x = MathPlus.roundToNthDecimal(x - 0.075)) {
                processY(p, g, x);
            }
            double d = MathPlus.roundToNthDecimal(MathPlus.definiteIntegral(g.getExpression(), "x", firstPoint, loc.getX()));
            String s = d == 0.0 ? "§c계산 불가" : Double.toString(d);
            p.showTitle(Title.title(Component.text("§a" + s), Component.text("§e색칠한 구역의 넓이 §5(§6∫§e" + NumberParser.toSubScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + NumberParser.toSuperScript(Double.toString(firstPoint)) + " " + g.getUserExpression() + " §4dx§5)"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
        }
    }

    private static void processY(Player p, @NotNull Graph g, double x) {
        Color c = Color.fromRGB(255 - g.getGraphColor().getRed(), 255 - g.getGraphColor().getGreen(), 255 - g.getGraphColor().getBlue());
        if (g.getValues().getOrDefault(x, 0.0) > 0) {
            for (double y = 0; y <= g.getValues().get(x); y = MathPlus.roundToNthDecimal(y + 0.075)) {
                p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), x + 0.5, y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, 1.25F), true);
            }
        } else if (g.getValues().getOrDefault(x, 0.0) < 0) {
            for (double y = 0; y >= g.getValues().get(x); y = MathPlus.roundToNthDecimal(y - 0.075)) {
                p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), x + 0.5, y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, 1.25F), true);
            }
        }
    }
}
