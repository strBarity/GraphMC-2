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

/**
 * 그래프의 전반적인 과정을 처리하는 클래스입니다.
 * @see Graph
 */
public class GraphHandler {
    /**
     * 그래프의 설정값 파일입니다.
     */
    private static final Config config = Config.getConfig("graphSettingsData");
    /**
     * 현재 존재하는 모든 그래프들의 리스트입니다. {@link List} 타입이지만 이론상 중복 원소가 존재할 수 없습니다.
     */
    private static final List<Graph> graphs = new ArrayList<>();
    /**
     * 그래프의 원점입니다. 파일로부터 불러오며 기본값은 첫 번째 월드의 스폰입니다.
     */
    private static Location graphOrigin;
    /**
     * 그래프의 범위(반경)입니다. 예를 들어 <code>50.0</code>일 경우 원점으로부터 x, y축으로
     * 각각 <code>-25</code>블록, <code>+25</code>블록까지 그래프를 표시합니다.
     * <code>100</code>을 넘어갈 경우 서버 성능에 영향을 줄 수 있습니다.
     */
    private static double graphRadius;
    /**
     * 그래프의 정확도로, 한 블록당 표시되는 입자 수입니다. 기본값은 <code>10.0</code>입니다.
     * <code>100</code>을 넘어갈 경우 서버 성능에 영향을 줄 수 있습니다.
     */
    private static double graphAccuracy;
    /**
     * 그래프 선의 굵기입니다. 기본값은 <code>1.0</code>입니다.
     */
    private static double graphSize;
    /**
     * 그래프 입자의 표시를 반복하는 작업의 taskId를 할당하여 작업의 취소가 가능하게 하는 맵입니다.
     * 그래프의 표시는 동시에 진행되는 경우가 많기 때문에 {@link ConcurrentHashMap}을 사용합니다.
     */
    private static final Map<Marker, Integer> taskId = new ConcurrentHashMap<>();
    /**
     * 그래프의 로딩 바입니다. 보이는 경우는 많지 않지만 복잡한 함수의 경우 그래프가 표시되는 데 시간이 걸리기에,
     * 그래프가 얼마나 로딩되어가는 지 표시해주는 역할을 합니다.
     */
    private static final BossBar LOADING_BAR = Bukkit.createBossBar("§b로딩 중...", BarColor.RED, BarStyle.SOLID);
    /**
     * 이 플러그인에 사용되는 연산에서 반올림할 소수점의 자리입니다. <code>5</code>가 권장됩니다.
     */
    private static final double N = 5;

    /*
     그래프의 원점, 범위(반경), 정확도(한 블록당 입자 수), 선의 굵기를 파일로부터 불러오고, 로딩 바를 보이지 않도록 합니다.
     만약 파일로부터 불러온 값이 없을 경우, 기본값을 파일에 저장하고 해당 값을 불러옵니다.
     */
    static {
        String origin = "origin";
        String radius = "radius";
        String accuracy = "accuracy";
        String size = "size";
        Object originObject = config.get(origin);
        if (originObject != null) {
            String[] location = originObject.toString().split(",");
            World world = Bukkit.getWorld(location[0]);
            graphOrigin = world != null ? new Location(world, Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])) : Bukkit.getWorlds().getFirst().getSpawnLocation();
        } else {
            graphOrigin = Bukkit.getWorlds().getFirst().getSpawnLocation();
        }
        Object radiusObject = config.get(radius);
        Object accuracyObject = config.get(accuracy);
        Object sizeObject = config.get(size);
        if (radiusObject != null) {
            graphRadius = Double.parseDouble(radiusObject.toString());
        } else {
            graphRadius = 10.0;
            config.set(radius, 10.0);
        }
        if (accuracyObject != null) {
            graphAccuracy = Double.parseDouble(accuracyObject.toString());
        } else {
            graphAccuracy = 10.0;
            config.set(accuracy, 10.0);
        }
        if (sizeObject != null) {
            graphSize = Double.parseDouble(sizeObject.toString());
        } else {
            graphSize = 1.0;
            config.set(size, 1.0);
        }
        LOADING_BAR.setVisible(false);
    }

    /**
     * 로딩 바가 보이는 플레이어를 추가합니다.
     *
     * @param p 로딩 바가 보이게 될 플레이어
     */
    public static void addLoadingBarViewer(@NotNull Player p) {
        LOADING_BAR.addPlayer(p);
    }

    /**
     * 현재 존재하는 모든 그래프들의 {@link List}를 반환합니다.
     *
     * @return 현재 존재하는 모든 그래프들이 담긴 리스트, 비어있을 수 있음
     */
    public static @NotNull List<Graph> getGraphs() {
        return graphs;
    }

    /**
     * 이 플러그인에 사용되는 연산에서 반올림할 소수점의 자리를 반환합니다. 권장되는 값은 <code>5</code> 입니다.
     *
     * @return 이 플러그인에 사용되는 연산에서 반올림할 소수점의 자리
     */
    public static double getN() {
        return N;
    }

    /**
     * 현재 존재하는 그래프 중 <code>expression</code>을 식으로 가지는 그래프를 반환합니다.
     * 만약 해당하는 그래프가 없을 경우 <code>null</code>을 반환합니다.
     *
     * @param expression 그래프의 식
     * @return <code>expression</code>에 해당하는 그래프가 있다면 해당 그래프를 반환, 없다면 <code>null</code> 반환
     */
    public static @Nullable Graph getGraph(@NotNull String expression) {
        for (Graph g : graphs) {
            if (g.getExpression().equals(expression)) {
                return g;
            }
        }
        return null;
    }

    /**
     * <code>marker</code>가 지속적으로 입자를 표시하기 위한 작업의 ID를 저장합니다.
     *
     * @param marker <code>id</code>에 해당되는 {@link Marker}
     * @param id     <code>marker</code>의 작업 ID
     */
    public static void setTaskId(@NotNull Marker marker, int id) {
        taskId.put(marker, id);
    }

    /**
     * <code>marker</code>가 지속적으로 입자를 표시하기 위한 작업의 ID를 반환합니다.
     * 만약 <code>marker</code>에 해당하는 작업 ID가 없다면 <code>-1</code>을 반환합니다.
     *
     * @param marker 작업 ID를 불러올 {@link Marker}
     * @return <code>marker</code>에 해당되는 작업 ID, 만약 해당되는 작업 아이디가 없다면 <code>-1</code> 반환
     */
    public static int getTaskId(@NotNull Marker marker) {
        return taskId.getOrDefault(marker, -1);
    }

    /**
     * 로딩 바의 진행도를 설정합니다.
     *
     * @param d 로딩 바의 진행도
     */
    public static void setLoadingBarProgress(double d) {
        LOADING_BAR.setProgress(d);
    }

    /**
     * 로딩 바를 보이거나 숨깁니다.
     *
     * @param b 로딩 바의 표시 여부
     */
    public static void setLoadingBarVisible(boolean b) {
        LOADING_BAR.setVisible(b);
    }

    /**
     * 그래프를 추가합니다.
     *
     * @param graphExpression 그래프의 식
     * @return 생성된 그래프
     */
    public static @NotNull Graph addGraph(@NotNull String graphExpression) {
        Graph graph = new Graph(graphExpression);
        graphs.add(graph);
        return graph;
    }

    /**
     * 그래프를 제거합니다. 해당 식에 해당하는 그래프가 존재하지 않으면 작업은 무시됩니다.
     *
     * @param graphExpression 제거할 그래프의 식
     */
    public static void removeGraph(@NotNull String graphExpression) {
        List<Graph> graphsClone = new ArrayList<>(graphs);
        for (Graph g : graphsClone) {
            if (g.getExpression().equals(graphExpression)) {
                graphs.remove(g);
                return;
            }
        }
    }

    /**
     * 그래프의 원점을 반환합니다.
     *
     * @return 그래프의 원점
     */
    public static @NotNull Location getGraphOrigin() {
        return graphOrigin;
    }

    /**
     * 그래프의 원점을 설정합니다.
     *
     * @param graphOrigin 그래프의 원점
     */
    public static void setGraphOrigin(@NotNull Location graphOrigin) {
        GraphHandler.graphOrigin = graphOrigin;
    }

    /**
     * 그래프의 정확도, 즉 한 블록당 그래프의 입자 수를 반환합니다.
     *
     * @return 그래프의 정확도 (한 블록당 그래프의 입자 수)
     */
    public static double getGraphAccuracy() {
        return graphAccuracy;
    }

    /**
     * 그래프의 정확도, 즉 한 블록당 그래프의 입자 수를 설정합니다.
     *
     * @param graphAccuracy 설정할 그래프의 정확도 (한 블록당 그래프의 입자 수)
     */
    public static void setGraphAccuracy(double graphAccuracy) {
        GraphHandler.graphAccuracy = graphAccuracy;
    }

    /**
     * 그래프의 범위(반경)을 반환합니다.
     *
     * @return 그래프의 범위(반경)
     */
    public static double getGraphRadius() {
        return graphRadius;
    }

    /**
     * 그래프의 범위(반경)을 설정합니다.
     * 100이 넘어갈 시 서버 성능에 영향을 끼칠수 있습니다.
     *
     * @param graphRadius 설정할 그래프의 범위(반경)
     */
    public static void setGraphRadius(double graphRadius) {
        GraphHandler.graphRadius = graphRadius;
    }

    /**
     * 그래프 선의 굵기를 반환합니다.
     *
     * @return 그래프 선의 굵기
     */
    public static double getGraphSize() {
        return graphSize;
    }

    /**
     * 그래프 선의 굵기를 설정합니다.
     *
     * @param graphSize 설정할 그래프 선의 굵기
     */
    public static void setGraphSize(double graphSize) {
        GraphHandler.graphSize = graphSize;
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private GraphHandler() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
