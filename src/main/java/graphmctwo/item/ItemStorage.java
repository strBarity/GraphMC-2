package graphmctwo.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 플러그인에 이용되는 아이템 상수를 모아놓은 클래스입니다.
 */
public class ItemStorage {

    /**
     * 정적분 막대기 아이템입니다.
     */
    public static final ItemStack DEF_INTEGRAL_WAND = ItemFactory.createItem(Material.STICK, "§d정적분 막대기", Arrays.asList("§a시작점§7(기본값 §dx§f=§b0§7)§a부터 바라보는 곳까지,", "§a선택한 그래프§7(기본값 §dx축§7)§a와 현재 선택한 그래프로", "§a둘러쌓인 곳의 §2넓이§a를 구합니다. §7(0.35초마다 갱신)", " ", "§e[좌클릭] §6- §b그래프를 선택합니다.", "§e[우클릭] §6- §b새로운 정적분 시작점을 설정합니다.", "§8(해당 시작점부터 바라보는 곳까지의 넓이를 구합니다.)", "§e[F] §6- §b현재 선택된 그래프를 두 번째 그래프로 설정합니다.", "§8 (해당 그래프와 현재 선택된 그래프로 둘러쌓인 곳의 넓이를 구합니다.)", "§e[Q] §6- §b두 번째 그래프를 선택 해제합니다."), null, 1, true);

    /**
     * 현재 클래스의 모든 아이템이 있는 리스트로, /items 명령어를 구현하는 데에 사용됩니다.
     * 지금은 아이템이 하나밖에 없으므로 {@link List#of()}를 이용한 단일 리스트를 사용하고 있습니다.
     */
    private static final List<ItemStack> ITEMS = List.of(DEF_INTEGRAL_WAND);

    /**
     * {@link ItemStorage}에 저장된 모든 아이템들이 담긴 리스트를 반환합니다.
     *
     * @return {@link ItemStorage}에 저장된 모든 아이템이 담긴 리스트
     */
    public static @NotNull List<ItemStack> getItems() {
        return ITEMS;
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private ItemStorage() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
