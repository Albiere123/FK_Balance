package br.ufpb.dcx.lima.albiere.fK_Money.configs;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class Functions {

    private final FileConfiguration file;

    public Functions(FileConfiguration file) {
        this.file = file;
    }

    public String getColouredString(String path) {
        return Objects.requireNonNull(file.getString(path)).replaceAll("&", "§");
    }

    public FileConfiguration getConfig() {
        return file;
    }
}