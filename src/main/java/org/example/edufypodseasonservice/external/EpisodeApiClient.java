package org.example.edufypodseasonservice.external;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
                    .uri(episodeRemoveApiUrl, seasonId, episodeId)
                    .retrieve()
                    .toBodilessEntity();
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException(
                        "Failed to remove episode " + episodeId + " from season " + seasonId +
                                ". Status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to remove episode " + episodeId + " from season " + seasonId, e);
        }
    }

    public void addSeasonToEpisode(UUID episodeId, UUID seasonId) {
        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(episodeAddApiUrl, seasonId, episodeId)
                    .retrieve()
                    .toBodilessEntity();
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IllegalStateException(
                        "Failed to add episode " + episodeId + " to season " + seasonId +
                                ". Status: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new IllegalStateException("Failed to add episode " + episodeId + " to season " + seasonId, e);
        }
    }

}
