package com.example.demo.controller;


import com.example.demo.service.DatabaseService;
import com.example.demo.util.HtmlTransformer;
import com.example.demo.util.Table;
import com.example.demo.util.XsltGenerator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

/**
 * This class is a REST controller that handles AJAX requests.
 */
@RestController
@RequiredArgsConstructor
public class AjaxController {
    private final DatabaseService databaseService;

    /**
     * Returns an HTML table for the given table name.
     *
     * @param tableName the name of the table to convert to HTML
     * @return the HTML representation of the table
     */
    @GetMapping("/api/toHtmlTable")
    public @NotNull String toHtmlTable(@RequestParam("tableName") String tableName) {
        Table table = new Table(tableName,
                databaseService.getColumnNamesOfCurrentCatalogAndSchema(tableName));
        return HtmlTransformer.transformToHtml(
                databaseService.queryForXmlString(table),
                () -> XsltGenerator.forHtmlTable(table));
    }

    /**
     * This method retrieves and returns a map of catalog names and their corresponding schema names.
     *
     * @return A map of catalog names and their corresponding schema names
     */
    @GetMapping("/api/getCatalogSchemasMap")
    public @NotNull Map<String, Set<String>> getCatalogSchemasMap() {
        return databaseService.getCatalogSchemasMap();
    }

    /**
     * This method retrieves and returns a map of schema names and their corresponding table names for the current catalog.
     *
     * @return A map of schema names and their corresponding table names
     */
    @GetMapping("/api/getSchemaTablesMap")
    public @NotNull Map<String, Set<String>> getSchemaTablesMapOfCurrentCatalog() {
        return databaseService.getSchemaTablesMapOfCurrentCatalog();
    }

    /**
     * This method retrieves and returns a map of schema names and their corresponding view names for the current catalog.
     *
     * @return A map of schema names and their corresponding view names
     */
    @GetMapping("/api/getSchemaViewsMap")
    public @NotNull Map<String, Set<String>> getSchemaViewsMapOfCurrentCatalog() {
        return databaseService.getSchemaViewsMapOfCurrentCatalog();
    }

    /**
     * This method sets the catalog name for the database service.
     *
     * @param catalogName The name of the catalog to set
     */
    @PostMapping("/api/setCatalog")
    public void setCatalogName(@RequestParam("catalogName") String catalogName) {
        databaseService.setCatalog(catalogName);
    }

    /**
     * This method sets the schema name for the database service.
     *
     * @param schemaName The name of the schema to set
     */
    @PostMapping("/api/setSchema")
    public void setSchema(@RequestParam("schemaName") String schemaName) {
        databaseService.setSchema(schemaName);
    }
}
