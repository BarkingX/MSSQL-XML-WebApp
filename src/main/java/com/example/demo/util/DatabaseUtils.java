package com.example.demo.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.SequencedSet;
import java.util.Set;
import java.util.function.Predicate;

public class DatabaseUtils {
    public static final String DEFAULT_TABLE_NAME_PATTERN = "%";
    public static final String DEFAULT_COLUMN_NAME_PATTERN = "%";
    private static final Set<String> SYSTEM_CATALOGS = Set.of("master", "tempdb", "model",
            "msdb");
    private static final Set<String> SYSTEM_SCHEMAS = Set.of("guest", "sys",
            "INFORMATION_SCHEMA");
    private static final Set<String> INVALID_TYPES = Set.of("geography", "geometry",
            "image", "uniqueidentifier");

    @Contract(pure = true)
    public static @NotNull Predicate<String> isNotSystemCatalog() {
        return catalog -> !SYSTEM_CATALOGS.contains(catalog);
    }

    @Contract(pure = true)
    public static @NotNull Predicate<String> isNotSystemSchema() {
        return schema -> !(schema.startsWith("db_") || SYSTEM_SCHEMAS.contains(schema));
    }

    @Contract(pure = true)
    public static @NotNull Predicate<String> isInvalidType() {
        return INVALID_TYPES::contains;
    }

    public static @NotNull SequencedSet<String> getColumn(
            @NotNull ResultSet rs, @NotNull String columnLabel) throws SQLException {
        return getColumn(rs, columnLabel, $ -> true);
    }

    public static @NotNull SequencedSet<String> getColumn(
            @NotNull ResultSet rs, @NotNull String columnLabel,
            @NotNull Predicate<String> filter) throws SQLException {
        return getColumn(rs, columnLabel, filter, columnLabel);
    }

    public static @NotNull SequencedSet<String> getColumn(
            @NotNull ResultSet rs, String columnLabel,
            @NotNull Predicate<String> filter,
            @NotNull String predicateArgColumnLabel) throws SQLException {
        var values = new LinkedHashSet<String>();
        while (rs.next()) {
            if (filter.test(rs.getString(predicateArgColumnLabel))) {
                values.add(rs.getString(columnLabel));
            }
        }
        return values;
    }
}
