package com.example.demo.repository;

import com.example.demo.util.Table;
import com.example.demo.util.XmlQuery;
import com.example.demo.util.XmlQuery.XmlQueryBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static com.example.demo.util.DatabaseUtils.*;

@Setter
@Repository
public class DatabaseRepository implements AutoCloseable {
    private final Connection conn;
    private final DatabaseMetaData metaData;
    private final XmlQueryBuilder queryBuilder;
    @Getter
    private volatile String catalog;
    @Getter
    private volatile String schema;

    @Autowired
    public DatabaseRepository(@NotNull DataSource dataSource) throws SQLException {
        this.conn = dataSource.getConnection();
        this.metaData = conn.getMetaData();
        this.queryBuilder = XmlQuery.builder().query(XmlQuery.FOR_PATH);
    }

    public @NotNull String queryForXmlString(@NotNull Table table) {
        return queryForXmlString(
                queryBuilder.columnNames(table.columnNames())
                        .catalogName(catalog)
                        .schemaName(schema)
                        .tableName(table.tableName())
                        .build());
    }

    private @NotNull String queryForXmlString(@NotNull XmlQuery query) {
        try (var stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(query.toString())) {
            if (rs.next()) {
                return Objects.requireNonNull(rs.getSQLXML(1),
                        "NULL RESULT SET!").getString();
            }
        } catch (NullPointerException e) {
            return "EMPTY";
        } catch (SQLException e) {
            e.forEach(Throwable::printStackTrace);
        }
        throw new RuntimeException("ERROR! Bad query:\n" + query);
    }

    @Contract(pure = true)
    public @NotNull Set<String> getCatalogNames() {
        try (ResultSet rs = metaData.getCatalogs()) {
            return getColumn(rs, "TABLE_CAT", isNotSystemCatalog());
        } catch (SQLException e) {
            e.forEach(Throwable::printStackTrace);
            throw new RuntimeException(e);
        }
    }

    @Contract(pure = true)
    public @NotNull Set<String> getSchemaNames(@NotNull String catalogName) {
        try (ResultSet rs = metaData.getSchemas(catalogName, null)) {
            return getColumn(rs, "TABLE_SCHEM", isNotSystemSchema()
                    .and(schemaOfCatalogHasTable(catalogName)
                            .or(schemaOfCatalogHasView(catalogName))));
        } catch (SQLException e) {
            e.forEach(Throwable::printStackTrace);
            throw new RuntimeException(e);
        }
    }

    public @NotNull Set<String> getTableNames(@NotNull String catalogName,
                                              @NotNull String schemaName) {
        return getTableNamesOfType("TABLE", catalogName, schemaName);
    }

    public @NotNull Set<String> getViewNames(@NotNull String catalogName,
                                             @NotNull String schemaName) {
        return getTableNamesOfType("VIEW", catalogName, schemaName);
    }

    private @NotNull Set<String> getTableNamesOfType(@NotNull String type,
                                                     @NotNull String catalogName,
                                                     @NotNull String schemaName) {
        try (ResultSet rs = metaData.getTables(catalogName, schemaName,
                DEFAULT_TABLE_NAME_PATTERN, new String[]{type})) {
            return getColumn(rs, "TABLE_NAME");
        } catch (SQLException e) {
            e.forEach(Throwable::printStackTrace);
            throw new RuntimeException(e);
        }
    }

    public @NotNull Set<String> getColumnNamesOfCurrentCatalogAndSchema
            (@NotNull String tableName) {
        try (ResultSet rs = metaData.getColumns(catalog, schema,
                tableName, DEFAULT_COLUMN_NAME_PATTERN)) {
            return getColumn(rs, "COLUMN_NAME", isInvalidType().negate(),
                    "TYPE_NAME");
        } catch (SQLException e) {
            e.forEach(Throwable::printStackTrace);
            throw new RuntimeException(e);
        }
    }

    @Contract(pure = true)
    private @NotNull Predicate<String> schemaOfCatalogHasView(@NotNull String catalogName) {
        return schemaName -> !getViewNames(catalogName, schemaName).isEmpty();
    }

    @Contract(pure = true)
    private @NotNull Predicate<String> schemaOfCatalogHasTable(@NotNull String catalogName) {
        return schemaName -> !getTableNames(catalogName, schemaName).isEmpty();
    }

    @Override
    public void close() throws SQLException {
        conn.close();
    }
}
