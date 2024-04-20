package com.example.demo.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CollectionUtils {
    @Contract(pure = true)
    public static @NotNull Map<String, Set<String>> getFilteredMapExcludingEmptyValues(
            @NotNull Supplier<? extends Set<String>> keySupplier,
            @NotNull Function<String, ? extends Map.Entry<String, Set<String>>> mapper) {
        return getFilteredMap(keySupplier, mapper, isValueSetNotEmpty());
    }

    @Contract(pure = true)
    public static @NotNull Map<String, Set<String>> getFilteredMap(
            @NotNull Supplier<? extends Set<String>> keySupplier,
            @NotNull Function<String, ? extends Map.Entry<String, Set<String>>> mapper,
            @NotNull Predicate<? super Map.Entry<String, Set<String>>> filter) {
        return keySupplier.get().stream()
                .map(mapper)
                .filter(filter)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Contract(pure = true)
    public static @NotNull Function<String, Map.Entry<String, Set<String>>>
    mapToValueSet(@NotNull Function<String, Set<String>> valueSetGenerator) {
        return key -> new SimpleImmutableEntry<>(key, valueSetGenerator.apply(key));
    }


    @Contract(pure = true)
    public static @NotNull Predicate<? super Map.Entry<String, Set<String>>>
    isValueSetNotEmpty() {
        return entry -> !entry.getValue().isEmpty();
    }
}

