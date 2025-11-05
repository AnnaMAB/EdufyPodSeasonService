package org.example.edufypodseasonservice.mapper;


import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.springframework.stereotype.Component;

@Component
public class SeasonDtoConverter {

    public SeasonDto SeasonFullDtoConvert(Season season) {
        SeasonDto seasonDto = new SeasonDto();
        return seasonDto;
    }

    public SeasonDto SeasonLimitedDtoConvert(Season season) {
        SeasonDto seasonDto = new SeasonDto();
        return seasonDto;
    }

}
