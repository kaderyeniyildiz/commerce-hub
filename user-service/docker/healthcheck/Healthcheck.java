package com.devlab.userservice.healthcheck;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Healthcheck {

    public static void main(String[] args) {
        try {
            HttpClient client = HttpClient.newBuilder()
                                          .connectTimeout(Duration.ofSeconds(3))
                                          .build();

            HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("http://localhost:8080/actuator/health"))
                                             .timeout(Duration.ofSeconds(5))
                                             .GET()
                                             .build();

            HttpResponse<Void> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.discarding()
            );

            if (response.statusCode() != 200) {
                System.exit(1);
            }

        } catch (Exception exception) {
            System.exit(1);
        }
    }
}
