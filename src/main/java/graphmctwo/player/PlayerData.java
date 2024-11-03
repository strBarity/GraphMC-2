package graphmctwo.player;

import graphmctwo.graph.Graph;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 플레이어 데이터: 플레이어 개인별로 할당 된 데이터를 제어합니다.
 */
public class PlayerData {
    /**
     * 정적분 계산을 하도록 설정된 그래프로, 기본값은 <code>null</code>입니다.
     */
    private Graph selectedGraph;
    /**
     * 정적분 계산을 하도록 설정된 두 번째 그래프로, 기본값은 <code>null</code>입니다.
     */
    private Graph secondGraph;
    /**
     * 정적분 계산을 하는 시작점으로, 기본값은 <code>0</code>입니다.
     */
    private double selectedDefiniteIntegralPoint;
    /**
     * 플레이어별 데이터를 다루는 {@link Map}으로, 플레이어 데이터 갱신은 한 틱에 두 번 이상이 일어날 수 있으므로
     * {@link ConcurrentHashMap}을 사용합니다.
     */
    private static final Map<Player, PlayerData> PLAYER_DATA_MAP = new ConcurrentHashMap<>();

    /**
     * 플레이어 데이터를 생성합니다.
     *
     * @param selectedGraph                 정적분 계산을 하도록 선택 된 그래프, <code>null</code>이 가능하며 이 경우 정적분 계산은 되지 않음
     * @param secondGraph                   정적분 계산을 하도록 선택 된 두 번째 그래프, <code>null</code>이 가능하며 이 경우 둘러쌓인 넓이의 계산은 되지 않음
     * @param selectedDefiniteIntegralPoint 정적분 계산을 하는 시작점, 기본값은 <code>0</code>
     */
    public PlayerData(@Nullable Graph selectedGraph, @Nullable Graph secondGraph, double selectedDefiniteIntegralPoint) {
        this.selectedGraph = selectedGraph;
        this.secondGraph = secondGraph;
        this.selectedDefiniteIntegralPoint = selectedDefiniteIntegralPoint;
    }

    /**
     * 플레이어의 데이터가 존재하는지를 확인힙니다. 플레이어의 데이터는 한번 생성되면 삭제되는 경우가 없기 때문에
     * 대부분의 경우에는 이미 데이터가 존재해 <code>false</code>를 반환하고,
     * 플레이어가 처음 서버에 들어온 경우 및 플러그인이 리로드된 경우 등 데이터가 아직 생성되지 않은 경우에 한해서만 <code>true</code>를 반환합니다.
     * @param p 데이터가 존재하는지 확인할 플레이어
     * @return 플레이어의 데이터 존재 여부
     */
    public static boolean doPlayerDataExists(Player p) {
        return PLAYER_DATA_MAP.containsKey(p);
    }

    /**
     * 플레이어의 데이터를 반환합니다. 이론상으로는 불가능하지만 만약 플레이어의 데이터가 존재하지 않는 경우 새로운 플레이어의 데이터를 기본값으로 만들어 반환합니다.
     * @param p 데이터를 불러올 플레이어
     * @return <code>p</code>의 플레이어 데이터
     */
    public static @NotNull PlayerData getPlayerData(@NotNull Player p) {
        return PLAYER_DATA_MAP.getOrDefault(p, createPlayerData(p));
    }

    /**
     * 데이터가 생성되지 않은 플레이어에 한해 플레이어의 데이터를 생성합니다.
     * 주로 플레이어가 서버에 들어올 때나, 플러그인이 활성화될 때 이미 플레이어가 있는 경우(서버 리로드 등)
     * 플레이어 데이터를 다시 만들어 주는 역할을 합니다. 반환값은 생성된 플레이어의 데이터입니다.
     *
     * @param p 데이터를 생성할 플레이어, 데이터는 모두 해당하는 데이터의 기본값으로 처리됨
     * @return 생성된 플레이어의 데이터
     */
    public static @NotNull PlayerData createPlayerData(@NotNull Player p) {
        PlayerData d = new PlayerData(null, null, 0);
        PLAYER_DATA_MAP.put(p, d);
        return d;
    }

    /**
     * 정적분 계산을 하는 시작점을 반환합니다.
     *
     * @return 정적분 계산을 하는 시작점
     */
    public double getSelectedDefiniteIntegralPoint() {
        return selectedDefiniteIntegralPoint;
    }

    /**
     * 정적분 계산을 하도록 설정된 그래프를 반환합니다.
     *
     * @return 정적분 계산을 하도록 설정된 그래프, 데이터가 할당되어있지 않은 경우 <code>null</code> 반환
     */
    public @Nullable Graph getSelectedGraph() {
        return selectedGraph;
    }

    /**
     * 플레이어의 두 번째 그래프를 반환합니다.
     *
     * @return 플레이어의 두 번째 그래프, <code>null</code>이 반환될 수 있음
     */
    public @Nullable Graph getSecondGraph() {
        return secondGraph;
    }

    /**
     * 플레이어의 두 번째 그래프를 설정합니다.
     *
     * @param secondGraph 플레이어의 두 번째 그래프, <code>null</code> 가능
     */
    public void setSecondGraph(@Nullable Graph secondGraph) {
        this.secondGraph = secondGraph;
    }

    /**
     * 정적분 계산을 하는 그래프를 설정합니다. <code>null</code>으로 설정할 수 있습니다.
     *
     * @param selectedGraph 정적분 계산을 하도록 설정할 그래프, <code>null</code> 가능
     */
    public void setSelectedGraph(@Nullable Graph selectedGraph) {
        this.selectedGraph = selectedGraph;
    }

    /**
     * 정적분 계산을 하는 시작점을 설정합니다.
     *
     * @param selectedDefiniteIntegralPoint 정적분 계산을 하는 시작점, 기본값은 <code>0</code> (초기화 시 사용)
     */
    public void setSelectedDefiniteIntegralPoint(double selectedDefiniteIntegralPoint) {
        this.selectedDefiniteIntegralPoint = selectedDefiniteIntegralPoint;
    }
}
