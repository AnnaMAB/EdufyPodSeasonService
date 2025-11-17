package org.example.edufypodseasonservice.entities;


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "season_id", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(name = "podcast_id", columnDefinition = "char(36)", nullable = false)
    private UUID podcastId;
    @Column(nullable = false)
    private Integer seasonNumber;
    @Column(length = 500, nullable = true)
    private String description;
    @ElementCollection
    @CollectionTable(name = "season_episode_ids", joinColumns = @JoinColumn(name = "season_id"))
    @Column(name = "episode_id", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private List<UUID> episodes = new ArrayList<>();
    @Column(length = 500)
    private String thumbnailUrl;
    @Column(length = 500)
    private String imageUrl;


    public Season() {
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
        return "Season{" +
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
