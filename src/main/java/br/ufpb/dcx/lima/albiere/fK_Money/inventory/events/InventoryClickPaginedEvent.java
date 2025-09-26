package br.ufpb.dcx.lima.albiere.fK_Money.inventory.events;

import br.ufpb.dcx.lima.albiere.fK_Money.FK_Balance;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.InventoryModuleInterface;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

public class InventoryClickPaginedEvent implements Listener {

    @EventHandler
    public void onCkickPaginedEvent(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        InventoryView inv = e.getView();

        String inventoryTitle = e.getView().getTitle();
        String topMoneyTitle = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Title")).replaceAll("&", "§");
        if(topMoneyTitle.equals(inventoryTitle)) {
            if (e.getCurrentItem() == null) {
                return;
            }
            e.setCancelled(
                    true
            );
            Material a = Material.getMaterial(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.Type"));
            String name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.item_nextAndPrevious.NextName")).replaceAll("&", "§");
            if (e.getCurrentItem().getType() == a && Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().equals(name)) {
                List<String> nextLore = FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.item_nextAndPrevious.NextLore")
                        .stream()
                        .map(s -> s.replaceAll("&", "§"))
                        .toList();
                if(e.getCurrentItem().getItemMeta().getLore() == nextLore) {
                    ((Inventory) e.getView()).clear();
                    InventoryModuleInterface inventory = FK_Balance.loadTopMoney(1);
                    int rankUser = FK_Balance.getEconomyManager().getYTopMoney(player);
                    name = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getString("essential.moneyTop.Name")).replaceAll("\\{Rank}", String.valueOf(rankUser)).replaceAll("\\{Player}", player.getName());
                    List<String> lore = Objects.requireNonNull(FK_Balance.getOptions().getConfig().getStringList("essential.moneyTop.Lore"));
                    lore.replaceAll(s -> s.replaceAll("\\{Money}", String.valueOf(FK_Balance.getEconomyManager().getPlayerBalance((player).getUniqueId()).getMoney())));
                    assert inventory != null;
                    inventory.addPlayerHead(4, 1, name, lore, (player).getUniqueId());

                    System.out.println("abrindo...");
                    inventory.open(player);

                }
            }
            }

    }
}
