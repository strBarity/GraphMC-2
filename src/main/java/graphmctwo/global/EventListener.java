package graphmctwo.global;

import graphmctwo.main.Main;
import graphmctwo.player.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class EventListener implements Listener {
    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        if (PlayerData.getPlayerData(e.getPlayer()) == null) {
            PlayerData.createPlayerData(e.getPlayer());
        }
        e.joinMessage(Component.text(Main.INDEX + "§d").append(e.getPlayer().name()).append(Component.text("§5님이 게임에 참여했습니다.")));
    }
}
