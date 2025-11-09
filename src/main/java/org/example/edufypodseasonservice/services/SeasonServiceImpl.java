package org.example.edufypodseasonservice.services;


import jakarta.transaction.Transactional;
import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
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

    @Autowired
    public SeasonServiceImpl(SeasonRepository seasonRepository, SeasonDtoConverter seasonDtoConverter) {
        this.seasonRepository = seasonRepository;
        this.seasonDtoConverter = seasonDtoConverter;
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
        return null;
    }

    @Override
    public SeasonDto getLatestSeason(UUID podcastId) {
        return null;
    }

    @Transactional
    @Override
    public Season addSeason(SeasonDto seasonDto) {
        return null;
    }

    @Transactional
    @Override
    public Season updateSeason(SeasonDto seasonDto) {
        return null;
    }

    @Transactional
    @Override
    public String deleteSeason(UUID seasonId) {
        return "";
    }
}
