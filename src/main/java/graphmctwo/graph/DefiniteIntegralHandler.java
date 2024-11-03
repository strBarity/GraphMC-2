package graphmctwo.graph;

import graphmctwo.calculator.MathPlus;
import graphmctwo.calculator.NumberParser;
import graphmctwo.main.GraphMC;
import graphmctwo.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * 정적분 프로세스를 처리하는 클래스입니다.
 *
 * @see GraphHandler
 * @see Graph
 */
public class DefiniteIntegralHandler {
    /**
     * 색칠된 영역에서의 입자의 크기입니다. <code>2.25F</code>가 권장됩니다.
     */
    private static final float PAINTED_AREA_PARTICLE_SIZE = 2.25F;

    /**
     * 정적분 막대기를 들고 있을 때 정적분을 처리하는 프로세스를 시작합니다.
     */
    public static void startHandlingDefiniteIntegralSticks() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(GraphMC.class), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
                    String s;
                    PlayerData d = PlayerData.getPlayerData(p);
                    Graph g = d.getSelectedGraph();
                    if (g == null) {
                        s = "§c지정된 그래프 없음";
                    } else {
                        int index = GraphHandler.getGraphs().indexOf(g);
                        String s1 = index == 0 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index - 1).getDisplayExpression();
                        String s2 = index == GraphHandler.getGraphs().size() - 1 ? "§c선택 해제" : "§7y§7=" + GraphHandler.getGraphs().get(index + 1).getDisplayExpression();
                        String s3 = d.getSecondGraph() == null ? "" : "§2① ";
                        String s4 = d.getSecondGraph() == null ? "" : "       §8|       §2② §dy§f=" + d.getSecondGraph().getDisplayExpression();
                        s = s1 + "  §e◀    " + s3 + "§dy§f=" + g.getDisplayExpression() + "    §e▶  " + s2 + s4;
                        if (g.isGraphVisible()) {
                            processDefiniteIntegral(p, g);
                        }
                    }
                    p.sendActionBar(Component.text(s));
                }
            }
        }, 0, 7);
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 정적분 막대기 처리 프로세스를 시작했습니다.");
    }

    /**
     * 정적분을 처리합니다. 만약 처리 과정에서 {@link IllegalDefiniteIntegralException}이 발생했다면,
     * "계산 오류"를 플레이어에게 띄우며 버그 제보를 요청합니다.
     *
     * @param p 정적분 막대기를 들고있는 플레이어
     * @param g 선택된 정적분을 할 그래프
     */
    private static void processDefiniteIntegral(@NotNull Player p, @NotNull Graph g) {
        Location loc = p.getEyeLocation().add(p.getEyeLocation().getDirection().normalize().multiply(p.getEyeLocation().distance(p.getTargetBlock(null, 500).getLocation()))).add(-0.5, -0.5, 1.25);
        double x;
        double firstPoint = PlayerData.getPlayerData(p).getSelectedDefiniteIntegralPoint();
        double d;
        String s1;
        String s2 = "";
        String s3;
        String e = g.getExpression();
        Graph s = PlayerData.getPlayerData(p).getSecondGraph();
        if (s == null || s.equals(g)) {
            s3 = g.getDisplayExpression();
        } else {
            e = e + "-" + s.getExpression();
            s3 = "§c{" + g.getDisplayExpression() + "§c} §b- §c{" + s.getDisplayExpression() + "§c}";
        }
         d = MathPlus.roundToNthDecimal(MathPlus.definiteIntegral(e, "x", firstPoint, loc.getX()));
        s1 = d == 0.0 ? "§c계산 불가" : Double.toString(d);
        try {
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
        } catch (IllegalDefiniteIntegralException exception) {
            exception.printStackTrace();
            s1 = "§4계산 오류 §c(개발자에게 제보해주세요)";
            s2 = loc.getX() > firstPoint ? NumberParser.toSubScript(Double.toString(firstPoint)) + NumberParser.toSuperScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + " " : NumberParser.toSubScript(Double.toString(MathPlus.roundToNthDecimal(loc.getX(), 1))) + NumberParser.toSuperScript(Double.toString(firstPoint)) + " ";
        } finally {
            p.showTitle(Title.title(Component.text("§a" + s1), Component.text("§e색칠한 구역의 넓이 §5(§6∫§e" + s2 + s3 + " §4dx§5)"), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));
        }
    }

    /**
     * 계산된 y 좌표에 대해 입자를 표시합니다. 만약 두 그래프로 둘러쌓인 곳의 넓이를 구하는 경우 {@link DefiniteIntegralHandler#processYOnSurrounded(Player, double, Graph, Graph)}가 사용됩니다.
     *
     * @param p 정적분 막대기를 들고있는 플레이어
     * @param g 선택된 정적분을 할 그래프
     * @param x y 좌표에 대한 현재 x 좌표
     */
    private static void processY(@NotNull Player p, @NotNull Graph g, double x) {
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
                processYOnSurrounded(p, x, g, s);
            } else if (g.getValues().getOrDefault(x, 0.0) < s.getValues().getOrDefault(x, 0.0)) {
                processYOnSurrounded(p, x, s, g);
            }
        }
    }

    /**
     * 두 그래프로 둘러쌓인 부분의 입자를 표시합니다.
     *
     * @param p  정적분 막대기를 들고있는 플레이어
     * @param x  y 좌표에 대한 현재 x 좌표
     * @param g1 플레이어가 선택한 그래프와 두 번째 그래프 중 현재 <code>x</code>에 대해 함숫값이 <code>g2</code>보다 큰 그래프
     * @param g2 플레이어가 선택한 그래프와 두 번째 그래프 중 현재 <code>x</code>에 대해 함숫값이 <code>g1</code>보다 작은 그래프
     * @throws IllegalDefiniteIntegralException 현재 <code>x</code>에 대한 <code>g1</code>의 함숫값이 <code>g2</code>보다 작은 경우
     */
    private static void processYOnSurrounded(@NotNull Player p, double x, @NotNull Graph g1, @NotNull Graph g2) {
        if (g2.getValues().getOrDefault(x, 0.0) > g1.getValues().getOrDefault(x, 0.0)) {
            throw new IllegalDefiniteIntegralException("x=" + x + "에서 y=" + g1.getDisplayExpression() + "의 함숫값이 y=" + g2.getDisplayExpression() + "보다 커야 하지만, 실제로는 반대입니다.");
        }
        Color c = Color.fromRGB(255 - ((g1.getGraphColor().getRed() + g2.getGraphColor().getRed()) / 2), 255 - ((g1.getGraphColor().getGreen() + g2.getGraphColor().getGreen()) / 2), 255 - ((g1.getGraphColor().getBlue() + g2.getGraphColor().getBlue()) / 2));
        for (double y = g2.getValues().getOrDefault(x, 0.0); y <= g1.getValues().get(x); y = MathPlus.roundToNthDecimal(y + 1.0 / (GraphHandler.getGraphAccuracy() / 10))) {
            p.getWorld().spawnParticle(Particle.DUST, new Location(p.getWorld(), GraphHandler.getGraphOrigin().getX() + x + 0.5, GraphHandler.getGraphOrigin().getY() + y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.1), 1, 0, 0, 0, 0, new Particle.DustOptions(c, PAINTED_AREA_PARTICLE_SIZE), true);
        }
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     *
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private DefiniteIntegralHandler() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
