package org.example.edufypodseasonservice.mapper;


import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.springframework.stereotype.Component;

@Component
public class SeasonDtoConverter {


    public SeasonDto seasonFullDtoConvert(Season season) {
        SeasonDto seasonDto = new SeasonDto();
        seasonDto.setId(season.getId());
        seasonDto.setName(season.getName());
        seasonDto.setSeasonNumber(season.getSeasonNumber());
        seasonDto.setDescription(season.getDescription());
        seasonDto.setEpisodes(season.getEpisodes());
        seasonDto.setPodcastId(season.getPodcastId());
        seasonDto.setThumbnailUrl(season.getThumbnailUrl());
        seasonDto.setImageUrl(season.getImageUrl());
        return seasonDto;
    }

    public SeasonDto seasonLimitedDtoConvert(Season season) {
        SeasonDto seasonDto = new SeasonDto();
        seasonDto.setId(season.getId());
        seasonDto.setName(season.getName());
        seasonDto.setSeasonNumber(season.getSeasonNumber());
        seasonDto.setPodcastId(season.getPodcastId());
        seasonDto.setThumbnailUrl(season.getThumbnailUrl());
        return seasonDto;
    }

}
