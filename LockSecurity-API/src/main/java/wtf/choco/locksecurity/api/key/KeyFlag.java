package wtf.choco.locksecurity.api.key;

import org.bukkit.ChatColor;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a property (flag) that may be assigned to a smithed key.
 *
 * @since 3.0.0
 * @author Parker Hawke - Choco
 */
public enum KeyFlag {

    /**
     * The key cannot be duplicated in a crafting table with an unsmithed key.
     */
    PREVENT_DUPLICATION(0x01, ChatColor.RED + "This key cannot be duplicated."),

    /**
     * The key cannot be merged in a crafting table with another smithed key.
     */
    PREVENT_MERGING(0x02, ChatColor.RED + "This key cannot be merged."),

    /**
     * The key cannot be reset in a crafting table.
     */
    PREVENT_RESETTING(0x04, ChatColor.RED + "This key cannot be reset."),

    /**
     * The key will break on use.
     */
    BREAK_ON_USE(0x08, ChatColor.RED + "This key will break on use."),

    /**
     * The key should hide coordinates in the key's lore.
     */
    HIDE_BLOCK_COORDINATES(0x10),

    /**
     * The key should hide the lore applied by other key flags. Very meta...
     */
    HIDE_FLAG_LORE(0x20);


    public static final int BITMASK = 0b111111;

    private final int bit;
    private final String loreEntry;

    private KeyFlag(int bit, String loreEntry) {
        this.bit = bit;
        this.loreEntry = loreEntry;
    }

    private KeyFlag(int bit) {
        this(bit, null);
    }

    /**
     * Get the internal bit used to represent this flag in persistent storage.
     *
     * @return the internal bit representation
     */
    public int getBit() {
        return bit;
    }

    /**
     * Get the line of text to be written in the key's lore.
     *
     * @return the lore entry. null if none
     */
    @Nullable
    public String getLoreEntry() {
        return loreEntry;
    }

}
