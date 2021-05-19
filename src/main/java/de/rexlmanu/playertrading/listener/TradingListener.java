/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.playertrading.listener;

import de.rexlmanu.playertrading.PlayerTradingPlugin;
import de.rexlmanu.playertrading.trading.TradeHandler;
import de.rexlmanu.playertrading.trading.Trading;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.Optional;

public class TradingListener implements Listener {

    @EventHandler
    public void handle(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;
        Player target = (Player) event.getRightClicked();

        TradeHandler handler = PlayerTradingPlugin.getPlugin().getTradeHandler();
        if (handler.isTrading(target)) {
            player.sendMessage(String.format("§b%s §7is already trading...", target.getName()));
            return;
        }

        if (handler.getInvitations(player).contains(target)) {
            player.sendMessage(String.format("§7You already invited §b%s §7to a trade.", target.getName()));
            return;
        }

        if (handler.isInvited(player)) {
            Trading trading = handler.getInvitedTradings(player).stream().filter(value -> value.getInviter().equals(target)).findAny().orElse(null);
            if (trading != null) {
                handler.acceptTrading(trading);
                return;
            }
        }

        handler.addTrading(Trading.createTrading(player, target));
    }

}
