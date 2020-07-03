package wtf.choco.locksecurity.key;

import org.bukkit.ChatColor;

public enum KeyFlag {

    PREVENT_DUPLICATION(0x01, ChatColor.RED + "This key cannot be duplicated."),
    PREVENT_MERGING(0x02, ChatColor.RED + "This key cannot be merged."),
    PREVENT_RESETTING(0x04, ChatColor.RED + "This key cannot be reset."),
    BREAK_ON_USE(0x08, ChatColor.RED + "This key will break on use."),
    HIDE_BLOCK_COORDINATES(0x10);


    public static final int BITMASK = 0b11111;

    private final int bit;
    private final String loreEntry;

    private KeyFlag(int bit, String loreEntry) {
        this.bit = bit;
        this.loreEntry = loreEntry;
    }

    private KeyFlag(int bit) {
        this(bit, null);
    }

    public int getBit() {
        return bit;
    }

    public String getLoreEntry() {
        return loreEntry;
    }

}
