package eu.virtusdevelops.easyclans.api.item

interface ClanItem {
    val name: String?
    val lore: List<String>

    val material: String
    val enchantments: Map<String, Int>
    val itemFlags: List<String>
    val customModelData: Int?
    val nbtData: Map<String, Any>
}