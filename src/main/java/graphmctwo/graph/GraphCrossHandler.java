package graphmctwo.graph;

import graphmctwo.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;

public class GraphCrossHandler {
    private GraphCrossHandler() {

    }

    public static void startSyncGraphCrosses() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Main.class), () -> {
            for (Marker mk : GraphHandler.getGraphOrigin().getWorld().getEntitiesByClass(Marker.class)) {
                if (mk.getNearbyEntities(0.01, 0.01, 0.01).stream().anyMatch(e -> e.getType().equals(EntityType.MARKER))) {
                    GraphHandler.getGraphOrigin().getWorld().spawnParticle(Particle.END_ROD, mk.getLocation().add(0, 0, 0.1), 1, 0, 0, 0, 0);
                }
            }
        }, 0, 20);
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 그래프 교점 처리 프로세스를 시작했습니다.");
    }
}
