package br.ufpb.dcx.lima.albiere.fK_Money.iniciais;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PlayerEconomy {
    private final Map<String, BigDecimal> tipoMoeda = new HashMap<>();
    private final String player_name;

    public PlayerEconomy(BigDecimal money, BigDecimal cash, String player_name) {
        tipoMoeda.put("money", money);
        tipoMoeda.put("cash", cash);
        this.player_name = player_name;
    }

    public BigDecimal getMoney() {
        return tipoMoeda.get("money");
    }

    public void setMoney(BigDecimal money) {
        tipoMoeda.put("money", money);
    }

    public BigDecimal getCash() {
        return tipoMoeda.get("cash");
    }

    public void setCash(BigDecimal cash) {
        tipoMoeda.put("cash", cash);
    }

    public void addMoney(BigDecimal amount) {
        tipoMoeda.put("money", getMoney().add(amount));
    }

    public void removeMoney(BigDecimal amount) {
        tipoMoeda.put("money", getMoney().subtract(amount));
    }

    public void addCash(BigDecimal amount) {
        tipoMoeda.put("cash", getCash().add(amount));
    }

    public void removeCash(BigDecimal amount) {
        tipoMoeda.put("cash", getCash().subtract(amount));
    }

    public String getPlayer_name() {
        return player_name;
    }
}