package graphmctwo.graph;

import graphmctwo.calculator.ExpressionParser;
import graphmctwo.calculator.FunctionCalculator;
import graphmctwo.calculator.MathPlus;
import graphmctwo.main.GraphMC;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * "그래프" 그 자체 클래스입니다. 그래프 기능을 구현하는 데에 가장 핵심이 됩니다.
 * @see GraphHandler
 */
public class Graph {
    /**
     * 그래프의 시스템 식입니다. 이 식은 유저가 볼 수 없으며, 시스템에서만 사용됩니다.
     */
    private final String expression;
    /**
     * 그래프의 디스플레이 식입니다. 유저에게는 이 식으로 보여집니다.
     */
    private final String displayExpression;
    /**
     * 그래프가 보이는 지의 여부입니다.
     */
    private boolean graphVisible = false;
    /**
     * 그래프의 색상입니다. 그래프가 처음 생성될 때 랜덤한 RGB를 가지고 생성됩니다.
     */
    private final Color graphColor;
    /**
     * 그래프의 모든 (x, y) 순서쌍을 나타낸 {@link Map}으로, 작업이 동시에 진행되는 경우가 있기 때문에 {@link ConcurrentHashMap}을 사용합니다.
     */
    private final Map<Double, Double> values = new ConcurrentHashMap<>();

    /**
     * 그래프를 생성합니다.
     * @param expression 그래프의 식
     */
    public Graph(@NotNull String expression) {
        this.expression = expression;
        this.displayExpression = ExpressionParser.toDisplayExpression(expression);
        Random random = new Random();
        graphColor = Color.fromRGB(random.nextInt(0, 255), random.nextInt(0, 255), random.nextInt(0, 255));
    }

    /**
     * 그래프의 시스템 식을 반환합니다.
     * @return 그래프의 식
     */
    public @NotNull String getExpression() {
        return expression;
    }

    /**
     * 그래프의 모든 (x, y) 값에 해당하는 {@link Map}을 반환합니다.
     * @return 그래프의 모든 (x, y) 값에 해당하는 {@link Map}
     */
    public @NotNull Map<Double, Double> getValues() {
        return values;
    }

    /**
     * 그래프의 디스플레이 식을 반환합니다.
     * @return 그래프의 디스플레이 식
     */
    public @NotNull String getDisplayExpression() {
        return displayExpression;
    }

    /**
     * 그래프의 표시 여부를 반환합니다.
     * @return 그래프의 표시 여부
     */
    public boolean isGraphVisible() {
        return graphVisible;
    }

    /**
     * 그래프의 색상을 반환합니다.
     * @return 그래프의 색상
     */
    public @NotNull Color getGraphColor() {
        return graphColor;
    }

    /**
     * 그래프의 표시 여부를 전환합니다. 만약 그래프가 켜져 있다면 끄고, 꺼져 있다면 켭니다.
     * @return 새롭게 갱신된 그래프의 표시 여부 (만약 원래 꺼져 있었다면 <code>true</code> 반환, 켜져 있었다면 <code>false</code> 반환)
     * @throws FailedToShowGraphException 그래프를 처음 추가할 때 0을 식에 대입해 오류가 난다면
     * 추가하지 않는 방식을 통해 유효성을 검사하기 때문에 이론상 던져지지 않는 예외이지만,
     * 이 검사에도 불구하고 만약 계산 과정에서 {@link IllegalArgumentException}이 던져진다면 던져짐.
     */
    public boolean toggle() {
        if (graphVisible) {
            graphVisible = false;
        } else {
            graphVisible = true;
            GraphHandler.setLoadingBarVisible(true);
            try {
                for (double i = 0.0; i < GraphHandler.getGraphRadius() * GraphHandler.getGraphAccuracy(); i++) {
                    GraphHandler.setLoadingBarProgress(i / (GraphHandler.getGraphRadius() * GraphHandler.getGraphAccuracy()));
                    double x = MathPlus.roundToNthDecimal((i / GraphHandler.getGraphAccuracy()) - (GraphHandler.getGraphRadius() / 2));
                    double y = MathPlus.roundToNthDecimal(FunctionCalculator.evaluate(expression.replace("x", "(" + x + ")")));
                    if (Double.isNaN(y) || Math.abs(y) > GraphHandler.getGraphRadius() / 2) {
                        if (y > 0) {
                            values.put(x, GraphHandler.getGraphRadius() / 2);
                        } else if (y < 0) {
                            values.put(x, GraphHandler.getGraphRadius() / -2);
                        }
                        continue;
                    }
                    values.put(x, y);
                    Location l = new Location(GraphHandler.getGraphOrigin().getWorld(), GraphHandler.getGraphOrigin().getX() + x + 0.5, GraphHandler.getGraphOrigin().getY() + y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.15);
                    Marker mk = Objects.requireNonNull(GraphHandler.getGraphOrigin().getWorld().spawn(l, Marker.class));
                    mk.setGravity(false);
                    mk.setCustomNameVisible(false);
                    GraphHandler.setTaskId(mk, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(GraphMC.class), () -> {
                        if (!graphVisible) {
                            Bukkit.getScheduler().cancelTask(GraphHandler.getTaskId(mk));
                            mk.remove();
                            values.clear();
                        } else {
                            mk.getWorld().spawnParticle(Particle.DUST, mk.getLocation(), 1, 0, 0, 0, 0, new Particle.DustOptions(graphColor, (float) GraphHandler.getGraphSize()), true);
                            mk.teleport(l);
                        }
                    }, 0, 5L));
                }
            } catch (Exception e) {
                throw new FailedToShowGraphException(e);
            } finally {
                GraphHandler.setLoadingBarVisible(false);
            }
        }
        return graphVisible;
    }
}
