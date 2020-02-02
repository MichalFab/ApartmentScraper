package net.devdiaries.apartmentsscraper.services.helpers;

import net.devdiaries.apartmentsscraper.exceptions.OffersException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

public class HttpStatusExtractor implements Function<String, Integer> {

    @Override
    public Integer apply(String url) {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode();
        } catch (InterruptedException | IOException e) {
            throw new OffersException("Cannot check if offer is expired", url, e);
        }
    }
}
