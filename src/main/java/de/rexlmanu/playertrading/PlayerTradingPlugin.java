/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.rexlmanu.playertrading;

import de.rexlmanu.playertrading.listener.TradingListener;
import de.rexlmanu.playertrading.trading.TradeHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class PlayerTradingPlugin extends JavaPlugin {

    @Getter
    private static PlayerTradingPlugin plugin;

    private TradeHandler tradeHandler;

    @Override
    public void onEnable() {
        plugin = this;

        this.tradeHandler = new TradeHandler();

        Bukkit.getPluginManager().registerEvents(new TradingListener(), this);
    }
}
