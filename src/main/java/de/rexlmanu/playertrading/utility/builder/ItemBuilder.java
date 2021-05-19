/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2019.
 */
package de.rexlmanu.playertrading.utility.builder;

import com.google.common.collect.Lists;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/******************************************************************************************
 *    Urheberrechtshinweis                                                                *
 *    Copyright © Emmanuel Lampe 2019                                                     *
 *    Erstellt: 29.01.2019 / 00:09                                                        *
 *                                                                                        *
 *    Alle Inhalte dieses Quelltextes sind urheberrechtlich geschützt.                    *
 *    Das Urheberrecht liegt, soweit nicht ausdrücklich anders gekennzeichnet,            *
 *    bei Emmanuel Lampe. Alle Rechte vorbehalten.                                        *
 *                                                                                        *
 *    Jede Art der Vervielfältigung, Verbreitung, Vermietung, Verleihung,                 *
 *    öffentlichen Zugänglichmachung oder andere Nutzung                                  *
 *    bedarf der ausdrücklichen, schriftlichen Zustimmung von Emmanuel Lampe.             *
 ******************************************************************************************/

public final class ItemBuilder {

    private ItemStack itemStack;

    public ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(Material material, int amount) {
        this.itemStack = new ItemStack(material, amount);
    }

    public ItemBuilder(Material material, int amount, short durability) {
        this.itemStack = new ItemStack(material, amount, durability);
    }

    public ItemBuilder(Material material, int amount, int durability) {
        this.itemStack = new ItemStack(material, amount, (short) durability);
    }

    public ItemBuilder(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public static ItemBuilder itemStack(Material material) {
        return new ItemBuilder(material);
    }

    public static ItemBuilder itemStack(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder itemStack(Material material, int amount, short durability) {
        return new ItemBuilder(material, amount, durability);
    }

    public ItemBuilder setDisplayName(String name) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }

    public ItemBuilder setDurability(short durability) {
        this.itemStack.setDurability(durability);
        return this;
    }

    public ItemBuilder addLore(List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setLore(lore);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addLore(String... lore) {
        return this.addLore(Arrays.asList(lore));
    }

    public ItemBuilder addLore(String line) {
        List<String> lore = Lists.newArrayList();
        List<String> itemLore = this.itemStack.getItemMeta().getLore();
        if (Objects.nonNull(itemLore) && ! itemLore.isEmpty()) lore.addAll(itemLore);
        lore.add(line);
        return this.addLore(lore);
    }

    public ItemBuilder addItemFlags(ItemFlag... itemFlags) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addItemFlags(itemFlags);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder setSkullOwner(String owner) {
        SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
        skullMeta.setOwner(owner);
        this.itemStack.setItemMeta(skullMeta);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public ItemBuilder addEnchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addEnchant(final Enchantment enchantment, final int level) {
        this.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder addEnchant(final Enchantment enchantment) {
        this.addEnchant(enchantment, 1, true);
        return this;
    }

    public ItemBuilder removeEnchant(final Enchantment enchantment) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.removeEnchant(enchantment);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder addGlow() {
        this.addEnchant(Enchantment.DURABILITY);
        this.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        final ItemMeta itemMeta = this.itemStack.getItemMeta();
        itemMeta.setUnbreakable(unbreakable);
        this.itemStack.setItemMeta(itemMeta);
        return this;
    }

    public ItemBuilder removeEnchants() {
        this.itemStack.getEnchantments().forEach((enchantment, integer) -> {
            this.removeEnchant(enchantment);
        });
        return this;
    }
}
