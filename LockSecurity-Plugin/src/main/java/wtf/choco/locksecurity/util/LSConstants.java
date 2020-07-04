package wtf.choco.locksecurity.util;

import java.util.Locale;

import wtf.choco.locksecurity.LockSecurity;

public final class LSConstants {

    // Configuration entries

    public static final String METRICS = "Metrics";
    public static final String PERFORM_UPDATE_CHECKS = "PerformUpdateChecks";

    public static final String KEYS_UNSMITHED_MODEL_DATA = "Keys.Unsmithed.ModelData";
    public static final String KEYS_UNSMITHED_RECIPE_YIELD = "Keys.Unsmithed.Recipe.Yield";
    public static final String KEYS_SMITHED_MODEL_DATA = "Keys.Smithed.ModelData";
    public static final String KEYS_ALLOW_KEY_DUPLICATION = "Keys.AllowKeyDuplication";
    public static final String KEYS_ALLOW_KEY_MERGING = "Keys.AllowKeyMerging";
    public static final String KEYS_ALLOW_KEY_RESETTING = "Keys.AllowKeyRestting";

    public static final String LOCKABLE_BLOCKS = "LockableBlocks";


    // Permissions (only those referenced in source - these are not all available permissions)

    public static final String LOCKSECURITY_BLOCK_CLONEKEY = "locksecurity.block.clonekey";
    public static final String LOCKSECURITY_BLOCK_INSPECT = "locksecurity.block.inspect";
    public static final String LOCKSECURITY_BLOCK_LOCK = "locksecurity.block.lock";
    public static final String LOCKSECURITY_BLOCK_NICKNAME = "locksecurity.block.nickname";
    public static final String LOCKSECURITY_BLOCK_UNLOCK = "locksecurity.block.unlock";

    public static final String LOCKSECURITY_COMMAND_LOCKLIST_OTHER = "locksecurity.command.locklist.other";
    public static final String LOCKSECURITY_COMMAND_RELOAD = "locksecurity.command.reload";

    public static final String LOCKSECURITY_CRAFTING_DUPLICATE = "locksecurity.crafting.duplicate";
    public static final String LOCKSECURITY_CRAFTING_MERGE = "locksecurity.crafting.merge";
    public static final String LOCKSECURITY_CRAFTING_RESET = "locksecurity.crafting.reset";
    public static final String LOCKSECURITY_CRAFTING_UNSMITHED = "locksecurity.crafting.unsmithed";

    public static final String LOCKSECURITY_NOTIFYUPDATE = "locksecurity.notifyupdate";

    public static final String MINECRAFT_COMMAND_TELEPORT = "minecraft.command.teleport";


    // Miscellaneous

    public static final String PATH_RESOURCE_PACK = "resource_pack/LockSecurityRP.zip";
    public static final String RECIPE_CATEGORY_UNSMITHED_KEY = LockSecurity.getInstance().getName().toLowerCase(Locale.ROOT) + ":unsmithed_key";

    private LSConstants() { }

}
