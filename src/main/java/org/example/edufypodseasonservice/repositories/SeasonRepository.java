package org.example.edufypodcastservice.repositories;

import org.example.edufypodcastservice.entities.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, UUID> {
    List<Episode> findAllByPodcast_Id(UUID podcastId);
}
