package org.cards_tracker.util;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Util {
    @NotNull
    public static Optional<String> findFirstDuplicate(@NotNull final List<String> items) {
        final HashSet<Object> distinctSet = new HashSet<>(items.size());
        for (String item : items) {
            if (!distinctSet.add(item)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    @NotNull
    public static Optional<String> findFirstDistinct(@NotNull final List<String> firstItems,
                                                     @NotNull final List<String> secondItems) {
        final HashMap<String, Integer> itemOccurrences = new HashMap<>(firstItems.size() + secondItems.size());
        for (String item : firstItems) {
            if (itemOccurrences.containsKey(item)) {
                itemOccurrences.replace(item, itemOccurrences.get(item) + 1);
                continue;
            }
            itemOccurrences.put(item, 1);
        }
        for (String item : secondItems) {
            if (itemOccurrences.containsKey(item)) {
                itemOccurrences.replace(item, itemOccurrences.get(item) + 1);
                continue;
            }
            return Optional.of(item);
        }
        for (Map.Entry<String, Integer> item : itemOccurrences.entrySet()) {
            boolean existsInBothLists = item.getValue() % 2 == 0;
            if (!existsInBothLists) {
                return Optional.of(item.getKey());
            }
        }
        return Optional.empty();
    }
}
