package org.example.edufypodseasonservice.external;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.edufypodseasonservice.converters.UserInfo;
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
    private final UserInfo userInfo;
    private static final Logger F_LOG = LogManager.getLogger("functionality");

    @Autowired
    public EpisodeApiClient(RestClient.Builder restClientBuilder, UserInfo userInfo) {
        this.restClient = restClientBuilder.build();
        this.userInfo = userInfo;
    }

    public Boolean episodeExists(UUID episodeId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Boolean>  episodeExistsResponse = restClient.get()
                    .uri(episodeExistsApiUrl, episodeId)
                    .retrieve()
                    .toEntity(Boolean.class);
            if (episodeExistsResponse.getStatusCode().is2xxSuccessful() && episodeExistsResponse.getBody() != null) {
                F_LOG.info("{} successfully checked if episode exists.", role);
                return episodeExistsResponse.getBody();
            } else {
                F_LOG.warn("{}: Episod exist check failed: {}", role, episodeExistsResponse.getStatusCode());
                throw new IllegalStateException(
                        episodeExistsResponse.getStatusCode().toString());
            }
        } catch (RestClientException e) {
            F_LOG.warn("{}: Episod exist check failed: {}", role, e.getMessage());
            throw new IllegalStateException("Failed to check episode " + episodeId, e);
        }
    }


    public void removeSeasonFromEpisode(UUID episodeId, UUID seasonId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(episodeRemoveApiUrl, episodeId, seasonId)
                    .retrieve()
                    .toBodilessEntity();
            if (response.getStatusCode().is2xxSuccessful()) {
                F_LOG.info("{} successfully removed season from episode.", role);
            } else {
                F_LOG.warn("{}: Failed to remove season from episode. error: {}", role, response.getStatusCode());
                throw new IllegalStateException(
                        response.getStatusCode().toString());
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                F_LOG.warn("{}: Failed to remove season from episode. error: {}", role, message);
                throw new IllegalStateException(
                        String.format("Failed to remove episode. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                F_LOG.warn("{}: Failed to remove season from episode. error: {}", role, parseEx.getMessage());
                throw new IllegalStateException("Failed to remove episode. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            F_LOG.warn("{}: Failed to remove season from episode. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Could not connect to episode service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            F_LOG.warn("{}: Failed to remove season from episode. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Unexpected error calling episode service", ex);
        }
    }


    public void addSeasonToEpisode(UUID episodeId, UUID seasonId) {
        String role = userInfo.getRole();
        try {
            ResponseEntity<Void> response = restClient.put()
                    .uri(episodeAddApiUrl, episodeId, seasonId)
                    .retrieve()
                    .toBodilessEntity();
            if (response.getStatusCode().is2xxSuccessful()) {
                F_LOG.info("{} successfully added season to episode.", role);
            } else {
                F_LOG.warn("{}: Failed to add season to episode. error: {}", role, response.getStatusCode());
                throw new IllegalStateException(
                        response.getStatusCode().toString());
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            String body = e.getResponseBodyAsString();
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                String message = json.path("message").asText();
                String path = json.path("path").asText();

                F_LOG.warn("{}: Failed to add season to episode. error: {}", role, message);
                throw new IllegalStateException(
                        String.format("Failed to add episode. Status %s, %s, Path:%s",
                                status, message, path), e);
            } catch (IOException parseEx) {
                F_LOG.warn("{}: Failed to add season to episode. error: {}", role, parseEx.getMessage());
                throw new IllegalStateException("Failed to add episode. Status=" + status + " body=" + body, e);
            }
        } catch (ResourceAccessException ex) {
            F_LOG.warn("{}: Failed to add season to episode. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Could not connect to episode service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            F_LOG.warn("{}: Failed to add season to episode. error: {}", role, ex.getMessage());
            throw new IllegalStateException("Unexpected error calling episode service", ex);
        }
    }


}
