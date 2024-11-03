package graphmctwo.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ItemStorage {
    private ItemStorage() {

    }

    public static final ItemStack DEF_INTEGRAL_WAND = ItemFactory.createItem(Material.STICK, "§d정적분 막대기", Arrays.asList("§a시작점§7(기본값 §dx§f=§b0§7)§a부터 바라보는 곳까지,", "§a선택한 그래프§7(기본값 §dx축§7)§a와 현재 선택한 그래프로", "§a둘러쌓인 곳의 §2넓이§a를 구합니다. §7(0.35초마다 갱신)", " ", "§e[좌클릭] §6- §b그래프를 선택합니다.", "§e[우클릭] §6- §b새로운 정적분 시작점을 설정합니다.", "§8(해당 시작점부터 바라보는 곳까지의 넓이를 구합니다.)",  "§e[F] §6- §b현재 선택된 그래프를 두 번째 그래프로 설정합니다.", "§8 (해당 그래프와 현재 선택된 그래프로 둘러쌓인 곳의 넓이를 구합니다.)", "§e[Q] §6- §b두 번째 그래프를 선택 해제합니다."), null, 1, true);

    private static final List<ItemStack> ITEMS = List.of(DEF_INTEGRAL_WAND);

    public static List<ItemStack> getItems() {
        return ITEMS;
    }
}
