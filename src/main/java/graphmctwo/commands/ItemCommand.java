package graphmctwo.commands;

import graphmctwo.item.ItemStorage;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * /item 명령어를 처리하는 클래스입니다.
 */
public class ItemCommand implements TabExecutor {
    /**
     * /item 명령어를 입력받았을 때에 대해 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param command       실행한 커맨드
     * @param s             실행한 커맨드의 이름
     * @param strings       실행한 커맨드의 인수들
     * @return 정상적으로 커맨드가 작동한 경우 <code>true</code>, 아닌 경우 <code>false</code>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            Inventory inv = Bukkit.createInventory(null, 54, Component.text("Items"));
            ItemStorage.getItems().forEach(inv::addItem);
            p.openInventory(inv);
            return true;
        }
        return false;
    }

    /**
     * /item 명령어에 대해 자동완성을 구현하는 메소드입니다. 이 명령어는 인수 없이 작동하기 때문에 항상 빈 리스트를 반환합니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param command       실행한 커맨드
     * @param s             실행한 커맨드의 이름
     * @param strings       실행한 커맨드의 인수들
     * @return 자동완성 후보가 담긴 리스트
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return List.of();
    }
}
