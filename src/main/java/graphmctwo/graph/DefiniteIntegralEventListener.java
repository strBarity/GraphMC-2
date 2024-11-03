package graphmctwo.graph;

import graphmctwo.calculator.MathPlus;
import graphmctwo.main.Main;
import graphmctwo.player.PlayerData;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

public class DefiniteIntegralEventListener implements Listener {
    @EventHandler
    public void onInteract(@NotNull PlayerInteractEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
            e.setCancelled(true);
            if (e.getAction().isLeftClick()) {
                Graph g = PlayerData.getPlayerData(e.getPlayer()).getSelectedGraph();
                int index = GraphHandler.getGraphs().indexOf(g);
                if (g == null || index == -1) {
                    if (GraphHandler.getGraphs().isEmpty()) {
                        e.getPlayer().sendMessage(Main.INDEX + "§c현재 존재하는 그래프가 없어 그래프를 선택할 수 없습니다.");
                    } else {
                        PlayerData.getPlayerData(e.getPlayer()).setSelectedGraph(GraphHandler.getGraphs().getFirst());
                    }
                } else {
                    PlayerData.getPlayerData(e.getPlayer()).setSelectedGraph(null);
                    if (index + 1 < GraphHandler.getGraphs().size()) {
                        PlayerData.getPlayerData(e.getPlayer()).setSelectedGraph(GraphHandler.getGraphs().get(index + 1));
                    }
                }
            } else if (e.getAction().isRightClick()) {
                double d = MathPlus.roundToNthDecimal(e.getPlayer().getEyeLocation().add(e.getPlayer().getEyeLocation().getDirection().normalize().multiply(e.getPlayer().getEyeLocation().distance(e.getPlayer().getTargetBlock(null, 500).getLocation()))).add(-0.5, -0.5, 1.25).getX(), 1);
                PlayerData.getPlayerData(e.getPlayer()).setSelectedDefiniteIntegralPoint(d);
                e.getPlayer().sendMessage(Main.INDEX + "새로운 정적분 시작점 §dx§b=" + d + "§f을(를) 설정했습니다. 이제 이 구간부터 바라보는 곳까지의 정적분을 계산합니다.");
            }
        }
    }
    @EventHandler
    public void onSwap(@NotNull PlayerSwapHandItemsEvent e) {
        if (e.getOffHandItem().getType().equals(Material.STICK)) {
            e.setCancelled(true);
            PlayerData d = PlayerData.getPlayerData(e.getPlayer());
            Graph g = d.getSelectedGraph();
            if (g != null) {
                d.setSecondGraph(g);
                e.getPlayer().sendMessage(Main.INDEX + "§f두 번째 그래프를 §dy§f=" + g.getUserExpression() + "§f로 설정했습니다.");
            } else {
                e.getPlayer().sendMessage(Main.INDEX + "§c현재 선택된 그래프가 없어 두 번째 그래프를 설정할 수 없습니다.");
            }
        }
    }
    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent e) {
        if (e.getItemDrop().getItemStack().getType().equals(Material.STICK)) {
            e.setCancelled(true);
            PlayerData d = PlayerData.getPlayerData(e.getPlayer());
            Graph g = d.getSecondGraph();
            if (g != null) {
                d.setSecondGraph(null);
                e.getPlayer().sendMessage(Main.INDEX + "두 번째 그래프를 선택 해제했습니다.");
            } else {
                e.getPlayer().sendMessage(Main.INDEX + "§c이미 두 번째 그래프가 선택되어있지 않습니다.");
            }
        }
    }
}
