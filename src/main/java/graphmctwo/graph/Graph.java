package graphmctwo.graph;

import graphmctwo.calculator.ExpressionParser;
import graphmctwo.calculator.FunctionCalculator;
import graphmctwo.calculator.MathPlus;
import graphmctwo.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Graph {
    private final String expression;
    private final String userExpression;
    private boolean graphVisible = false;
    private final Color graphColor;
    private final Map<Double, Double> values = new ConcurrentHashMap<>();

    public Graph(String expression) {
        this.expression = expression;
        this.userExpression = ExpressionParser.userFunction(expression);
        Random random = new Random();
        graphColor = Color.fromRGB(random.nextInt(0, 255), random.nextInt(0, 255), random.nextInt(0, 255));
    }

    public String getExpression() {
        return expression;
    }

    public Map<Double, Double> getValues() {
        return values;
    }

    public String getUserExpression() {
        return userExpression;
    }

    public boolean isGraphVisible() {
        return graphVisible;
    }

    public Color getGraphColor() {
        return graphColor;
    }

    public boolean toggle() {
        if (graphVisible) {
            graphVisible = false;
        } else {
            graphVisible = true;
            GraphHandler.setLoadingBarVisible(true);
            try {
                for (double i = 0.0; i < GraphHandler.getGraphRadius() * GraphHandler.getGraphAccuracy(); i++) {
                    GraphHandler.setLoadingBar(i / (GraphHandler.getGraphRadius() * GraphHandler.getGraphAccuracy()));
                    double x = MathPlus.roundToNthDecimal((i / GraphHandler.getGraphAccuracy()) - (GraphHandler.getGraphRadius() / 2));
                    double y = MathPlus.roundToNthDecimal(FunctionCalculator.evaluate(expression.replace("x", "(" + x + ")"))) + GraphHandler.getGraphOrigin().getY();
                    if (Double.isNaN(y) || Math.abs(y) > GraphHandler.getGraphRadius() / 2) {
                        continue;
                    }
                    values.put(x, y);
                    Location l = new Location(GraphHandler.getGraphOrigin().getWorld(), GraphHandler.getGraphOrigin().getX() + x + 0.5, GraphHandler.getGraphOrigin().getY() + y + 0.5, GraphHandler.getGraphOrigin().getZ() + 1.05);
                    Marker mk = Objects.requireNonNull(GraphHandler.getGraphOrigin().getWorld().spawn(l, Marker.class));
                    mk.setGravity(false);
                    mk.setCustomNameVisible(false);
                    GraphHandler.setTaskId(mk, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
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
