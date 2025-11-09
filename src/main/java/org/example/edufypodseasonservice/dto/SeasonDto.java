package org.example.edufypodseasonservice.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class SeasonDto {

    private UUID id;
    private String name;
    private Integer seasonNumber;
    private String description;
    private List<UUID> episodes = new ArrayList<>();
    private UUID podcastId;
    private String thumbnailUrl;
    private String imageUrl;

    public SeasonDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UUID> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<UUID> episodes) {
        this.episodes = episodes;
    }

    public UUID getPodcastId() {
        return podcastId;
    }

    public void setPodcastId(UUID podcastId) {
        this.podcastId = podcastId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "SeasonDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", seasonNumber=" + seasonNumber +
                ", description='" + description + '\'' +
                ", episodes=" + episodes +
                ", podcastId=" + podcastId +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
