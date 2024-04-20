package com.example.demo.util;

import org.jetbrains.annotations.NotNull;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.function.Supplier;

public class HtmlTransformer {
    public static final String EMPTY_TABLE = """
            <div class="warning">
                The table is empty.
            </div>
            """;

    public static @NotNull String transformToHtml(
            @NotNull String xml, @NotNull Supplier<String> xsltSupplier) {
        if ("EMPTY".equals(xml)) {
            return HtmlTransformer.EMPTY_TABLE;
        }

        var xmlSource = new StreamSource(new StringReader(xml));
        var xsltSource = new StreamSource(new StringReader(xsltSupplier.get()));
        var html = new StreamResult(new StringWriter());

        transform(xmlSource, xsltSource, html);
        return html.getWriter().toString();
    }

    private static void transform(Source xml, Source xslt, Result html) {
        try {
            var tf = TransformerFactory.newInstance();
            var t = tf.newTransformer(xslt);
            t.transform(xml, html);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
