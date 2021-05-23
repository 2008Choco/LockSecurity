package wtf.choco.locksecurity.metrics;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;

import wtf.choco.locksecurity.LockSecurity;

public final class StatHandler {

    @SuppressWarnings("unused")
    private static final String HEY_PEOPLE_CRACKING_THIS = "Please leave 'PREMIUM_STATUS' alone :) This is for statistic purposes only, I promise! The only time this is not 'Premium' is in the free version, 3.0.0."
        + " Actually... you can even set the value below to 'Leaked' if you really want! That would be helpful too, at least for stats!";
    // This is only ever going to be different in the free version... Piracy is not included here. They're considered "Premium"
    private static final String PREMIUM_STATUS = "Premium";

    private static StatHandler instance;

    private StatHandler(LockSecurity plugin, Metrics metrics) {
        // Locked blocks by type, "lockedByType"
        metrics.addCustomChart(new AdvancedPie("lockedByType", () -> {
            Map<String, Integer> lockedByType = new HashMap<>();
            plugin.getLockedBlockManager().getLockedBlocks().forEach(block -> lockedByType.merge(block.getType().getKey().toString(), 1, Integer::sum));
            return lockedByType;
        }));

        // Lock count per player, "perPlayerLockCount"
        metrics.addCustomChart(new DrilldownPie("perPlayerLockCount", () -> {
            Map<String, Map<String, Integer>> perPlayerAmount = new HashMap<>();

            Map<UUID, Integer> lockCount = new HashMap<>();
            plugin.getLockedBlockManager().getLockedBlocks().forEach(block -> lockCount.merge(block.getOwner().getUniqueId(), 1, Integer::sum));

            lockCount.forEach((playerUUID, amount) -> {
                Map<String, Integer> specificAmountMap = null;

                if (amount >= 0 && amount < 5) {
                    specificAmountMap = perPlayerAmount.computeIfAbsent("1 - 4", $ -> new HashMap<>());
                } else if (amount >= 5 && amount < 10) {
                    specificAmountMap = perPlayerAmount.computeIfAbsent("5 - 9", $ -> new HashMap<>());
                } else if (amount >= 10 && amount < 25) {
                    specificAmountMap = perPlayerAmount.computeIfAbsent("10 - 24", $ -> new HashMap<>());
                } else if (amount >= 25 && amount < 50) {
                    specificAmountMap = perPlayerAmount.computeIfAbsent("25 - 49", $ -> new HashMap<>());
                } else { // 50 or more
                    specificAmountMap = perPlayerAmount.computeIfAbsent("50 or more", $ -> new HashMap<>());
                }

                specificAmountMap.merge(String.valueOf(amount), 1, Integer::sum);
            });

            return perPlayerAmount;
        }));

        // Free or premium, "premium"
        metrics.addCustomChart(new SimplePie("premium", () -> PREMIUM_STATUS));
    }

    public static void init(LockSecurity plugin, int metricsId) {
        Preconditions.checkArgument(instance == null, "Metrics have already been initialized");
        StatHandler.instance = new StatHandler(plugin, new Metrics(plugin, metricsId));
    }

}
