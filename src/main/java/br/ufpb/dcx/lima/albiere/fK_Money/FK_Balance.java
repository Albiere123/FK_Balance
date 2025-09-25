package br.ufpb.dcx.lima.albiere.fK_Money;

import br.ufpb.dcx.lima.albiere.fK_Money.configs.Config;
import br.ufpb.dcx.lima.albiere.fK_Money.configs.Functions;
import br.ufpb.dcx.lima.albiere.fK_Money.events.IO;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager.Manager;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager.ManagerInterface;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.events.InventoryClickPaginedEvent;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.manager.InventoryManager;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.manager.InventoryTypes;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.InventoryModuleInterface;


public final class FK_Balance extends JavaPlugin {

    private static Config configM;
    private static FileConfiguration options;
    private static Manager economyManager;
    private static FileConfiguration moneyOptions;
    private static Functions op;
    private static InventoryManager inventoryManager;

    @Override
    public void onEnable() {
        loadConfig();
        verifyArchives();
        loadEconomy();
        loadCommands();
        loadEvents();
        startLoop();
        registerAPI();
        System.out.println(op.getColouredString("essential.prefix") + "Plugin Iniciado!");

    }

    @Override
    public void onDisable() {
        op = new Functions(options);
        System.out.println(op.getColouredString("essential.prefix") + "Plugin encerrado.");
        getEconomyManager().saveAllPlayersBalance();
    }

    public void verifyArchives() {
        op = new Functions(options);
        if (!options.contains("essential.prefix")) {
            options.set("essential.prefix", "&e[ &aFK_Balance &e] &f");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.Title")) {
            options.set("essential.moneyTop.Title", "&6&l          Money TOP");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.Name")) {
            options.set("essential.moneyTop.Name", "&e{Rank}. {Player}");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.Lore")) {
            List<String> lore = (new ArrayList<>());
            lore.add("&aR$ {Money}");
            options.set("essential.moneyTop.Lore", lore);
            configM.saveConfig("options");
        }
        if (!options.contains("sql.ip")) {
            options.set("sql.ip", "localhost");
            configM.saveConfig("options");
        }
        if (!options.contains("sql.database")) {
            options.set("sql.database", "fk_balance");
            configM.saveConfig("options");
        }
        if (!options.contains("sql.table")) {
            options.set("sql.table", "coins");
            configM.saveConfig("options");
        }
        if (!options.contains("sql.user")) {
            options.set("sql.user", "root");
            configM.saveConfig("options");
        }
        if (!options.contains("sql.password")) {
            options.set("sql.password", "");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.item_nextAndPrevious.Type")) {
            options.set("essential.moneyTop.item_nextAndPrevious.Type", "ARROW");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.item_nextAndPrevious.PreviousName")) {
            options.set("essential.moneyTop.item_nextAndPrevious.PreviousName", "Página Anterior");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.item_nextAndPrevious.PreviousLore")) {
            options.set("essential.moneyTop.item_nextAndPrevious.PreviousLore", List.of(""));
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.item_nextAndPrevious.NextName")) {
            options.set("essential.moneyTop.item_nextAndPrevious.NextName", "Próxima Página");
            configM.saveConfig("options");
        }
        if (!options.contains("essential.moneyTop.item_nextAndPrevious.NextLore")) {
            options.set("essential.moneyTop.item_nextAndPrevious.NextLore", List.of(""));
            configM.saveConfig("options");
        }
    }

    public static Config getConfigM() {
        return configM;
    }

    public static FileConfiguration getMoneyOptions() {
        return moneyOptions;
    }

    public static Functions getOptions() {
        return op;
    }

    public static Manager getEconomyManager() {
        return economyManager;
    }

    public void loadConfig() {
        configM = new Config(this);
        configM.loadConfig("options");
        configM.loadConfig("balanceOptions");
        options = configM.getConfig("options");
    }

    public void loadEconomy() {
        moneyOptions = configM.getConfig("balanceOptions");
        economyManager = new Manager();
        economyManager.loadAllPlayersBalance();
    }

    public static FK_Balance getPls() {
        return getPlugin(FK_Balance.class);
    }


    public void loadEvents() {
        getServer().getPluginManager().registerEvents(new IO(), this);
        getServer().getPluginManager().registerEvents(new InventoryClickPaginedEvent(), this);
    }

    public void startLoop() {
        long autosaveInterval = 20L * 60L * 10L;

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            getLogger().info("Salvando dados da economia automaticamente...");
            getEconomyManager().saveAllPlayersBalance();
            getLogger().info("Dados salvos com sucesso!");

        }, autosaveInterval, autosaveInterval);
    }

    public void registerAPI() {
        getServer().getServicesManager().register(
                ManagerInterface.class,
                economyManager,
                this,
                ServicePriority.Normal
        );
    }

    public void loadCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        inventoryManager = new InventoryManager();

        String commandPackage = "br.ufpb.dcx.lima.albiere.fK_Money.commands";

        Reflections reflections = new Reflections(commandPackage);
        Set<Class<? extends BaseCommand>> allCommands = reflections.getSubTypesOf(BaseCommand.class);

        getLogger().info("Registrando " + allCommands.size() + " comandos...");
        for (Class<? extends BaseCommand> commandClass : allCommands) {
            try {
                BaseCommand commandInstance = commandClass.getDeclaredConstructor().newInstance();
                commandManager.registerCommand(commandInstance);
                getLogger().info("Comando '" + commandClass.getSimpleName() + "' registrado com sucesso!");
            } catch (Exception e) {
                getLogger().severe("Não foi possível registrar o comando " + commandClass.getSimpleName());
                e.printStackTrace();
            }
        }
    }

    public static InventoryManager getManager() {
        return inventoryManager;
    }

    public static InventoryModuleInterface loadTopMoney(int index) {
        InventoryModuleInterface inventory = inventoryManager.createInventory(
                "topMoney",
                Objects.requireNonNull(options.getString("essential.moneyTop.Title")),
                5,
                InventoryTypes.PAGED
        );
        if(inventory == null) return null;
        List<OfflinePlayer> rank = economyManager.getTopMoney();

        List<String> nextLore = options.getStringList("essential.moneyTop.item_nextAndPrevious.NextLore")
                .stream()
                .map(s -> s.replaceAll("&", "§"))
                .collect(Collectors.toList());

        List<String> lore1 = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.PreviousLore");
        lore1.replaceAll(s -> s.replaceAll("&", "§"));

        if (index == 0) {
            inventory.addItem(26,
                    Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")),
                    1,
                    Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.NextName")).replaceAll("&", "§"),
                    nextLore);
        } else {
            inventory.addItem(18, Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")), 1, Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.PreviousName")).replaceAll("&", "§"), lore1);
        }
        if(index > rank.size()) return null;
        for (int i = index; i < rank.size(); i++) {
            OfflinePlayer rankedPlayer = rank.get(i);
            if(rankedPlayer == null) return null;
            String name = Objects.requireNonNull(options.getString("essential.moneyTop.Name"))
                    .replaceAll("\\{Rank}", String.valueOf(i + 1))
                    .replaceAll("\\{Player}", Objects.requireNonNull(rankedPlayer.getName()));

            List<String> finalLore = options.getStringList("essential.moneyTop.Lore")
                    .stream()
                    .map(s -> s.replaceAll("\\{Money}", String.valueOf(economyManager.getPlayerBalance(rankedPlayer.getUniqueId()).getMoney())))
                    .collect(Collectors.toList());

            inventory.addPlayerHead((index==1 ?i-14 : i) <= 25 ? (index==1 ?i-14 : i)  + 19 : (index==1 ?i-14 : i) + 21, 1, name, finalLore, rankedPlayer.getUniqueId());
        }

        return inventory;
    }


    public static InventoryModuleInterface loadTopCash(int index) {
        InventoryModuleInterface inventory = inventoryManager.createInventory(
                "topCash",
                Objects.requireNonNull(options.getString("essential.cashTop.Title")),
                5,
                InventoryTypes.PAGED
        );

        List<OfflinePlayer> rank = economyManager.getTopCash();

        List<String> nextLore = FK_Balance.getOptions().getConfig().getStringList("essential.cashTop.item_nextAndPrevious.NextLore")
                .stream()
                .map(s -> s.replaceAll("&", "§"))
                .collect(Collectors.toList());

        List<String> lore1 = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.PreviousLore");
        lore1.replaceAll(s -> s.replaceAll("&", "§"));

        if (index == 0) {
            inventory.addItem(26,
                    Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.cashTop.item_nextAndPrevious.Type")),
                    1,
                    Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.cashTop.item_nextAndPrevious.NextName")).replaceAll("&", "§"),
                    nextLore);
        } else {
            inventory.addItem(18, Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")), 1, Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.PreviousName")).replaceAll("&", "§"), lore1);
        }

        if(index > rank.size()) return null;
        for (int i = index; i < rank.size(); i++) {
            OfflinePlayer rankedPlayer = rank.get(i);
            if(rankedPlayer == null) return null;
            String name = Objects.requireNonNull(options.getString("essential.cashTop.Name"))
                    .replaceAll("\\{Rank}", String.valueOf(i + 1))
                    .replaceAll("\\{Player}", Objects.requireNonNull(rankedPlayer.getName()));

            List<String> finalLore = options.getStringList("essential.cashTop.Lore")
                    .stream()
                    .map(s -> s.replaceAll("\\{Money}", String.valueOf(economyManager.getPlayerBalance(rankedPlayer.getUniqueId()).getCash())))
                    .collect(Collectors.toList());

            inventory.addPlayerHead((index==1 ?i-14 : i) <= 25 ? (index==1 ?i-14 : i)  + 19 : (index==1 ?i-14 : i) + 21, 1, name, finalLore, rankedPlayer.getUniqueId());
        }
        return  inventory;
    }
    }