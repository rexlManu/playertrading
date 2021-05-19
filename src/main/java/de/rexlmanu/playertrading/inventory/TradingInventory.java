/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.playertrading.inventory;

import com.google.common.primitives.Ints;
import de.rexlmanu.playertrading.PlayerTradingPlugin;
import de.rexlmanu.playertrading.trading.Trading;
import de.rexlmanu.playertrading.utility.builder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TradingInventory implements Listener {

    private Trading trading;
    private Inventory inventory;
    private ItemStack acceptorItem, inviterItem;

    public TradingInventory(Trading trading) {
        this.trading = trading;
        this.inventory = Bukkit.createInventory(null, 6 * 9, "§bTrading Inventory");
        for (int i = 1; i < 5; i++) {
            this.inventory.setItem(i * 9 + 4, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§r").build());
        }
        this.inventory.setItem(4, this.inviterItem = createDecisionItem(trading.getInviter(), false));

        this.inventory.setItem(49, this.acceptorItem = createDecisionItem(trading.getAcceptor(), false));
        Bukkit.getPluginManager().registerEvents(this, PlayerTradingPlugin.getPlugin());
    }

    private ItemStack createDecisionItem(Player player, boolean accepted) {
        return new ItemBuilder(accepted ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK)
                .setDisplayName(String.format("§8» §b%s", player.getName()))
                .addLore(String.format(accepted ? "§b%s §7has accepted the trade." : "§b%s §7has not accepted the trade.", player.getName()))
                .build();
    }

    private int[] getSideSlots(Player player) {
        if (this.trading.getInviter().equals(player)) {
            return new int[]{0, 1, 2, 3};
        } else {
            return new int[]{5, 6, 7, 8};
        }
    }

    private boolean hasAccepted(Player player) {
        return this.inventory.getItem(this.getDecisionSlot(player)).getType().equals(Material.EMERALD_BLOCK);
    }

    private int getDecisionSlot(Player player) {
        return this.trading.getInviter().equals(player) ? 4 : 49;
    }

    @EventHandler
    public void handle(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        this.getPlayers().forEach(player -> {
            for (int i = 0; i < 6; i++) {
                for (Integer slot : Ints.asList(this.getSideSlots(player))) {
                    ItemStack item = this.inventory.getItem(i * 9 + slot);
                    if (item != null)
                        player.getInventory().addItem();
                }
            }
        });

        Bukkit.getScheduler().runTask(PlayerTradingPlugin.getPlugin(), () -> {
            this.dispose();
            for (Player player : this.getPlayers()) {
                player.sendMessage(String.format("§7The trade got canceled by §b%s§7.", event.getPlayer().getName()));
                if (player.getOpenInventory() == null) continue;
                if (player.getOpenInventory().getTopInventory() == null) continue;
                if (player.getOpenInventory().getTopInventory().equals(this.inventory)) {
                    try {
                        player.closeInventory();
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                }
            }
        });
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if (event.getView().getTopInventory().equals(this.inventory)) {
            if (event.isShiftClick()) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(this.inventory)) return;
        if (event.getSlot() % 9 == 4) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);

            if (event.getCurrentItem() == null) return;
            int decisionSlot = this.getDecisionSlot(player);
            if (event.getSlot() == decisionSlot) {
                this.inventory.setItem(decisionSlot, this.createDecisionItem(player, !this.hasAccepted(player)));
            }

            if (this.hasAccepted(this.trading.getAcceptor()) && this.hasAccepted(this.trading.getInviter())) {
                this.acceptedTrade();
                return;
            }
        }
        if (!Ints.contains(this.getSideSlots(player), event.getSlot() % 9)) {
            event.setCancelled(true);
            event.setResult(Event.Result.DENY);
            return;
        }
    }

    @EventHandler
    public void handle(InventoryDragEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        for (Integer inventorySlot : event.getInventorySlots()) {
            if (!Ints.contains(this.getSideSlots(player), inventorySlot)) {
                event.setCancelled(true);
                event.setResult(Event.Result.DENY);
            }
        }
    }

    private void acceptedTrade() {
        this.getItemStacks(this.trading.getInviter()).forEach(itemStack -> this.trading.getAcceptor().getInventory().addItem(itemStack));
        this.getItemStacks(this.trading.getAcceptor()).forEach(itemStack -> this.trading.getInviter().getInventory().addItem(itemStack));
        this.trading.getAcceptor().closeInventory();
        this.trading.getInviter().closeInventory();
        this.trading.getInviter().sendMessage(String.format("§7You have successful traded with §b%s§7.", this.trading.getAcceptor().getName()));
        this.trading.getAcceptor().sendMessage(String.format("§7You have successful traded with §b%s§7.", this.trading.getInviter().getName()));
        this.dispose();
    }

    private List<ItemStack> getItemStacks(Player player) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (Integer slot : Ints.asList(this.getSideSlots(player))) {
                ItemStack item = inventory.getItem(i * 9 + slot);
                if (item != null)
                    itemStacks.add(item);
            }
        }
        return itemStacks;
    }

    private List<Player> getPlayers() {
        return Arrays.asList(this.trading.getAcceptor(), this.trading.getInviter());
    }

    private void dispose() {
        HandlerList.unregisterAll(this);
        PlayerTradingPlugin.getPlugin().getTradeHandler().tradingEnded(this.trading);
    }

    public void openInventory() {
        this.getPlayers().forEach(player -> player.openInventory(this.inventory));
    }
}
