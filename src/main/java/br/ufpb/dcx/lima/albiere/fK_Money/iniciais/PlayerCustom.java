package br.ufpb.dcx.lima.albiere.fK_Money.iniciais;

import org.bukkit.entity.Player;

public class PlayerCustom {

    private final Player p;

    public PlayerCustom(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    public void sendColouredMessage(String s) {
        p.sendMessage(s.replaceAll("&", "§"));
    }
}
