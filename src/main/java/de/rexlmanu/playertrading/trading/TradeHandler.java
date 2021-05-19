/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.playertrading.trading;

import de.rexlmanu.playertrading.PlayerTradingPlugin;
import de.rexlmanu.playertrading.inventory.TradingInventory;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TradeHandler extends BukkitRunnable {

    private static final long THIRTY_SECONDS_IN_MILLIS = TimeUnit.SECONDS.toMillis(30);

    @Getter
    private Set<Trading> tradings;
    private Set<Player> inTrades;

    public TradeHandler() {
        this.tradings = new CopyOnWriteArraySet<>();
        this.inTrades = new CopyOnWriteArraySet<>();
        this.runTaskTimerAsynchronously(PlayerTradingPlugin.getPlugin(), 0, 1);
    }

    public void run() {
        long currentTimeMillis = System.currentTimeMillis();
        this.tradings.stream().filter(trading -> !trading.isAccepted() &&
                (trading.getInvitedSendAt() - currentTimeMillis) > THIRTY_SECONDS_IN_MILLIS).forEach(this::tradingTimeOut);
    }

    public boolean isTrading(Player player) {
        return this.inTrades.contains(player);
    }

    public boolean isInvited(Player player) {
        return this.tradings.stream().anyMatch(trading -> !trading.isAccepted() && trading.getAcceptor().equals(player));
    }

    public Set<Trading> getInvitedTradings(Player acceptor) {
        return this.tradings.stream().filter(trading -> !trading.isAccepted() && trading.getAcceptor().equals(acceptor)).collect(Collectors.toSet());
    }

    public List<Player> getInvitations(Player inviter) {
        return this.tradings.stream().filter(trading -> trading.getInviter().equals(inviter)).map(Trading::getAcceptor).collect(Collectors.toList());
    }

    private void tradingTimeOut(Trading trading) {
        this.tradings.remove(trading);
        trading.getInviter().sendMessage(String.format("§7Your invitation to §b%s §7timed out.", trading.getAcceptor().getName()));
    }

    public void addTrading(Trading trading) {
        this.tradings.add(trading);
        trading.getAcceptor().sendMessage(String.format("§7You got invited by §b%s §7to trade with him.", trading.getInviter().getName()));
        trading.getInviter().sendMessage(String.format("§7You invited §b%s §7to trade with him.", trading.getAcceptor().getName()));
    }

    public void acceptTrading(Trading trading) {
        this.tradings.remove(trading);
        trading.setAccepted(true);
        this.inTrades.add(trading.getAcceptor());
        this.inTrades.add(trading.getInviter());
        TradingInventory tradingInventory = new TradingInventory(trading);
        tradingInventory.openInventory();
    }

    public void tradingEnded(Trading trading) {
        this.inTrades.remove(trading.getInviter());
        this.inTrades.remove(trading.getAcceptor());
    }
}
