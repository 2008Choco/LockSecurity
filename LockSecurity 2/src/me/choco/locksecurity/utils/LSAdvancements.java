package me.choco.locksecurity.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.utils.AdvancementBuilder.Criteria;

public class LSAdvancements {
	
	public static Advancement LOCKSECURITY_2; // Root advancement
	
	public static void loadAdvancements(LockSecurity plugin) {
		if (LOCKSECURITY_2 != null) return;
		
		LOCKSECURITY_2 = new AdvancementBuilder(new NamespacedKey(plugin, "root"))
				.withTitle("LockSecurity 2")
				.withDescription("Obtain your first unsmithed key!")
				.withIcon(NamespacedKey.minecraft("tripwire_hook"))
				.withBackground(NamespacedKey.minecraft("textures/blocks/planks_oak.png"))
				.addCriteria(new Criteria("impossible", "minecraft:impossible"))
				.setAnnounceToChat(false)
				.save();
		
		
	}
	
}