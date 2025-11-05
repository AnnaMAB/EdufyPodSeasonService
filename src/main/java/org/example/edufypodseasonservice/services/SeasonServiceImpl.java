package org.example.edufypodseasonservice.services;


import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.example.edufypodseasonservice.repositories.SeasonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class SeasonServiceImpl implements SeasonService {

    private final SeasonRepository seasonRepository;

    @Autowired
    public SeasonServiceImpl(SeasonRepository seasonRepository) {
        this.seasonRepository = seasonRepository;
    }

    @Override
    public SeasonDto getSeason(UUID seasonId) {
        return null;
    }

    @Override
    public List<SeasonDto> getAllSeasons() {
        return List.of();
    }

    @Override
    public List<SeasonDto> getSeasonsByPodcast(UUID podcastId) {
        return List.of();
    }

    @Override
    public SeasonDto getFirstSeason(UUID podcastId) {
        return null;
    }

    @Override
    public SeasonDto getLatestSeason(UUID podcastId) {
        return null;
    }

    @Override
    public Season addSeason(SeasonDto seasonDto) {
        return null;
    }

    @Override
    public Season updateSeason(SeasonDto seasonDto) {
        return null;
    }

    @Override
    public String deleteSeason(UUID seasonId) {
        return "";
    }
}
