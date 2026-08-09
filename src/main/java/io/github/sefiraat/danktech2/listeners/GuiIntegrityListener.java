package io.github.sefiraat.danktech2.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Blocks inventory actions that bypass the virtual storage accounting used by
 * Dank packs. Bukkit resolves COLLECT_TO_CURSOR across both inventories before
 * ChestMenu handlers can update the pack's persisted amount.
 */
public final class GuiIntegrityListener implements Listener {

    private static final String PACK_GUI_PREFIX = "Dank Pack - Tier ";

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().startsWith(PACK_GUI_PREFIX)) {
            return;
        }

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getClick().isKeyboardClick()) {
            event.setCancelled(true);
        }
    }
}
