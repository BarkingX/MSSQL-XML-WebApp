package com.example.demo.util;

import lombok.Builder;

import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.trimAllWhitespace;

@Builder
public record XmlQuery(String query, Set<String> columnNames,
                       String catalogName, String schemaName, String tableName) {
    public static final String FOR_PATH = """
            DECLARE @xmldata xml
            SET @xmldata=(SELECT TOP 1000 %s FROM [%s].[%s].[%s] FOR XML PATH, ROOT)
            SELECT @xmldata AS returnXml;
            """;

    @Override
    public String toString() {
        var columns = columnNames.stream()
                .map(column -> "[%s] AS [%s]".formatted(column, trimAllWhitespace(column)))
                .collect(Collectors.joining(","));
        return query.formatted(columns, catalogName, schemaName, tableName);
    }
}
