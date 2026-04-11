package com.earth2me.essentials.craftbukkit;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class InventoryData {
    private final List<Integer> emptySlots;
    private final Map<ItemStack, List<Integer>> partialSlots;

    public InventoryData(List<Integer> emptySlots, Map<ItemStack, List<Integer>> partialSlots) {
        this.emptySlots = emptySlots;
        this.partialSlots = partialSlots;
    }

    public List<Integer> getEmptySlots() {
        return emptySlots;
    }

    public Map<ItemStack, List<Integer>> getPartialSlots() {
        return partialSlots;
    }
}
