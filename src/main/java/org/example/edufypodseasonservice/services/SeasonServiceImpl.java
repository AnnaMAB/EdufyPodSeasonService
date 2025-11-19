package org.example.edufypodseasonservice.services;


import jakarta.transaction.Transactional;
import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.example.edufypodseasonservice.external.EpisodeApiClient;
import org.example.edufypodseasonservice.mapper.SeasonDtoConverter;
import org.example.edufypodseasonservice.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;
    private final SeasonDtoConverter seasonDtoConverter;
    private final EpisodeApiClient episodeApiClient;

    @Autowired
    public SeasonServiceImpl(SeasonRepository seasonRepository, SeasonDtoConverter seasonDtoConverter, EpisodeApiClient episodeApiClient) {
        this.seasonRepository = seasonRepository;
        this.seasonDtoConverter = seasonDtoConverter;
        this.episodeApiClient = episodeApiClient;
    }

    @Override
    public SeasonDto getSeason(UUID seasonId) {
        if (seasonId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Season season = seasonRepository.findById(seasonId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No season exists with id: %s.", seasonId)
            );
        });
        return seasonDtoConverter.seasonFullDtoConvert(season);
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        List<Season> seasons = seasonRepository.findAllByOrderByPodcastIdAscSeasonNumberAsc();
        List<SeasonDto> seasonDtos = new ArrayList<>();
        for (Season season : seasons) {
            seasonDtos.add(seasonDtoConverter.seasonLimitedDtoConvert(season));
        }
        return seasonDtos;
    }

    @Override
    public List<SeasonDto> getSeasonsByPodcast(UUID podcastId, boolean full) {
        if (podcastId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PodcastId must be provided"
            );
        }
        List<Season> seasons = seasonRepository.findByPodcastIdOrderBySeasonNumberAsc(podcastId);
        List<SeasonDto> seasonDtos = new ArrayList<>();
        if (full) {
            for (Season season : seasons) {
                seasonDtos.add(seasonDtoConverter.seasonFullDtoConvert(season));
            }
        }else {
            for (Season season : seasons) {
                seasonDtos.add(seasonDtoConverter.seasonLimitedDtoConvert(season));
            }
        }
        return seasonDtos;
    }

    @Override
    public SeasonDto getFirstSeason(UUID podcastId) {
        if (podcastId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PodcastId must be provided"
            );
        }
        Season season = seasonRepository.findFirstByPodcastIdOrderBySeasonNumberAsc(podcastId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No season exists for podcastId: %s.", podcastId)
            );
        });
        return seasonDtoConverter.seasonFullDtoConvert(season);
    }

    @Override
    public SeasonDto getLatestSeason(UUID podcastId) {
        if (podcastId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PodcastId must be provided"
            );
        }
        Season season = seasonRepository.findFirstByPodcastIdOrderBySeasonNumberDesc(podcastId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No season exists for podcastId: %s.", podcastId)
            );
        });
        return seasonDtoConverter.seasonFullDtoConvert(season);
    }

    @Transactional
    @Override
    public Season addSeason(SeasonDto seasonDto) {
        Season season = new Season();
        if (seasonDto.getName() == null || seasonDto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (seasonDto.getPodcastId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PodcastId is required");
        }
        if (seasonDto.getSeasonNumber() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season number is required");
        }
        if(seasonRepository.existsByPodcastIdAndSeasonNumber(seasonDto.getPodcastId(), seasonDto.getSeasonNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season with that number already exists");
        }
        if (seasonDto.getDescription() != null || !seasonDto.getDescription().isBlank()) {
            season.setDescription(seasonDto.getDescription());
        }
        if (seasonDto.getImageUrl() != null && !seasonDto.getImageUrl().isBlank()) {
            season.setImageUrl(seasonDto.getImageUrl());
        }else {
            season.setImageUrl("https://default/image.url");
        }
        if (seasonDto.getThumbnailUrl() != null && !seasonDto.getThumbnailUrl().isBlank()) {
            season.setThumbnailUrl(seasonDto.getThumbnailUrl());
        }else {
            season.setThumbnailUrl("https://default/thumbnail.url");
        }
        if (seasonDto.getEpisodes() != null && !seasonDto.getEpisodes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episodes can't be added from this endpoint");
        }
        season.setName(seasonDto.getName());
        season.setPodcastId(seasonDto.getPodcastId());
        season.setSeasonNumber(seasonDto.getSeasonNumber());
        return seasonRepository.save(season);
    }

    @Transactional
    @Override
    public Season updateSeason(SeasonDto seasonDto) {
        if(seasonDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SeasonId is required");
        }
        Season season = seasonRepository.findById(seasonDto.getId()).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No season exists with id: %s.", seasonDto.getId())
            );
        });
        if (seasonDto.getName() != null && !seasonDto.getName().equals(season.getName())) {
            if(seasonDto.getName().isBlank()) {
                // F_LOG.warn("{} tried to update a workout with invalid title.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Name can not be left blank."
                );
            }
            season.setName(seasonDto.getName());
        }
        if (seasonDto.getPodcastId() != null && !seasonDto.getPodcastId().equals(season.getPodcastId())) {
            season.setPodcastId(seasonDto.getPodcastId());
        }
        if (seasonDto.getSeasonNumber() != null && !seasonDto.getSeasonNumber().equals(season.getSeasonNumber())) {
            if(seasonRepository.existsByPodcastIdAndSeasonNumber(season.getPodcastId(), seasonDto.getSeasonNumber())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season with that number already exists");
            }
            season.setSeasonNumber(seasonDto.getSeasonNumber());
        }
        if (seasonDto.getDescription() != null && !seasonDto.getDescription().equals(season.getDescription())) {
            season.setDescription(seasonDto.getDescription());
        }
        if (seasonDto.getImageUrl() != null && !seasonDto.getImageUrl().equals(season.getImageUrl())) {
            season.setImageUrl(seasonDto.getImageUrl());
        }
        if (seasonDto.getThumbnailUrl() != null && !seasonDto.getThumbnailUrl().equals(season.getThumbnailUrl())) {
            season.setThumbnailUrl(seasonDto.getThumbnailUrl());
        }
        if (seasonDto.getEpisodes() != null && !seasonDto.getEpisodes().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episodes can't be added from this endpoint");
        }
        return seasonRepository.save(season);
    }

    @Transactional
    @Override
    public String deleteSeason(UUID seasonId) {
        if (seasonId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Season season = seasonRepository.findById(seasonId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No season exists with ID: %s", seasonId)));
        if (!season.getEpisodes().isEmpty()){
            for (UUID episodeId : season.getEpisodes()) {
                episodeApiClient.removeSeasonFromEpisode(episodeId, seasonId);
            }
        }
        seasonRepository.deleteById(seasonId);
        return String.format("Season with Id: %s have been successfully deleted and episodes removed.", seasonId);
    }

    @Transactional
    @Override
    public SeasonDto addEpisodesToSeason(UUID seasonId, List<UUID> episodeIds) {
        if (seasonId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season ID must be provided");
        }
        if (episodeIds == null || episodeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one episode ID must be provided");
        }

        Season season = seasonRepository.findById(seasonId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No season exists with ID: %s", seasonId)));
        List<UUID> currentEpisodes = season.getEpisodes();
        for (UUID episodeId : episodeIds) {
            if (!currentEpisodes.contains(episodeId)) {
                currentEpisodes.add(episodeId);
                episodeApiClient.addSeasonToEpisode(episodeId, seasonId);
            }
        }

        season.setEpisodes(currentEpisodes);
        Season saved = seasonRepository.save(season);

        return seasonDtoConverter.seasonFullDtoConvert(saved);
    }

    @Transactional
    @Override
    public SeasonDto addOneEpisodeToSeason(UUID seasonId, UUID episodeId) {
        if (seasonId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season ID must be provided");
        }
        if (episodeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episode ID must be provided");
        }
        if (!episodeApiClient.episodeExists(episodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Episode " + episodeId + " not found");
        }

        Season season = seasonRepository.findById(seasonId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No season exists with ID: %s", seasonId)));
        List<UUID> episodes = season.getEpisodes();
        if (episodes.contains(episodeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Episode %s already exists in season %s", episodeId, seasonId));
        }
        episodes.add(episodeId);
        season.setEpisodes(episodes);
        episodeApiClient.addSeasonToEpisode(episodeId, seasonId);
        Season saved = seasonRepository.save(season);

        return seasonDtoConverter.seasonFullDtoConvert(saved);
    }

    @Transactional
    @Override
    public SeasonDto removeEpisodesFromSeason(UUID seasonId, List<UUID> episodeIds) {
        if (seasonId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season ID must be provided");
        }
        if (episodeIds == null || episodeIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one episode ID must be provided");
        }
        Season season = seasonRepository.findById(seasonId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No season exists with ID: %s", seasonId)));
        List<UUID> currentEpisodes = season.getEpisodes();
        for (UUID episodeId : episodeIds) {
            if (currentEpisodes.contains(episodeId)) {
                currentEpisodes.remove(episodeId);
                System.out.println(episodeId);
                episodeApiClient.removeSeasonFromEpisode(episodeId, seasonId);
            }
        }
        season.setEpisodes(currentEpisodes);
        Season saved = seasonRepository.save(season);

        return seasonDtoConverter.seasonFullDtoConvert(saved);
    }


    @Transactional
    @Override
    public SeasonDto removeOneEpisodeFromSeason(UUID seasonId, UUID episodeId) {
        if (seasonId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Season ID must be provided");
        }
        if (episodeId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episode ID must be provided");
        }
        if (!episodeApiClient.episodeExists(episodeId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Episode " + episodeId + " not found");
        }
        Season season = seasonRepository.findById(seasonId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("No season exists with ID: %s", seasonId)));
        List<UUID> episodes = season.getEpisodes();
        if (!episodes.contains(episodeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Episode %s dosen't exists in season %s", episodeId, seasonId));
        }
        episodes.remove(episodeId);
        season.setEpisodes(episodes);
        episodeApiClient.removeSeasonFromEpisode(episodeId, seasonId);
        Season saved = seasonRepository.save(season);

        return seasonDtoConverter.seasonFullDtoConvert(saved);
    }
}
