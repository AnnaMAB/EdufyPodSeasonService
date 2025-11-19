package org.example.edufypodseasonservice.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.util.UUID;

@Service
public class EpisodeApiClient {

    private final RestClient restClient;
    @Value("${episodeExists.api.url}")
    private String episodeExistsApiUrl;
    @Value("${episodeAdd.api.url}")
    private String episodeAddApiUrl;
    @Value("${episodeRemove.api.url}")
    private String episodeRemoveApiUrl;

    @Autowired
    public EpisodeApiClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public boolean episodeExists(UUID episodeId) {
        try {
            Boolean episodeExistsResponse = restClient.get()
                    .uri(episodeExistsApiUrl, episodeId)
                    .retrieve()
                    .body(Boolean.class);
            return episodeExistsResponse;
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to check episode " + episodeId, e);
        }
    }


    public void removeSeasonFromEpisode(UUID episodeId, UUID seasonId) {
        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(episodeRemoveApiUrl, episodeId, seasonId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                throw new IllegalStateException(
                        String.format("Failed to remove episode. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                throw new IllegalStateException("Failed to remove episode. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Could not connect to episode service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unexpected error calling episode service", ex);
        }
    }


    public void addSeasonToEpisode(UUID episodeId, UUID seasonId) {
        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(episodeAddApiUrl, episodeId, seasonId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                throw new IllegalStateException(
                        String.format("Failed to add episode. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                throw new IllegalStateException("Failed to add episode. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            throw new IllegalStateException("Could not connect to episode service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Unexpected error calling episode service", ex);
        }
    }


}
