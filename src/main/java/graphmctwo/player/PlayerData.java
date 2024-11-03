package graphmctwo.player;

import graphmctwo.graph.Graph;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {
    private Graph selectedGraph;
    private Graph secondGraph;
    private double selectedDefiniteIntegralPoint;
    private static final Map<Player, PlayerData> PLAYER_DATA_MAP = new ConcurrentHashMap<>();

    public PlayerData(Graph selectedGraph, Graph secondGraph, double selectedDefiniteIntegralPoint) {
        this.selectedGraph = selectedGraph;
        this.secondGraph = secondGraph;
        this.selectedDefiniteIntegralPoint = selectedDefiniteIntegralPoint;
    }


    public Graph getSecondGraph() {
        return secondGraph;
    }

    public void setSecondGraph(Graph secondGraph) {
        this.secondGraph = secondGraph;
    }

    public static PlayerData getPlayerData(Player p) {
        return PLAYER_DATA_MAP.get(p);
    }

    public static void createPlayerData(Player p) {
        PLAYER_DATA_MAP.put(p, new PlayerData(null, null, 0));
    }

    public Graph getSelectedGraph() {
        return selectedGraph;
    }

    public void setSelectedGraph(@Nullable Graph selectedGraph) {
        this.selectedGraph = selectedGraph;
    }

    public double getSelectedDefiniteIntegralPoint() {
        return selectedDefiniteIntegralPoint;
    }

    public void setSelectedDefiniteIntegralPoint(double selectedDefiniteIntegralPoint) {
        this.selectedDefiniteIntegralPoint = selectedDefiniteIntegralPoint;
    }
}
