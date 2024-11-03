package graphmctwo.item;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemFactory {
    private ItemFactory() {

    }

    public static @NotNull ItemStack createItem(Material type, String name, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchant, int amount, boolean isShiny) {
        ItemStack i = new ItemStack(type, amount);
        ItemMeta m = i.getItemMeta();
        m.displayName((Component.text(name, null, TextDecoration.ITALIC.withState(false).decoration())));
        if (lore != null) {
            List<Component> finalLore = new ArrayList<>();
            for (String s : lore) {
                finalLore.add(Component.text(s, null, TextDecoration.ITALIC.withState(false).decoration()));
            }
            m.lore(finalLore);
        }
        if (enchant != null) {
            for (Map.Entry<Enchantment, Integer> entry : enchant.entrySet()) {
                m.addEnchant(entry.getKey(), entry.getValue(), true);
            }
        }
        if (isShiny) {
            m.addEnchant(Enchantment.UNBREAKING, 1, false);
        }
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ARMOR_TRIM, ItemFlag.HIDE_DYE);
        m.setUnbreakable(true);
        i.setItemMeta(m);
        return i;
    }
}
