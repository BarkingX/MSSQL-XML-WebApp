package com.example.demo.util;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public record Table(String tableName, Set<String> columnNames) {
    public Table {
        Objects.requireNonNull(tableName);
        columnNames = Collections.unmodifiableSet(columnNames);
    }
}
