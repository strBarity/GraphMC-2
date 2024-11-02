package graphmctwo.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class Config {
    private File configFile;
    private FileConfiguration fileConfiguration;
    private final String fileName;

    private Config(String fileName) {
        this.fileName = fileName;
        loadData();
    }

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

    private void reload() {
        try {
            fileConfiguration.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getConsoleSender().sendMessage("컨피그 파일 로드 중 오류가 발생했습니다.");
        }
    }

    public void saveData() {
        try {
            fileConfiguration.save(configFile);
            reload();
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage("컨피그 파일 저장 중 오류가 발생했습니다.");
        }
    }

    public Object get(String path) {
        reload();
        return fileConfiguration.get(path);
    }

    public void set(String path, Object value) {
        fileConfiguration.set(path, value);
        saveData();
    }

    public static @NotNull Config getConfig(String fileName) {
        return new Config(fileName);
    }

}
