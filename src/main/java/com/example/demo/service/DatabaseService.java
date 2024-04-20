package com.example.demo.service;

import com.example.demo.repository.DatabaseRepository;
import com.example.demo.util.Table;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

import static com.example.demo.util.CollectionUtils.getFilteredMapExcludingEmptyValues;
import static com.example.demo.util.CollectionUtils.mapToValueSet;

@Service
@RequiredArgsConstructor
public class DatabaseService {
    private final DatabaseRepository databaseRepository;

    public void setCatalog(String catalog) {
        databaseRepository.setCatalog(catalog);
    }

    public void setSchema(String schema) {
        databaseRepository.setSchema(schema);
    }

    public @NotNull Map<String, Set<String>> getCatalogSchemasMap() {
        return getFilteredMapExcludingEmptyValues(
                this::getCatalogNames, mapToValueSet(this::getSchemaNames));
    }

    public @NotNull Map<String, Set<String>> getSchemaTablesMapOfCurrentCatalog() {
        return getFilteredMapExcludingEmptyValues(
                this::getSchemaNamesOfCurrentCatalog,
                mapToValueSet(this::getTableNamesOfCurrentCatalog));
    }

    public @NotNull Map<String, Set<String>> getSchemaViewsMapOfCurrentCatalog() {
        return getFilteredMapExcludingEmptyValues(
                this::getSchemaNamesOfCurrentCatalog,
                mapToValueSet(this::getViewNamesOfCurrentCatalog));
    }

    public @NotNull String queryForXmlString(@NotNull Table table) {
        return databaseRepository.queryForXmlString(table);
    }

    public @NotNull Set<String> getCatalogNames() {
        return databaseRepository.getCatalogNames();
    }

    public @NotNull Set<String> getSchemaNamesOfCurrentCatalog() {
        return getSchemaNames(databaseRepository.getCatalog());
    }

    public @NotNull Set<String> getSchemaNames(@NotNull String catalogName) {
        return databaseRepository.getSchemaNames(catalogName);
    }

    public @NotNull Set<String> getTableNamesOfCurrentCatalog(@NotNull String schemaName) {
        return databaseRepository.getTableNames(
                databaseRepository.getCatalog(), schemaName);
    }

    public @NotNull Set<String> getViewNamesOfCurrentCatalog(@NotNull String schemaName) {
        return databaseRepository.getViewNames(
                databaseRepository.getCatalog(), schemaName);
    }

    public @NotNull Set<String> getColumnNamesOfCurrentCatalogAndSchema(String tableName) {
        return databaseRepository.getColumnNamesOfCurrentCatalogAndSchema(tableName);
    }
}
