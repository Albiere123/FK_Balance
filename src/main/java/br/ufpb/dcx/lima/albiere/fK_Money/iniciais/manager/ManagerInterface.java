package br.ufpb.dcx.lima.albiere.fK_Money.iniciais.manager;

import br.ufpb.dcx.lima.albiere.fK_Money.iniciais.PlayerEconomy;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ManagerInterface {

    void loadAllPlayersBalance();
    void loadPlayerBalance(Player player);
    void savePlayerBalance(Player player);
    void saveAllPlayersBalance();
    PlayerEconomy getPlayerBalance(UUID playerUUID);
    void transferirMoney(UUID remetenteUUID, UUID destinatarioUUID, BigDecimal valor);
    void transferirCash(UUID remetenteUUID, UUID destinatarioUUID, BigDecimal valor);
    void adicionarMoney(Player user, BigDecimal money);
    void setMoney(Player user, BigDecimal money);
    List<PlayerEconomy> listarUsuarios(Player user);
    List<Player> getTopMoney();
    int getYTopMoney(Player player);
}
