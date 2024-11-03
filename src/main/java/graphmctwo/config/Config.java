package graphmctwo.config;

import graphmctwo.main.GraphMC;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * 설정값에 대한 파일 저장을 처리하는 클래스입니다.
 */
public class Config {
    /**
     * 디스크에 저장되는 컨피그 "파일"입니다.
     */
    private File configFile;
    /**
     * <code>configFile</code>로부터 불러온 컨피그입니다.
     */
    private FileConfiguration fileConfiguration;
    /**
     * <code>configFile</code>의 파일 이름입니다.
     */
    private final String fileName;

    /**
     * <code>fileName</code>으로 된 새로운 컨피그 파일을 플러그인 디렉토리(plugins/GraphMC-2)에 생성합니다. {@link Config#getConfig(String)}로 실현됩니다.
     *
     * @param fileName 생성될 컨피그 파일의 이름 (.yml)
     */
    private Config(@NotNull String fileName) {
        this.fileName = fileName;
        loadData();
    }

    /**
     * 컨피그 파일의 데이터를 불러옵니다. 이 과정을 거치지 않으면 컨피그를 불러올 수 없습니다.
     * 따라서 모든 컨피그 파일에 대해 {@link GraphMC#onEnable()}에서 이를 이용해 파일을 불러와야 합니다.
     */
    public void loadData() {
        configFile = new File("plugins/GraphMC-2/" + this.fileName + ".yml");
        fileConfiguration = YamlConfiguration.loadConfiguration(configFile);
        try {
            if (!configFile.exists()) {
                fileConfiguration.save(configFile);
            }
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("컨피그 파일 로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 컨피그 파일의 데이터를 새로고침합니다. 서버 메모리에 저장된 값과 파일에 저장된 값이 일치하지 않는 경우를 방지하기 위해
     * {@link Config#saveData()}에서 데이터를 저장한 직후와 {@link Config#get(String)}에서 데이터를 불러오기 직전 모두에서 이 메소드를 실행하고 있습니다.
     */
    private void reload() {
        try {
            fileConfiguration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage("컨피그 파일 로드 중 오류가 발생했습니다.");
        }
    }

    /**
     * 현재 서버 메모리에 저장된 컨피그 데이터를 컨피그 파일에 저장합니다. 이미 있는 데이터는 덮어쓰기됩니다.
     */
    public void saveData() {
        try {
            fileConfiguration.save(configFile);
            reload();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("컨피그 파일 저장 중 오류가 발생했습니다.");
        }
    }

    /**
     * <code>path</code>에 해당하는 컨피그 값을 불러와 반환합니다. 만약 값이 없다면 <code>null</code>을 반환합니다.
     *
     * @param path 불러올 컨피그의 경로
     * @return <code>path</code>에 저장된 컨피그 값, 만약 저장된 컨피그가 없다면 <code>null</code>을 반환
     */
    public @Nullable Object get(@NotNull String path) {
        reload();
        return fileConfiguration.get(path);
    }

    /**
     * <code>path</code>에 해당하는 컨피그 값을 <code>value</code>로 설정합니다.
     *
     * @param path  저장할 컨피그의 경로
     * @param value 저장할 컨피그 값, <code>null</code>로 설정 시 값을 제거할 수 있음
     */
    public void set(@NotNull String path, @Nullable Object value) {
        fileConfiguration.set(path, value);
        saveData();
    }

    /**
     * <code>fileName</code>으로 된 새로운 컨피그 파일을 플러그인 디렉토리(plugins/GraphMC-2)에 생성합니다.
     *
     * @param fileName 생성될 컨피그 파일 이름
     * @return 생성된 {@link Config}
     */
    public static @NotNull Config getConfig(String fileName) {
        return new Config(fileName);
    }

}
