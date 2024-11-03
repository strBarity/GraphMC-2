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

/**
 * 원하는 커스텀 아이템을 빠르게 생성할 수 있도록 도와주는 클래스입니다.
 */
public class ItemFactory {

    /**
     * 커스텀 아이템을 생성합니다.이 아이템은 기본적으로 Unbreakable(파괴할 수 없음)이며, 기본적인 마인크래프트 설명(인챈트 목록 등)이 보이지 않습니다.
     *
     * @param type    아이템의 종류
     * @param name    아이템의 이름
     * @param lore    아이템의 설명 (한 원소가 한 줄이며 <code>\n</code>을 사용할 수 없음)
     * @param enchant 아이템의 인챈트(들)
     * @param amount  아이템의 개수
     * @param isShiny 아이템이 빛나는 여부
     * @return 완성된 아이템
     */
    public static @NotNull ItemStack createItem(@NotNull Material type, @NotNull String name, @Nullable List<String> lore, @Nullable Map<Enchantment, Integer> enchant, int amount, boolean isShiny) {
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

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private ItemFactory() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
