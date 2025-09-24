package br.ufpb.dcx.lima.albiere.fK_Money.eventos;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Money.inventario.SingleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Objects;

public class ClickInventory implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if(FK_Balance.getManager().inventoryExists(e.getView().getTitle())) {
            e.setCancelled(true);
            if(e.getView().getTitle().equals(Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Title")).replaceAll("&", "§"))) {
                SingleInventory inventory = FK_Balance.getManager().getInventory("topMoney"+e.getWhoClicked().getUniqueId());
                List<Player> rank = FK_Balance.getEconomyManager().getTopMoney();
                switch (e.getSlot()) {
                    case 26:
                        if(FK_Balance.getEconomyManager().getTopMoney().size() > 14) {

                            inventory.clear();


                            List<String> lore1 = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore"));
                            lore1.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance((e.getWhoClicked()).getUniqueId()).getMoney())));
                            String name1 = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(FK_Balance.getEconomyManager().getYTopMoney((Player) e.getWhoClicked()))).replaceAll("\\{Player}", e.getWhoClicked().getName());
                            inventory.addPlayerHead(4, 1, name1, lore1, (e.getWhoClicked()).getUniqueId());

                            lore1 = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.PreviousLore");
                            lore1.replaceAll(s -> s.replaceAll("&", "§"));
                            inventory.addItem(18, Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")), 1, Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.PreviousName")).replaceAll("&", "§"), lore1);

                            for (int i = 14; i < FK_Balance.getEconomyManager().getTopMoney().size(); i++) {
                                String name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(i + 1)).replaceAll("\\{Player}", rank.get(i).getName());
                                List<String> lore = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore");
                                int i1 = i;
                                lore.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance(rank.get(i1).getUniqueId()).getMoney())));
                                inventory.addPlayerHead(i - 14 <= 25 ? i + 19 : i + 21, 1, name, lore, rank.get(i).getUniqueId());
                            }
                        }

                    case 18:

                        inventory.clear();

                        List<String> lore1 = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore"));
                        lore1.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance((e.getWhoClicked()).getUniqueId()).getMoney())));
                        String name1 = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(FK_Balance.getEconomyManager().getYTopMoney((Player) e.getWhoClicked()))).replaceAll("\\{Player}", e.getWhoClicked().getName());
                        inventory.addPlayerHead(4, 1, name1, lore1, (e.getWhoClicked()).getUniqueId());

                        lore1 = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.NextLore");
                        lore1.replaceAll(s -> s.replaceAll("&", "§"));
                        inventory.addItem(26, Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type")), 1, Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.NextName")).replaceAll("&", "§"), lore1);
                        
                        for(int i = 0; i < FK_Balance.getEconomyManager().getTopMoney().size() ; i++) {
                            String name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(i+1)).replaceAll("\\{Player}", rank.get(i).getName());
                            List<String> lore = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore");
                            int i1 = i;
                            lore.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance(rank.get(i1).getUniqueId()).getMoney())));
                            inventory.addPlayerHead(i <= 25 ? i+19 : i+21, 1, name, lore, rank.get(i).getUniqueId());
                        }
                }
            }
        }

    }
}
