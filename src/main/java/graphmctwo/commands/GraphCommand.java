package graphmctwo.commands;

import graphmctwo.calculator.FunctionCalculator;
import graphmctwo.calculator.NumberParser;
import graphmctwo.config.Config;
import graphmctwo.graph.Graph;
import graphmctwo.graph.GraphHandler;
import graphmctwo.main.Main;
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

public class GraphCommand implements TabExecutor {
    private static final Config config = Config.getConfig("graphSettingsData");
    private static final String NOT_NUMBER = Main.INDEX + "§c올바른 숫자를 입력해주세요.";
    private static final String TOO_LARGE = Main.INDEX + "§c숫자가 너무 큽니다.";
    private static final String VERB = "§f입니다.";
    private static final String COMMA = "§7, §a";
    private static final String SET = "§f(으)로 설정했습니다.";
    private static final String DEFAULT_MESSAGE = Main.INDEX + "§b-------------------------------------------\n"
                                                  + Main.INDEX + "§cGraphMC §6- §e마인크래프트 그래핑 계산기§a by §dBarity_\n"
                                                  + Main.INDEX + "§7명령어 목록을 보려면 §e/graph help§7를 입력하세요.\n"
                                                  + Main.INDEX + "§b-------------------------------------------";
    private static final String HELP_MESSAGE = Main.INDEX + "§6---------------[ §e명령어 목록 §6]------------------\n"
                                               + Main.INDEX + "/graph help - 이 창을 띄웁니다.\n"
                                               + Main.INDEX + "/graph functions - 그래프의 식에 사용 가능한 함수 리스트를 봅니다.\n"
                                               + Main.INDEX + "/graph origin <x> <y> <z> - 그래프의 원점을 지정합니다.\n"
                                               + Main.INDEX + "/graph size <굵기> - 그래프의 굵기를 지정합니다.\n"
                                               + Main.INDEX + "/graph radius <범위> - 그래프의 범위를 지정합니다.\n"
                                               + Main.INDEX + "/graph accuracy <정확도> - 그래프의 정확도를 지정합니다.\n"
                                               + Main.INDEX + "/graph expression <식> - 그래프의 식을 지정합니다. (y의 대한 식)\n"
                                               + Main.INDEX + "/graph toggle - 그래프의 보이는 여부를 키거나 끕니다.\n"
                                               + Main.INDEX + "§6-------------------------------------------";
    private static final String FUNCTIONS_MESSAGE = Main.INDEX + "§d----------------[ §5함수 목록 §d]------------------\n"
                                                    + Main.INDEX + "§5e§f - 자연로그의 밑 §7(2.7182818284590452354...)\n"
                                                    + Main.INDEX + "§5pi§f, §5π§f - 원주율 §7(3.14159265358979323846...)\n"
                                                    + Main.INDEX + "§4abs§b(§dx§b)§f, §4|§dx§4|§f - 절댓값\n"
                                                    + Main.INDEX + "§aasin§b(§dx§b)§f, §aacos§b(§dx§b)§f, §aatan§b(§dx§b) §f- 아크 삼각함수 (역삼각함수)\n"
                                                    + Main.INDEX + "§2sinh§b(§dx§b), §2cosh§b(§dx§b), §2tanh§b(§dx§b) §f- 쌍곡그래프 함수\n"
                                                    + Main.INDEX + "§esin§b(§dx§b), §ecos§b(§dx§b), §etan§b(§dx§b) §f- 삼각함수\n"
                                                    + Main.INDEX + "§3exp§b(§dx§b) §f- EXP 함수 §b(§5e§b^§dx§b)\n"
                                                    + Main.INDEX + "§6log§b(§dx§b)§f - 로그 함수 (자연로그)\n"
                                                    + Main.INDEX + "§csqrt§b(§dx§b), §croot§b(§dx§b), §c§o√§b(§dx§b)§f - 제곱근 (루트)\n"
                                                    + Main.INDEX + "§7(음수의 제곱근과 로그는 무시되어 값이 반환되지 않음)\n"
                                                    + Main.INDEX + "§d-------------------------------------------";
    private static final String HELP = "help";
    private static final String FUNCTIONS = "functions";
    private static final String SIZE = "size";
    private static final String ORIGIN = "origin";
    private static final String RADIUS = "radius";
    private static final String ACCURACY = "accuracy";
    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String TOGGLE = "toggle";
    private static final String GRAPH = "그래프 §dy§f=";

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
                default -> commandSender.sendMessage(Main.INDEX + "§c알 수 없는 명령어입니다: /graph " + strings[0]);
            }
            return false;
        }
        return true;
    }

    private static void sizeCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotFloat(strings[1])) {
            commandSender.sendMessage(Main.INDEX + "현재 그래프의 굵기는 §e" + GraphHandler.getGraphSize() + VERB);
            return;
        } else if (Double.parseDouble(strings[1]) > 10) {
            commandSender.sendMessage(TOO_LARGE);
            return;
        }
        GraphHandler.setGraphSize(Float.parseFloat(strings[1]));
        commandSender.sendMessage(Main.INDEX + "그래프의 굵기를 §e" + GraphHandler.getGraphSize() + SET);
        config.set(SIZE, Float.parseFloat(strings[1]));
        refreshAllActiveGraphs();
    }

    private static void originCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1) {
            commandSender.sendMessage(Main.INDEX + "현재 그래프의 원점은 §e(§a" + GraphHandler.getGraphOrigin().getWorld().getName() + COMMA + GraphHandler.getGraphOrigin().getX() + COMMA + GraphHandler.getGraphOrigin().getY() + COMMA + GraphHandler.getGraphOrigin().getZ() + "§e) 입니다.");
            return;
        } else if (strings.length < 4) {
            commandSender.sendMessage(Main.INDEX + "§c좌표가 올바르지 않습니다.");
            return;
        } else if (NumberParser.isNotDouble(strings[1]) || NumberParser.isNotDouble(strings[2]) || NumberParser.isNotDouble(strings[3])) {
            commandSender.sendMessage(NOT_NUMBER);
            return;
        }
        GraphHandler.setGraphOrigin(new Location(GraphHandler.getGraphOrigin().getWorld(), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]), Double.parseDouble(strings[3])));
        commandSender.sendMessage(Main.INDEX + "그래프의 원점을 §e(§a" + GraphHandler.getGraphOrigin().getWorld().getName() + COMMA + GraphHandler.getGraphOrigin().getX() + COMMA + GraphHandler.getGraphOrigin().getY() + COMMA + GraphHandler.getGraphOrigin().getZ() + "§e) §f(으)로 설정했습니다.");
        config.set(ORIGIN, GraphHandler.getGraphOrigin().getWorld().getName() + "," + GraphHandler.getGraphOrigin().getX() + "," + GraphHandler.getGraphOrigin().getY() + "," + GraphHandler.getGraphOrigin().getZ());
        refreshAllActiveGraphs();
    }

    private static void radiusCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotDouble(strings[1])) {
            commandSender.sendMessage(Main.INDEX + "현재 그래프의 반경은 §e" + GraphHandler.getGraphRadius() + VERB);
            return;
        } else if (Double.parseDouble(strings[1]) > 100) {
            commandSender.sendMessage(TOO_LARGE);
            return;
        }
        GraphHandler.setGraphRadius(Double.parseDouble(strings[1]));
        commandSender.sendMessage(Main.INDEX + "그래프의 반경을 §e" + GraphHandler.getGraphRadius() + SET);
        config.set(RADIUS, Double.parseDouble(strings[1]));
        refreshAllActiveGraphs();
    }

    private static void accuracyCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1 || NumberParser.isNotDouble(strings[1])) {
            commandSender.sendMessage(Main.INDEX + "현재 그래프의 정확도는 §e" + GraphHandler.getGraphAccuracy() + VERB);
            return;
        }
        GraphHandler.setGraphAccuracy(Double.parseDouble(strings[1]));
        commandSender.sendMessage(Main.INDEX + "그래프의 정확도를 §e" + GraphHandler.getGraphAccuracy() + SET);
        config.set(ACCURACY, Double.parseDouble(strings[1]));
        refreshAllActiveGraphs();
    }

    private static void addCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (strings.length == 1) {
            commandSender.sendMessage(Main.INDEX + "§c그래프의 식을 입력해주세요.");
            return;
        }
        try {
            FunctionCalculator.evaluate(strings[1].replace("x", "(0)"));
        } catch (IllegalArgumentException e) {
            commandSender.sendMessage(Main.INDEX + "§c그래프의 식이 올바르지 않습니다 - " + e.getCause().getMessage());
            return;
        }
        for (Graph graph : GraphHandler.getGraphs()) {
            if (graph.getUserExpression().equals(new Graph(strings[1]).getUserExpression())) {
                commandSender.sendMessage(Main.INDEX + "§c해당 식의 그래프가 이미 존재합니다.");
                return;
            }
        }
        commandSender.sendMessage(Main.INDEX + GRAPH + GraphHandler.addGraph(strings[1]).getUserExpression() + "§f를 추가했습니다.");
    }

    private static void removeCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (isCallingInvalidGraph(strings)) {
            commandSender.sendMessage(Main.INDEX + "§c해당 그래프를 찾을 수 없습니다.");
            return;
        }
        Graph graph = GraphHandler.getGraph(strings[1]);
        if (Objects.requireNonNull(graph).isGraphVisible()) {
            graph.toggle();
        }
        GraphHandler.removeGraph(Objects.requireNonNull(graph).getExpression());
        commandSender.sendMessage(Main.INDEX + GRAPH + graph.getUserExpression() + "§f를 제거했습니다.");
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerData.getPlayerData(player).getSelectedGraph() == graph) {
                PlayerData.getPlayerData(player).setSelectedGraph(null);
                player.sendMessage(Main.INDEX + "§c선택된 그래프가 제거되어 자동으로 선택이 해제되었습니다.");
            }
            if (PlayerData.getPlayerData(player).getSecondGraph() == graph) {
                PlayerData.getPlayerData(player).setSecondGraph(null);
                player.sendMessage(Main.INDEX + "§c선택된 두 번째 그래프가 제거되어 자동으로 선택이 해제되었습니다.");
            }
        }
    }

    private static void toggleCommand(CommandSender commandSender, String @NotNull [] strings) {
        if (isCallingInvalidGraph(strings)) {
            commandSender.sendMessage(Main.INDEX + "§c해당 그래프를 찾을 수 없습니다.");
            return;
        }
        Graph graph = GraphHandler.getGraph(strings[1]);
        String message;
        if (Objects.requireNonNull(graph).toggle()) {
            message = "§f를 표시합니다.";
            if (commandSender instanceof Player p) {
                p.showTitle(Title.title(Component.text(""), Component.text("§dy§f=" + graph.getUserExpression() + " §d표시 중"), Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))));
            }
        } else {
            message = "§f를 더 이상 표시하지 않습니다.";
        }
        commandSender.sendMessage(Main.INDEX + GRAPH + graph.getUserExpression() + message);
    }

    private static boolean isCallingInvalidGraph(String @NotNull [] strings) {
        return strings.length <= 1 || GraphHandler.getGraph(strings[1]) == null;
    }

    private static void refreshAllActiveGraphs() {
        for (Graph graph : GraphHandler.getGraphs()) {
            if (graph.isGraphVisible()) {
                graph.toggle();
                Bukkit.broadcast(Component.text(Main.INDEX + "§7그래프 설정이 변경되어 활성화된 모든 그래프를 새로고침하는 중입니다..."));
                Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class), () -> {
                    for (Graph g : GraphHandler.getGraphs()) {
                        if (g.getExpression().equals(graph.getExpression())) {
                            g.toggle();
                        }
                    }
                    Bukkit.broadcast(Component.text(Main.INDEX + "§a새로고침이 완료되었습니다."));
                }, 5L);
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command
            command, @NotNull String s, @NotNull String[] strings) {
        if (s.equals("graph") && strings.length == 1) {
            return Arrays.asList(ORIGIN, RADIUS, ACCURACY, SIZE, ADD, REMOVE, TOGGLE, HELP, FUNCTIONS);
        } else if (strings.length == 2 && Arrays.asList(REMOVE, TOGGLE).contains(strings[0])) {
            return GraphHandler.getGraphs().stream().map(Graph::getExpression).toList();
        }
        return List.of();
    }
}
