package org.example.edufypodproducerservice.services;


import org.example.edufypodcastservice.dto.EpisodeDto;
import org.example.edufypodcastservice.entities.Episode;

import java.util.List;
import java.util.UUID;

public interface SeasonService {

    Episode addEpisode(EpisodeDto episodeDto);
    Episode updateEpisode(EpisodeDto episodeDto);
    String deleteEpisode(UUID episodeId);
    EpisodeDto getEpisode(UUID episodeId);
    List<EpisodeDto> getAllEpisodes();
    List<EpisodeDto> getEpisodesByPodcastId(UUID podcastId);




}
