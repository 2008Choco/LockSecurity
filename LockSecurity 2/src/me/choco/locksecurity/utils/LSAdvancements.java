package me.choco.locksecurity.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

import me.choco.locksecurity.LockSecurity;
import me.choco.locksecurity.utils.AdvancementBuilder.FrameType;

public class LSAdvancements {
	
	/* Advancement Structure
	 * 
	 * Hide the Evidence! <--- It was an Accident... ---> Safety is Over-rated
	 *                                 ^
	 *                                 |             |-> I Think I'm Safe ---> Lockpicks? No. Friendship
	 *                                 |             |
	 *                     |-> Shut the Front Door! -|-> I Need a Keychain
	 *                     |                         |
	 *     LockSecurity 2 -|                         |-> Over-protective
	 *                     |
	 *                     |-> Locksmith --------|-> Less Keys, More Numbers
	 *                             |             |
	 *                             v             |-> More Keys? Why?
	 *                      Whoops! Wrong Key!
	 */
	
	public static Advancement LOCKSECURITY_2; // Root advancement
	public static Advancement SHUT_THE_FRONT_DOOR;
	public static Advancement IT_WAS_AN_ACCIDENT;
	public static Advancement HIDE_THE_EVIDENCE;
	public static Advancement SAFETY_IS_OVERRATED;
	public static Advancement I_THINK_IM_SAFE, I_NEED_A_KEYCHAIN, OVER_PROTECTIVE;
	public static Advancement LOCKPICKS_NO_FRIENDSHIP;
	
	public static Advancement LOCKSMITH;
	public static Advancement WHOOPS_WRONG_KEY;
	public static Advancement LESS_KEYS_MORE_NUMBERS, MORE_KEYS_WHY;
	
	public static void loadAdvancements(LockSecurity plugin) {
		if (LOCKSECURITY_2 != null) return;
		
		LOCKSECURITY_2 = new AdvancementBuilder(new NamespacedKey(plugin, "root"))
				.withTitle("LockSecurity 2")
				.withDescription("Obtain your first unsmithed key!")
				.withIcon(NamespacedKey.minecraft("tripwire_hook"))
				.withBackground(NamespacedKey.minecraft("textures/blocks/planks_spruce.png"))
				.save();
		SHUT_THE_FRONT_DOOR = new AdvancementBuilder(new NamespacedKey(plugin, "shut_the_front_door"))
				.withTitle("Shut the Front Door!")
				.withDescription("Lock your very first block!")
				.withIcon(NamespacedKey.minecraft("wooden_door"))
				.withParent(LOCKSECURITY_2.getKey().toString())
				.withFrame(FrameType.GOAL)
				.save();
		IT_WAS_AN_ACCIDENT = new AdvancementBuilder(new NamespacedKey(plugin, "it_was_an_accident"))
				.withTitle("It was an Accident...")
				.withDescription("Didn't mean to lock that one. Unlock your first block.")
				.withIcon(NamespacedKey.minecraft("red_flower"))
				.withParent(SHUT_THE_FRONT_DOOR.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		SAFETY_IS_OVERRATED = new AdvancementBuilder(new NamespacedKey(plugin, "safety_is_overrated"))
				.withTitle("Safety is Overrated")
				.withDescription("Unlock 10 blocks.")
				.withIcon(NamespacedKey.minecraft("lava_bucket"))
				.withParent(IT_WAS_AN_ACCIDENT.getKey().toString())
				.withFrame(FrameType.GOAL)
				.save();
		HIDE_THE_EVIDENCE = new AdvancementBuilder(new NamespacedKey(plugin, "hide_the_evidence"))
				.withTitle("Hide the evidence!")
				.withDescription("I guess you don't need that key anymore. Unsmith it!")
				.withIcon(NamespacedKey.minecraft("glass_pane"))
				.withParent(IT_WAS_AN_ACCIDENT.getKey().toString())
				.save();
		I_THINK_IM_SAFE = new AdvancementBuilder(new NamespacedKey(plugin, "i_think_im_safe"))
				.withTitle("I Think I'm Safe")
				.withDescription("Lock 10 blocks. That should be enough!")
				.withIcon(NamespacedKey.minecraft("chest"))
				.withParent(SHUT_THE_FRONT_DOOR.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		I_NEED_A_KEYCHAIN = new AdvancementBuilder(new NamespacedKey(plugin, "i_need_a_keychain"))
				.withTitle("I Need a Keychain")
				.withDescription("Lock 25 blocks. You shouldn't need much more than that.")
				.withIcon(NamespacedKey.minecraft("fence_gate"))
				.withParent(SHUT_THE_FRONT_DOOR.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		OVER_PROTECTIVE = new AdvancementBuilder(new NamespacedKey(plugin, "over_protective"))
				.withTitle("Over-protective")
				.withDescription("Lock 100 blocks! Gee I don't know about this...")
				.withIcon(NamespacedKey.minecraft("iron_door"))
				.withParent(SHUT_THE_FRONT_DOOR.getKey().toString())
				.withFrame(FrameType.CHALLENGE)
				.save();
		LOCKPICKS_NO_FRIENDSHIP = new AdvancementBuilder(new NamespacedKey(plugin, "lockpicks_no_friendship"))
				.withTitle("Lockpicks? No. Friendship!")
				.withDescription("Transfer one of your locks to a friend. Come on... you have enough")
				.withIcon(NamespacedKey.minecraft("paper"))
				.withParent(I_THINK_IM_SAFE.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		
		LOCKSMITH = new AdvancementBuilder(new NamespacedKey(plugin, "locksmith"))
				.withTitle("Locksmith")
				.withDescription("Open a locked block with its respective key")
				.withIcon(NamespacedKey.minecraft("iron_nugget"))
				.withParent(LOCKSECURITY_2.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		WHOOPS_WRONG_KEY = new AdvancementBuilder(new NamespacedKey(plugin, "whoops_wrong_key"))
				.withTitle("Whoops! Wrong Key!")
				.withDescription("Erm.. this isn't the right key. Where did I put it!?")
				.withIcon(NamespacedKey.minecraft("redstone_block"))
				.withParent(LOCKSMITH.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		LESS_KEYS_MORE_NUMBERS = new AdvancementBuilder(new NamespacedKey(plugin, "less_keys_more_numbers"))
				.withTitle("Less Keys, More Numbers")
				.withDescription("Merge two keys together in a crafting table")
				.withIcon(NamespacedKey.minecraft("slime_ball"))
				.withParent(LOCKSMITH.getKey().toString())
				.setAnnounceToChat(false)
				.save();
		MORE_KEYS_WHY = new AdvancementBuilder(new NamespacedKey(plugin, "more_keys_why"))
				.withTitle("More Keys? Why?")
				.withDescription("Duplicate a key in a crafting table")
				.withIcon(NamespacedKey.minecraft("crafting_table"))
				.withParent(LOCKSMITH.getKey().toString())
				.setAnnounceToChat(false)
				.save();
	}
	
}