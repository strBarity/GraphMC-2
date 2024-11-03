package graphmctwo.main;

import graphmctwo.commands.GraphCommand;
import graphmctwo.commands.ItemCommand;
import graphmctwo.config.Config;
import graphmctwo.global.EventListener;
import graphmctwo.graph.*;
import graphmctwo.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Marker;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * GraphMC - 마인크래프트 그래핑 계산기: 플러그인 메인 클래스
 *
 * @author Barity_
 */
public final class GraphMC extends JavaPlugin {
    /**
     * 채팅 등의 출력에 쓰이는 접두사입니다.
     */
    public static final String INDEX = "§4[§cGraphMC§4-§d2§4] §f";

    /**
     * 플러그인이 활성화되었을 때 실행되는 코드입니다. 실행하는 작업은 다음과 같습니다:
     * 플러그인 활성화 메시지 전송, 플러그인 그래프 커맨드 등록, 플러그인 아이템 커맨드 등록,
     * 그래프 설정값 데이터 불러오기, 그래프 로딩 바 등록, 정적분 막대기 처리 프로세스 등록,
     * 글로벌 이벤트 등록, 생성된 Marker 제거, 현재 있는 플레이어의 데이터 생성.
     */
    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a플러그인이 활성화되었습니다.");

        PluginCommand graph = getCommand("graph");
        if (graph != null) {
            graph.setExecutor(new GraphCommand());
            graph.setTabCompleter(new GraphCommand());
            Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 커맨드를 등록했습니다.");
        }
        PluginCommand items = getCommand("items");
        if (items != null) {
            items.setExecutor(new ItemCommand());
            items.setTabCompleter(new ItemCommand());
            Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 아이템 커맨드를 등록했습니다.");
        }

        ScoreboardHandler.startScoreboard();
        GraphCrossHandler.startSyncGraphCrosses();
        DefiniteIntegralHandler.startHandlingDefiniteIntegralSticks();

        Config.getConfig("graphSettingsData").loadData();
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 설정값 데이터를 불러왔습니다.");

        Bukkit.getOnlinePlayers().forEach(GraphHandler::addLoadingBarViewer);
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 그래프 로딩 바를 등록했습니다.");

        Bukkit.getPluginManager().registerEvents(new DefiniteIntegralEventListener(), this);
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 정적분 막대기 처리 이벤트를 등록했습니다.");

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§a성공적으로 글로벌 이벤트를 등록했습니다.");

        GraphHandler.getGraphOrigin().getWorld().getEntitiesByClass(Marker.class).forEach(Entity::remove);
        Bukkit.getOnlinePlayers().forEach(PlayerData::createPlayerData);
    }

    /**
     * 플러그인이 비활성화되었을 때 실행되는 코드입니다. 현재는 플러그인 종료 메시지를 출력하는 기능밖에 없습니다.
     */
    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(GraphMC.INDEX + "§c플러그인이 비활성화되었습니다.");
    }
}
