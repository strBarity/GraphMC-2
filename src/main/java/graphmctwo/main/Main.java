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

public final class Main extends JavaPlugin {
    public static final String INDEX = "§4[§cGraphMC§4-§d2§4] §f";

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a플러그인이 활성화되었습니다.");

        PluginCommand graph = getCommand("graph");
        if (graph != null) {
            graph.setExecutor(new GraphCommand());
            graph.setTabCompleter(new GraphCommand());
            Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 그래프 커맨드를 등록했습니다.");
        }
        PluginCommand items = getCommand("items");
        if (items != null) {
            items.setExecutor(new ItemCommand());
            items.setTabCompleter(new ItemCommand());
            Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 아이템 커맨드를 등록했습니다.");
        }

        ScoreboardHandler.startScoreboard();
        GraphCrossHandler.startSyncGraphCrosses();
        DefiniteIntegralHandler.startHandlingDefiniteIntegralSticks();

        Config.getConfig("graphSettingsData").loadData();
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 그래프 설정값 데이터를 불러왔습니다.");

        Bukkit.getOnlinePlayers().forEach(GraphHandler::addBossBarViewer);
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 그래프 로딩 바를 등록했습니다.");

        Bukkit.getPluginManager().registerEvents(new DefiniteIntegralEventListener(), this);
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 정적분 막대기 처리 이벤트를 등록했습니다.");

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§a성공적으로 글로벌 이벤트를 등록했습니다.");

        GraphHandler.getGraphOrigin().getWorld().getEntitiesByClass(Marker.class).forEach(Entity::remove);
        Bukkit.getOnlinePlayers().forEach(PlayerData::createPlayerData);
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(Main.INDEX + "§c플러그인이 비활성화되었습니다.");
    }
}
