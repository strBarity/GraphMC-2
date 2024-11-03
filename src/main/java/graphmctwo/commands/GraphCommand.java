package graphmctwo.commands;

import graphmctwo.calculator.FunctionCalculator;
import graphmctwo.calculator.NumberParser;
import graphmctwo.config.Config;
import graphmctwo.graph.FailedToShowGraphException;
import graphmctwo.graph.Graph;
import graphmctwo.graph.GraphHandler;
import graphmctwo.main.GraphMC;
import graphmctwo.player.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * /graph 명령어를 처리하는 클래스입니다.
 */
public class GraphCommand implements TabExecutor {
    /**
     * <code>graphSettingsData.yml</code>에 대한 {@link Config}입니다.
     */
    private static final Config config = Config.getConfig("graphSettingsData");
    /**
     * 올바르지 않은 숫자를 입력받았을 때 출력되는 메시지입니다.
     */
    private static final String NOT_NUMBER = GraphMC.INDEX + "§c올바른 숫자를 입력해주세요.";
    /**
     * 너무 큰 숫자를 입력받았을 때 출력되는 메시지입니다.
     */
    private static final String TOO_LARGE = GraphMC.INDEX + "§c숫자가 너무 큽니다.";
    /**
     * ~입니다로 끝나는 메시지에 대해 오타를 방지하기 위해 추가한 상수입니다.
     */
    private static final String VERB = "§f입니다.";
    /**
     * 좌표를 시각화할 때 x, y, z 사이에 들어가는 쉼표에 대한 상수입니다.
     */
    private static final String COMMA = "§7, §a";
    /**
     * ~(으)로 설정했습니다로 끝나는 메시지에 대해 오타를 방지하기 위해 추가한 상수입니다.
     */
    private static final String SET = "§f(으)로 설정했습니다.";
    /**
     * 아무런 인수 없이 /graph 만 쳤을 때 나오는 메시지에 대한 상수입니다.
     */
    private static final String DEFAULT_MESSAGE = GraphMC.INDEX + "§b-------------------------------------------\n" + GraphMC.INDEX + "§cGraphMC§4-§d2 §6- §e마인크래프트 그래핑 계산기§a by §dBarity_\n" + GraphMC.INDEX + "§7명령어 목록을 보려면 §e/graph help§7를 입력하세요.\n" + GraphMC.INDEX + "§b-------------------------------------------";
    /**
     * /graph help 명령어의 메시지에 대한 상수입니다.
     */
    private static final String HELP_MESSAGE = GraphMC.INDEX + "§6---------------[ §e명령어 목록 §6]------------------\n" + GraphMC.INDEX + "/graph help - 이 창을 띄웁니다.\n" + GraphMC.INDEX + "/graph functions - 그래프의 식에 사용 가능한 함수 리스트를 봅니다.\n" + GraphMC.INDEX + "/graph origin <x> <y> <z> - 그래프의 원점을 지정합니다.\n" + GraphMC.INDEX + "/graph size <굵기> - 그래프의 굵기를 지정합니다.\n" + GraphMC.INDEX + "/graph radius <범위> - 그래프의 범위를 지정합니다.\n" + GraphMC.INDEX + "/graph accuracy <정확도> - 그래프의 정확도를 지정합니다.\n" + GraphMC.INDEX + "/graph add <식> - 해당 식의 그래프를 추가합니다. (y의 대한 식)\n" + GraphMC.INDEX + "/graph remove <식> - 해당 식의 그래프를 제거합니다. (y의 대한 식)\n" + GraphMC.INDEX + "/graph toggle <식> - 해당 식의 그래프가 보이는 여부를 키거나 끕니다.\n" + GraphMC.INDEX + "§6-------------------------------------------";
    /**
     * /graph functions 명령어에 대한 상수입니다.
     */
    private static final String FUNCTIONS_MESSAGE = GraphMC.INDEX + "§d----------------[ §5함수 목록 §d]------------------\n" + GraphMC.INDEX + "§5e§f - 자연로그의 밑 §7(2.7182818284590452354...)\n" + GraphMC.INDEX + "§5pi§f, §5π§f - 원주율 §7(3.14159265358979323846...)\n" + GraphMC.INDEX + "§4abs§b(§dx§b)§f, §4|§dx§4|§f - 절댓값\n" + GraphMC.INDEX + "§aasin§b(§dx§b)§f, §aacos§b(§dx§b)§f, §aatan§b(§dx§b) §f- 아크 삼각함수 (역삼각함수)\n" + GraphMC.INDEX + "§2sinh§b(§dx§b), §2cosh§b(§dx§b), §2tanh§b(§dx§b) §f- 쌍곡그래프 함수\n" + GraphMC.INDEX + "§esin§b(§dx§b), §ecos§b(§dx§b), §etan§b(§dx§b) §f- 삼각함수\n" + GraphMC.INDEX + "§3exp§b(§dx§b) §f- EXP 함수 §b(§5e§b^§dx§b)\n" + GraphMC.INDEX + "§6log§b(§dx§b)§f - 로그 함수 (상용로그: 밑이 10), §6ln§b(§dx§b)§f - 로그 함수 (자연로그: 밑이 §5e)\n" + GraphMC.INDEX + "§csqrt§b(§dx§b), §croot§b(§dx§b), §c§o√§b(§dx§b)§f - 제곱근 (루트)\n" + GraphMC.INDEX + "§dx§b!§f - 팩토리얼, §9§lΓ§b(§dx§b)§f - 감마 함수 (=§b(§dx§b-1)§b!§f)\n" + GraphMC.INDEX + "§7(음수의 제곱근과 로그는 무시되어 값이 반환되지 않음)\n" + GraphMC.INDEX + "§d-------------------------------------------";
    /**
     * 문자열 <code>help</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String HELP = "help";
    /**
     * 문자열 <code>functions/code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String FUNCTIONS = "functions";
    /**
     * 문자열 <code>size</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String SIZE = "size";
    /**
     * 문자열 <code>origin</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String ORIGIN = "origin";
    /**
     * 문자열 <code>radius</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String RADIUS = "radius";
    /**
     * 문자열 <code>accuracy</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String ACCURACY = "accuracy";
    /**
     * 문자열 <code>add</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String ADD = "add";
    /**
     * 문자열 <code>remove</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String REMOVE = "remove";
    /**
     * 문자열 <code>toggle</code>에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String TOGGLE = "toggle";
    /**
     * 그래프 수식 앞에 붙는 접미사에 대한 상수로, 오타를 방지하기 위함입니다.
     */
    private static final String GRAPH = "그래프 §dy§f=";

    /**
     * /graph 명령어를 입력받았을 때에 대해 처리하는 메소드입니다. 모든 첫 번째 인수에 대한 메소드가 나누어져 있습니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param command       실행한 커맨드
     * @param s             실행한 커맨드의 이름
     * @param strings       실행한 커맨드의 인수들
     * @return 정상적으로 커맨드가 작동한 경우 <code>true</code>, 아닌 경우 <code>false</code>
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (s.equals("graph")) {
            if (strings.length < 1) {
                commandSender.sendMessage(DEFAULT_MESSAGE);
                return false;
            }
            switch (strings[0]) {
                case HELP -> commandSender.sendMessage(HELP_MESSAGE);
                case FUNCTIONS -> commandSender.sendMessage(FUNCTIONS_MESSAGE);
                case SIZE -> sizeCommand(commandSender, strings);
                case ORIGIN -> originCommand(commandSender, strings);
                case RADIUS -> radiusCommand(commandSender, strings);
                case ACCURACY -> accuracyCommand(commandSender, strings);
                case ADD -> addCommand(commandSender, strings);
                case REMOVE -> removeCommand(commandSender, strings);
                case TOGGLE -> toggleCommand(commandSender, strings);
                default -> commandSender.sendMessage(GraphMC.INDEX + "§c알 수 없는 명령어입니다: /graph " + strings[0]);
            }
            return true;
        }
        return false;
    }

    /**
     * 그래프의 선 굵기에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void sizeCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotFloat(strings[1])) {
            commandSender.sendMessage(GraphMC.INDEX + "현재 그래프의 굵기는 §e" + GraphHandler.getGraphSize() + VERB);
            return;
        } else if (Double.parseDouble(strings[1]) > 10) {
            commandSender.sendMessage(TOO_LARGE);
            return;
        }
        GraphHandler.setGraphSize(Float.parseFloat(strings[1]));
        commandSender.sendMessage(GraphMC.INDEX + "그래프의 굵기를 §e" + GraphHandler.getGraphSize() + SET);
        config.set(SIZE, Float.parseFloat(strings[1]));
        refreshAllActiveGraphs();
    }

    /**
     * 그래프의 원점에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void originCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1) {
            commandSender.sendMessage(GraphMC.INDEX + "현재 그래프의 원점은 §e(§a" + GraphHandler.getGraphOrigin().getWorld().getName() + COMMA + GraphHandler.getGraphOrigin().getX() + COMMA + GraphHandler.getGraphOrigin().getY() + COMMA + GraphHandler.getGraphOrigin().getZ() + "§e) 입니다.");
            return;
        } else if (strings.length < 4) {
            commandSender.sendMessage(GraphMC.INDEX + "§c좌표가 올바르지 않습니다.");
            return;
        } else if (NumberParser.isNotDouble(strings[1]) || NumberParser.isNotDouble(strings[2]) || NumberParser.isNotDouble(strings[3])) {
            commandSender.sendMessage(NOT_NUMBER);
            return;
        }
        GraphHandler.setGraphOrigin(new Location(GraphHandler.getGraphOrigin().getWorld(), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3])));
        commandSender.sendMessage(GraphMC.INDEX + "그래프의 원점을 §e(§a" + GraphHandler.getGraphOrigin().getWorld().getName() + COMMA + GraphHandler.getGraphOrigin().getX() + COMMA + GraphHandler.getGraphOrigin().getY() + COMMA + GraphHandler.getGraphOrigin().getZ() + "§e) §f(으)로 설정했습니다.");
        config.set(ORIGIN, GraphHandler.getGraphOrigin().getWorld().getName() + "," + GraphHandler.getGraphOrigin().getX() + "," + GraphHandler.getGraphOrigin().getY() + "," + GraphHandler.getGraphOrigin().getZ());
        refreshAllActiveGraphs();
    }

    /**
     * 그래프의 범위(반경)에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void radiusCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotDouble(strings[1])) {
            commandSender.sendMessage(GraphMC.INDEX + "현재 그래프의 반경은 §e" + GraphHandler.getGraphRadius() + VERB);
            return;
        } else if (Double.parseDouble(strings[1]) > 100) {
            commandSender.sendMessage(TOO_LARGE);
            return;
        }
        GraphHandler.setGraphRadius(Double.parseDouble(strings[1]));
        commandSender.sendMessage(GraphMC.INDEX + "그래프의 반경을 §e" + GraphHandler.getGraphRadius() + SET);
        config.set(RADIUS, Double.parseDouble(strings[1]));
        refreshAllActiveGraphs();
    }

    /**
     * 그래프의 정확도, 즉 한 블럭당 입자 수에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void accuracyCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotDouble(strings[1])) {
            commandSender.sendMessage(GraphMC.INDEX + "현재 그래프의 정확도는 §e" + GraphHandler.getGraphAccuracy() + VERB);
            return;
        }
        GraphHandler.setGraphAccuracy(Double.parseDouble(strings[1]));
        commandSender.sendMessage(GraphMC.INDEX + "그래프의 정확도를 §e" + GraphHandler.getGraphAccuracy() + SET);
        config.set(ACCURACY, Double.parseDouble(strings[1]));
        refreshAllActiveGraphs();
    }

    /**
     * 그래프를 추가에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void addCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1) {
            commandSender.sendMessage(GraphMC.INDEX + "§c그래프의 식을 입력해주세요.");
            return;
        }
        try {
            FunctionCalculator.evaluate(strings[1].replace("x", "(0)"));
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(GraphMC.INDEX + "§c그래프의 식이 올바르지 않습니다 - " + e.getCause().getMessage());
            return;
        }
        if (GraphHandler.getGraph(strings[1]) != null) {
            commandSender.sendMessage(GraphMC.INDEX + "§c해당 식의 그래프가 이미 존재합니다.");
            return;
        }
        commandSender.sendMessage(GraphMC.INDEX + GRAPH + GraphHandler.addGraph(strings[1]).getDisplayExpression() + "§f를 추가했습니다.");
    }

    /**
     * 그래프 제거에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void removeCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (isCallingInvalidGraph(strings)) {
            commandSender.sendMessage(GraphMC.INDEX + "§c해당 그래프를 찾을 수 없습니다.");
            return;
        }
        Graph graph = GraphHandler.getGraph(strings[1]);
        if (Objects.requireNonNull(graph).isGraphVisible()) {
            graph.toggle();
        }
        GraphHandler.removeGraph(Objects.requireNonNull(graph).getExpression());
        commandSender.sendMessage(GraphMC.INDEX + GRAPH + graph.getDisplayExpression() + "§f를 제거했습니다.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerData.getPlayerData(player).getSelectedGraph() == graph) {
                PlayerData.getPlayerData(player).setSelectedGraph(null);
                player.sendMessage(GraphMC.INDEX + "§c선택된 그래프가 제거되어 자동으로 선택이 해제되었습니다.");
            }
            if (PlayerData.getPlayerData(player).getSecondGraph() == graph) {
                PlayerData.getPlayerData(player).setSecondGraph(null);
                player.sendMessage(GraphMC.INDEX + "§c선택된 두 번째 그래프가 제거되어 자동으로 선택이 해제되었습니다.");
            }
        }
    }

    /**
     * 그래프를 끼거나 켜는 것에 대한 커맨드를 처리하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param strings       실행한 커맨드의 인수들
     */
    private static void toggleCommand(@NotNull CommandSender commandSender, String @NotNull [] strings) {
        if (isCallingInvalidGraph(strings)) {
            commandSender.sendMessage(GraphMC.INDEX + "§c해당 그래프를 찾을 수 없습니다.");
            return;
        }
        Graph graph = GraphHandler.getGraph(strings[1]);
        String message;
        try {
            if (Objects.requireNonNull(graph).toggle()) {
                message = "§f를 표시합니다.";
                if (commandSender instanceof Player p) {
                    p.showTitle(Title.title(Component.text(""), Component.text("§dy§f=" + graph.getDisplayExpression() + " §d표시 중"), Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))));
                }
            } else {
                message = "§f를 더 이상 표시하지 않습니다.";
            }
            commandSender.sendMessage(GraphMC.INDEX + GRAPH + graph.getDisplayExpression() + message);
        } catch (FailedToShowGraphException e) {
            e.printStackTrace();
            commandSender.sendMessage(GraphMC.INDEX + "§c해당 그래프를 표시하는 도중 예상치 못한 오류가 발생했습니다. 서버 로그에서 오류가 난 부분을 개발자에게 제보해주세요.");
        }
    }

    /**
     * 해당 커맨드의 인수가 존재하는 그래프를 가리키고 있는 지를 판단하는 메소드입니다.
     *
     * @param strings 실행한 커맨드의 인수들
     * @return 인수가 비었거나 인수가 가리키는 그래프가 없다면 <code>false</code>, 인수가 올바른 그래프를 가리키고 있다면 <code>true</code>
     */
    private static boolean isCallingInvalidGraph(String @NotNull [] strings) {
        return strings.length <= 1 || GraphHandler.getGraph(strings[1]) == null;
    }

    /**
     * 현재 켜져 있는 모든 그래프를 새로고침합니다. 이는 주로 그래프 설정이 변경되었을 때에 호출됩니다.
     */
    private static void refreshAllActiveGraphs() {
        for (Graph graph : GraphHandler.getGraphs()) {
            if (graph.isGraphVisible()) {
                graph.toggle();
                Bukkit.broadcast(Component.text(GraphMC.INDEX + "§7그래프 설정이 변경되어 활성화된 모든 그래프를 새로고침하는 중입니다..."));
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(GraphMC.class), () -> {
                    Graph g = GraphHandler.getGraph(graph.getExpression());
                    if (g != null) g.toggle();
                    Bukkit.broadcast(Component.text(GraphMC.INDEX + "§a새로고침이 완료되었습니다."));
                }, 5L);
            }
        }
    }

    /**
     * /graph 커맨드에 대한 자동완성을 구현하는 메소드입니다.
     *
     * @param commandSender 커맨드를 실행한 주체
     * @param command       실행한 커맨드
     * @param s             실행한 커맨드의 이름
     * @param strings       실행한 커맨드의 인수들
     * @return 자동완성 후보가 담긴 리스트
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (s.equals("graph") && strings.length == 1) {
            return Arrays.asList(ORIGIN, RADIUS, ACCURACY, SIZE, ADD, REMOVE, TOGGLE, HELP, FUNCTIONS);
        } else if (strings.length == 2 && Arrays.asList(REMOVE, TOGGLE).contains(strings[0])) {
            return GraphHandler.getGraphs().stream().map(Graph::getExpression).toList();
        }
        return List.of();
    }
}
