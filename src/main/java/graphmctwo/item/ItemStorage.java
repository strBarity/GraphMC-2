package graphmctwo.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemStorage {
    private ItemStorage() {

    }

    public static final ItemStack DEF_INTEGRAL_WAND = ItemFactory.createItem(Material.STICK, "§d정적분 막대기", null, null, 1, true);

    private static final List<ItemStack> ITEMS = List.of(DEF_INTEGRAL_WAND);

    public static List<ItemStack> getItems() {
        return ITEMS;
    }
}
