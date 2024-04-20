package com.example.demo.util;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static org.springframework.util.StringUtils.trimAllWhitespace;

public class XsltGenerator {
    @Contract(pure = true)
    public static @NotNull String forHtmlTable(@NotNull Table table) {
        return """
                <?xml version="1.0"?>
                <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
                <xsl:template match="/">
                """
                + htmlTable(table)
                + """
                </xsl:template>
                </xsl:stylesheet>
                """;
    }

    @Contract(pure = true)
    private static @NotNull String htmlTable(@NotNull Table table) {
        var builder = new StringBuilder();
        builder.append("""
                <table class="transformedTable">
                <caption class="tableCaption"></caption>
                <thead>
                <tr>""");

        for (String column : table.columnNames()) {
            builder.append("<th>%s</th>".formatted(column));
        }

        builder.append("""
                </tr>
                </thead>
                <tbody>
                <xsl:for-each select="root/row">
                <tr>""");

        for (String column : table.columnNames()) {
            builder.append("<td><xsl:value-of select=\"%s\"/></td>"
                    .formatted(trimAllWhitespace(column)));
        }

        builder.append("""
                </tr>
                </xsl:for-each>
                </tbody>
                </table>
                """);
        return builder.toString();
    }
}

