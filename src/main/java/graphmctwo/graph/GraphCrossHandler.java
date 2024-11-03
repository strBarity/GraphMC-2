package graphmctwo.graph;

import graphmctwo.main.GraphMC;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 그래프들 간의 교점과 관련된 프로세스를 처리하는 클래스입니다.
 * @see GraphHandler
 * @see Graph
 */
public class GraphCrossHandler {

    /**
     * 그래프들의 교점을 처리하는 프로세스를 시작합니다. 만약 위치가 거의 비슷한(범위 0.01) {@link Marker} 두 개가 있다면, 파티클을 표시해 교점을 보여줍니다.
     */
    public static void startSyncGraphCrosses() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(GraphMC.class), () -> {
            for (Marker mk : GraphHandler.getGraphOrigin().getWorld().getEntitiesByClass(Marker.class)) {
                if (mk.getNearbyEntities(0.01, 0.01, 0.01).stream().anyMatch(e -> e.getType().equals(EntityType.MARKER))) {
                    GraphHandler.getGraphOrigin().getWorld().spawnParticle(Particle.END_ROD, mk.getLocation().add(0, 0, 0.1), 1, 0, 0, 0, 0);
                }
            }
        }, 0, 20);
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 교점 처리 프로세스를 시작했습니다.");
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private GraphCrossHandler() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
