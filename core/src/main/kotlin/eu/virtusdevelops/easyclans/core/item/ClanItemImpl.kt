package eu.virtusdevelops.easyclans.core.item

import eu.virtusdevelops.easyclans.api.item.ClanItem
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Registry
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class ClanItemImpl(
    override val name: String?,
    override val lore: List<String> = emptyList(),
    override val material: String,
    override val customModelData: Int?,
    override val enchantments: Map<String, Int> = emptyMap(),
    override val itemFlags: List<String> = emptyList(),
    override val nbtData: Map<String, Any> = emptyMap()
) : ClanItem {



    // move this somewhere else to remove spigotapi dependency
    fun ClanItem.toItemStack(): ItemStack{
        val item = ItemStack(Material.valueOf(material))
        val meta = item.itemMeta ?: return item


        // TODO: use MiniMessage to construct actual colors

        name?.let { meta.displayName(it) }
        if (lore.isNotEmpty()) meta.lore = lore


        customModelData?.let { meta.setCustomModelData(it) }

        for (flag in itemFlags) {
            meta.addItemFlags(ItemFlag.valueOf(flag))
        }


        // Apply enchantments
        for ((ench, level) in enchantments) {
            val enchant = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(ench.lowercase())) ?: continue
            meta.addEnchant(enchant, level, true)
        }

        // Handle NBT or special item types
        // (e.g., banners, skulls, potions) – you’ll need special handling here

        item.itemMeta = meta
        return item
    }

}