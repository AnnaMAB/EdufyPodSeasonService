package org.example.edufypodseasonservice.controller;

import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.example.edufypodseasonservice.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/podcasts")
public class SeasonController {

    private final SeasonService seasonService;

    @Autowired
    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/season")
    public ResponseEntity<SeasonDto> getSeason(UUID seasonId) {
        return ResponseEntity.ok(seasonService.getSeason(seasonId));
    }

    @GetMapping("/allseasons")
    public ResponseEntity<List<SeasonDto>> getAllSeasons() {
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @GetMapping("/allseasonsbypodcast/{podcastId}")
    public ResponseEntity<List<SeasonDto>> getAllSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getSeasonsByPodcast(podcastId));
    }

    @GetMapping("/firstseasonsbypodcast/{podcastId}")
    public ResponseEntity<SeasonDto> getFirstSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getFirstSeason(podcastId));
    }

    @GetMapping("/latestseasonsbypodcast/{podcastId}")
    public ResponseEntity<SeasonDto> getLatestSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getLatestSeason(podcastId));
    }

    @PostMapping("/addseason")
    public ResponseEntity<Season> addSeason(@RequestBody SeasonDto seasonDto) {
        return ResponseEntity.ok(seasonService.addSeason(seasonDto));
    }

    @PutMapping("/updateseason")
    public ResponseEntity<Season> updateSeason(@RequestBody SeasonDto seasonDto) {
        return ResponseEntity.ok(seasonService.updateSeason(seasonDto));
    }

    @DeleteMapping("/deleteseason/{seasonId}")
    public ResponseEntity<String> deleteSeason(@PathVariable UUID seasonId) {
        return ResponseEntity.ok(seasonService.deleteSeason(seasonId));
    }

}
