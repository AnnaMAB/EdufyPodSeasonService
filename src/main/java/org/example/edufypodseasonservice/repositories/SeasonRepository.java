package org.example.edufypodseasonservice.repositories;


import org.example.edufypodseasonservice.entities.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonRepository extends JpaRepository<Season, UUID> {

    boolean existsByPodcastIdAndSeasonNumber(UUID podcastId, Integer seasonNumber);
    List<Season> findAllByOrderByPodcastIdAscSeasonNumberAsc();
    List<Season> findByPodcastIdOrderBySeasonNumberAsc(UUID podcastId);
    Optional<Season> findFirstByPodcastIdOrderBySeasonNumberAsc(UUID podcastId); // first
    Optional<Season> findFirstByPodcastIdOrderBySeasonNumberDesc(UUID podcastId); // latest
}
