package me.choco.locksecurity.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.utils.AdvancementBuilder.Criteria;

public class LSAdvancements {
	
	public static Advancement THE_KEY_TO_EVERYTHING; // Root advancement
	
	public static void loadAdvancements(LockSecurity plugin) {
		if (THE_KEY_TO_EVERYTHING != null) return;
		
		THE_KEY_TO_EVERYTHING = new AdvancementBuilder(new NamespacedKey(plugin, "root"))
				.withTitle("The Key to Everything")
				.withDescription("Or... almost everything. Obtain your first unsmithed key!")
				.withIcon(NamespacedKey.minecraft("tripwire_hook"))
				.withBackground(NamespacedKey.minecraft("textures/blocks/planks_oak.png"))
				.addCriteria(new Criteria("impossible", "minecraft:impossible"))
				.setAnnounceToChat(false)
				.save();
	}
	
}