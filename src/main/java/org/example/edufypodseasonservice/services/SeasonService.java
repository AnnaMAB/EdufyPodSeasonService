package org.example.edufypodseasonservice.services;


import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;

import java.util.List;
import java.util.UUID;

public interface SeasonService {

    SeasonDto getSeason(UUID seasonId);
    List<SeasonDto> getAllSeasons();
    List<SeasonDto> getSeasonsByPodcast(UUID podcastId, boolean full);
    SeasonDto getFirstSeason(UUID podcastId);
    SeasonDto getLatestSeason(UUID podcastId);

    Season addSeason(SeasonDto seasonDto);
    Season updateSeason(SeasonDto seasonDto);
    String deleteSeason(UUID seasonId);
    SeasonDto addEpisodesToSeason(UUID seasonId, List<UUID> episodeIds);
    SeasonDto addOneEpisodeToSeason(UUID seasonId, UUID episodeId);
    SeasonDto removeEpisodesFromSeason(UUID seasonId, List<UUID> episodeIds);
    SeasonDto removeOneEpisodeFromSeason(UUID seasonId, UUID episodeId);

}
