package br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager;

import br.ufpb.dcx.lima.albiere.fK_Money.configs.mysql.MySQLConfig;
import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerEconomy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Manager implements ManagerInterface{
    private final MySQLConfig mySQLConfig;
    private Map<UUID, PlayerEconomy> playerBalances;


    public Manager() {
        this.mySQLConfig = new MySQLConfig();
        this.playerBalances = new HashMap<>();
    }

    /**
     * Carrega todos os jogadores do banco de dados para a memória.
     * Ideal para ser chamado no onEnable() do seu plugin.
     */
    public void loadAllPlayersBalance() {
        this.playerBalances = mySQLConfig.loadAllAccounts();
    }

    /**
     * Salva todos os jogadores em memória para o banco de dados.
     * Ideal para ser chamado no onDisable() do seu plugin.
     */
    public void saveAllPlayersBalance() {
        if (!playerBalances.isEmpty()) {
            mySQLConfig.saveAllAccounts(playerBalances);
        }
    }

    /**
     * Carrega um jogador específico (ex: ao entrar no servidor).
     * Se ele já estiver em memória, não faz nada.
     * Se não estiver, busca no DB. Se não achar no DB, cria uma nova conta.
     */
    public void loadPlayerBalance(Player player) {
        UUID uuid = player.getUniqueId();
        if (playerBalances.containsKey(uuid)) {
            return;
    }

        PlayerEconomy economy = mySQLConfig.loadAccount(uuid);
        if (economy == null) {
            economy = new PlayerEconomy(BigDecimal.ZERO, BigDecimal.ZERO, Bukkit.getOfflinePlayer(uuid).getName());
        }
        playerBalances.put(uuid, economy);
    }

    /**
     * Salva os dados de um jogador específico no banco de dados.
     * Ideal para ser chamado quando o jogador sai do servidor (PlayerQuitEvent).
     */
    public void savePlayerBalance(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerEconomy economy = playerBalances.get(uuid);
        if (economy != null) {
            mySQLConfig.saveAccount(uuid, economy);
        }
    }

    public PlayerEconomy getPlayerBalance(UUID playerUUID) {

        return playerBalances.computeIfAbsent(playerUUID, k -> new PlayerEconomy(BigDecimal.ZERO, BigDecimal.ZERO, Bukkit.getOfflinePlayer(playerUUID).getName()));
    }

    public void transferirMoney(UUID remetenteUUID, UUID destinatarioUUID, BigDecimal valor) {
        PlayerEconomy remetente = getPlayerBalance(remetenteUUID);
        PlayerEconomy destinatario = getPlayerBalance(destinatarioUUID);

        remetente.removeMoney(valor);
        destinatario.addMoney(valor);
    }

    public void transferirCash(UUID remetenteUUID, UUID destinatarioUUID, BigDecimal valor) {
        PlayerEconomy remetente = getPlayerBalance(remetenteUUID);
        PlayerEconomy destinatario = getPlayerBalance(destinatarioUUID);

        remetente.removeCash(valor);
        destinatario.addCash(valor);
    }

    public void adicionarMoney(Player user, BigDecimal money) {
        getPlayerBalance(user.getUniqueId()).addMoney(money);
    }

    public void adicionarCash(Player user, BigDecimal cash) {
        getPlayerBalance(user.getUniqueId()).addCash(cash);
    }

    public void setMoney(Player user, BigDecimal money) {
        getPlayerBalance(user.getUniqueId()).setMoney(money);
    }

    public void setCash(Player user, BigDecimal cash) {
        getPlayerBalance(user.getUniqueId()).setCash(cash);
    }

    @Override
    public List<PlayerEconomy> listarUsuarios(Player user) {
        List<PlayerEconomy> list = new ArrayList<>();
        playerBalances.forEach((id, economy) -> {
            list.add(economy);
        });
        return list;
    }


    public List<OfflinePlayer> getTopMoney() {
        List<Map.Entry<UUID, PlayerEconomy>> sortedList = new ArrayList<>(playerBalances.entrySet());
        sortedList.sort((a, b) -> b.getValue().getMoney().compareTo(a.getValue().getMoney()));
        return sortedList.stream()
                .map(entry -> Bukkit.getOfflinePlayer(entry.getKey()))
                .collect(Collectors.toList());
    }

    public int getYTopMoney(Player player) {
        List<OfflinePlayer> topPlayers = getTopMoney();
        return topPlayers.indexOf(player) + 1;
    }

    public List<OfflinePlayer> getTopCash() {
        List<Map.Entry<UUID, PlayerEconomy>> sortedList = new ArrayList<>(playerBalances.entrySet());
        sortedList.sort((a, b) -> b.getValue().getCash().compareTo(a.getValue().getCash()));
        return sortedList.stream()
                .map(entry -> Bukkit.getOfflinePlayer(entry.getKey()))
                .collect(Collectors.toList());
    }

    public int getYTopCash(Player player) {
        List<OfflinePlayer> topPlayers = getTopMoney();
        return topPlayers.indexOf(player) + 1;
    }
}