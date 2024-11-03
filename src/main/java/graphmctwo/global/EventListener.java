package graphmctwo.global;

import graphmctwo.main.GraphMC;
import graphmctwo.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 전반적인 이벤트에 대한 {@link Listener} 입니다.
 */
public class EventListener implements Listener {
    /**
     * 플레이어가 서버에 들어왔을 때 플레이어의 데이터 추가에 대해 처리하고,
     * 입장 메시지를 설정합니다.
     * @param e {@link PlayerJoinEvent}
     */
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        if (!PlayerData.doPlayerDataExists(e.getPlayer())) {
            PlayerData.createPlayerData(e.getPlayer());
        }
        e.joinMessage(Component.text(GraphMC.INDEX + "§d").append(e.getPlayer().name()).append(Component.text("§5님이 게임에 참여했습니다.")));
    }
}
