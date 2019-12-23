package net.devdiaries.apartmentsscraper.scrapers;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Function;

public class DocumentExtractor implements Function<String, Document> {

    @Override
    public Document apply(String s) {
        try {
            return Jsoup.connect(s).get();
        } catch (IOException e) {
            throw new RuntimeException("Cannot connect to: " + s, e.getCause());
        }
    }
}
