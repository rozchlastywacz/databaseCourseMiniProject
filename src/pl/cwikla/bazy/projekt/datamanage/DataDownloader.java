package pl.cwikla.bazy.projekt.datamanage;

import jdk.incubator.http.HttpClient;
import jdk.incubator.http.HttpRequest;
import jdk.incubator.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DataDownloader {
    private static final String DATA_ITALY = "https://raw.githubusercontent.com/pcm-dpc/COVID-19/master/dati-province/dpc-covid19-ita-province-";
    private static final String DATA_USA = "https://raw.githubusercontent.com/nytimes/covid-19-data/master/us-counties.csv";
    private final HttpClient client;

    public DataDownloader() {
        client = HttpClient.newBuilder().build();
    }

    public InputStream downloadDataForItaly(LocalDate date) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(
                        DATA_ITALY
                                + date.format(DateTimeFormatter.BASIC_ISO_DATE)
                                + ".csv"))
                .build();
        var response = client.send(request, HttpResponse.BodyHandler.asInputStream());
        return response.body();
    }
    public InputStream downloadDataForUSA() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(URI.create(DATA_USA))
                .build();
        var response = client.send(request, HttpResponse.BodyHandler.asInputStream());
        return response.body();
    }
}