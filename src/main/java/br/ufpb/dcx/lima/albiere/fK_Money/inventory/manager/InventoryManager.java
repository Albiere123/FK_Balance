package br.ufpb.dcx.lima.albiere.fK_Money.inventory.manager;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.InventoryModuleInterface;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.PagedInventory;
import br.ufpb.dcx.lima.albiere.fK_Money.inventory.SimpleInventory;

public class InventoryManager implements InventoryInterface {

    @Override
    public InventoryModuleInterface createInventory(String id, String title, int rows, InventoryTypes type) {
        String formattedTitle = title.replaceAll("&", "§");

        switch (type) {
            case PAGED:
                return new PagedInventory(id, formattedTitle, rows);
            case SIMPLE:
                return new SimpleInventory(id, formattedTitle, rows);
            default:
                throw new IllegalArgumentException("Tipo de inventário desconhecido: " + type);
        }
    }

}
