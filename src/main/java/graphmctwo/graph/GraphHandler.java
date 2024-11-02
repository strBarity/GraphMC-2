package graphmctwo.graph;

import graphmctwo.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Marker;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GraphHandler {
    private static final Config config = Config.getConfig("graphSettingsData");
    private static final List<Graph> graphs = new ArrayList<>();
    private static Location graphOrigin;
    private static double graphRadius;
    private static double graphAccuracy;
    private static double graphSize;
    private static final Map<Marker, Integer> taskId = new ConcurrentHashMap<>();
    private static final BossBar LOADING_BAR = Bukkit.createBossBar("§b로딩 중...", BarColor.RED, BarStyle.SOLID);
    private static final double N = 5;

    static {
        if (config.get("origin") != null) {
            String[] location = config.get("origin").toString().split(",");
            World world = Bukkit.getWorld(location[0]);
            graphOrigin = world != null ? new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])) : Bukkit.getWorlds().getFirst().getSpawnLocation();
        } else {
            graphOrigin = Bukkit.getWorlds().getFirst().getSpawnLocation();
        }
        graphRadius = config.get("radius") != null ? Double.parseDouble(config.get("radius").toString()) : 10.0;
        graphAccuracy = config.get("accuracy") != null ? Double.parseDouble(config.get("accuracy").toString()) : 10.0;
        graphSize = config.get("size") != null ? Double.parseDouble(config.get("size").toString()) : 1.0;
        LOADING_BAR.setVisible(false);
    }

    public static void addBossBarViewer(Player p) {
        LOADING_BAR.addPlayer(p);
    }

    public static List<Graph> getGraphs() {
        return graphs;
    }

    public static double getN() {
        return N;
    }

    public static @Nullable Graph getGraph(String expression) {
        for (Graph g : graphs) {
            if (g.getExpression().equals(expression)) {
                return g;
            }
        }
        return null;
    }

    public static void setTaskId(Marker marker, int id) {
        taskId.put(marker, id);
    }

    public static Integer getTaskId(Marker marker) {
        return taskId.get(marker) == null ? 0 : taskId.get(marker);
    }

    public static void setLoadingBar(double d) {
        LOADING_BAR.setProgress(d);
    }

    public static void setLoadingBarVisible(boolean b) {
        LOADING_BAR.setVisible(b);
    }

    public static @NotNull Graph addGraph(String graphExpression) {
        Graph graph = new Graph(graphExpression);
        graphs.add(graph);
        return graph;
    }

    public static void removeGraph(String graphExpression) {
        List<Graph> graphsClone = new ArrayList<>(graphs);
        for (Graph g : graphsClone) {
            if (g.getExpression().equals(graphExpression)) {
                graphs.remove(g);
                return;
            }
        }
    }

    public static Location getGraphOrigin() {
        return graphOrigin;
    }

    public static void setGraphOrigin(Location graphOrigin) {
        GraphHandler.graphOrigin = graphOrigin;
    }

    public static Double getGraphAccuracy() {
        return graphAccuracy;
    }

    public static void setGraphAccuracy(Double graphAccuracy) {
        GraphHandler.graphAccuracy = graphAccuracy;
    }

    public static Double getGraphRadius() {
        return graphRadius;
    }

    public static void setGraphRadius(Double graphRadius) {
        GraphHandler.graphRadius = graphRadius;
    }

    public static double getGraphSize() {
        return graphSize;
    }

    public static void setGraphSize(Float graphSize) {
        GraphHandler.graphSize = graphSize;
    }

    private GraphHandler() {

    }
}
