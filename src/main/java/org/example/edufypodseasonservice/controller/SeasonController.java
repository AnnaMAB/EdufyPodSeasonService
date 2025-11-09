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
@RequestMapping("/podcasts/seasons")
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

    @GetMapping("/allfullseasonsbypodcast/{podcastId}")
    public ResponseEntity<List<SeasonDto>> getAllFullSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getSeasonsByPodcast(podcastId, true));
    }

    @GetMapping("/alllimitedseasonsbypodcast/{podcastId}")
    public ResponseEntity<List<SeasonDto>> getAllLimitedSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getSeasonsByPodcast(podcastId, false));
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

    @PostMapping("/{seasonId}/addepisodes")
    public ResponseEntity<SeasonDto> addEpisodesToSeason(@PathVariable UUID seasonId, @RequestBody List<UUID> episodeIds) {
        return ResponseEntity.ok(seasonService.addEpisodesToSeason(seasonId, episodeIds));
    }

    @PostMapping("/{seasonId}/addepisodes/{episodeId}")
    public ResponseEntity<SeasonDto> addOneEpisodeToSeason(@PathVariable UUID seasonId,@PathVariable UUID episodeId) {
        return ResponseEntity.ok(seasonService.addOneEpisodeToSeason(seasonId, episodeId));
    }

    @DeleteMapping("/{seasonId}/removeepisodes/{episodeId}")
    public ResponseEntity<SeasonDto> removeOneEpisodeFromSeason(@PathVariable UUID seasonId, @PathVariable UUID episodeId) {
        return ResponseEntity.ok(seasonService.removeOneEpisodeFromSeason(seasonId, episodeId));
    }

    @DeleteMapping("/{seasonId}/removeepisodes")
    public ResponseEntity<SeasonDto> removeEpisodesFromSeason(@PathVariable UUID seasonId, @RequestBody List<UUID> episodeIds) {
        return ResponseEntity.ok(seasonService.removeEpisodesFromSeason(seasonId, episodeIds));
    }

}
