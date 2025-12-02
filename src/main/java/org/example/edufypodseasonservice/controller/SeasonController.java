package org.example.edufypodseasonservice.controller;

import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.example.edufypodseasonservice.services.SeasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/pods/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    @Autowired
    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Adminv')")
    @GetMapping("/season/{seasonId}")
    public ResponseEntity<SeasonDto> getSeason(@PathVariable UUID seasonId) {
        return ResponseEntity.ok(seasonService.getSeason(seasonId));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/allseasons")
    public ResponseEntity<List<SeasonDto>> getAllSeasons() {
        return ResponseEntity.ok(seasonService.getAllSeasons());
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/allfullseasonsbypodcast/{podcastId}")
    public ResponseEntity<List<SeasonDto>> getAllFullSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getSeasonsByPodcast(podcastId, true));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/alllimitedseasonsbypodcast/{podcastId}")
    public ResponseEntity<List<SeasonDto>> getAllLimitedSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getSeasonsByPodcast(podcastId, false));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/firstseasonsbypodcast/{podcastId}")
    public ResponseEntity<SeasonDto> getFirstSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getFirstSeason(podcastId));
    }

    @PreAuthorize("hasAnyRole('edufy_User','edufy_Admin')")
    @GetMapping("/latestseasonsbypodcast/{podcastId}")
    public ResponseEntity<SeasonDto> getLatestSeasonsByPodcast(@PathVariable UUID podcastId) {
        return ResponseEntity.ok(seasonService.getLatestSeason(podcastId));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PostMapping("/addseason")
    public ResponseEntity<Season> addSeason(@RequestBody SeasonDto seasonDto) {
        return ResponseEntity.ok(seasonService.addSeason(seasonDto));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/updateseason")
    public ResponseEntity<Season> updateSeason(@RequestBody SeasonDto seasonDto) {
        return ResponseEntity.ok(seasonService.updateSeason(seasonDto));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @DeleteMapping("/deleteseason/{seasonId}")
    public ResponseEntity<String> deleteSeason(@PathVariable UUID seasonId) {
        return ResponseEntity.ok(seasonService.deleteSeason(seasonId));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/{seasonId}/addepisodes")
    public ResponseEntity<SeasonDto> addEpisodesToSeason(@PathVariable UUID seasonId, @RequestBody List<UUID> episodeIds) {
        return ResponseEntity.ok(seasonService.addEpisodesToSeason(seasonId, episodeIds));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @PutMapping("/{seasonId}/addepisodes/{episodeId}")
    public ResponseEntity<SeasonDto> addOneEpisodeToSeason(@PathVariable UUID seasonId,@PathVariable UUID episodeId) {
        return ResponseEntity.ok(seasonService.addOneEpisodeToSeason(seasonId, episodeId));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @DeleteMapping("/{seasonId}/removeepisodes/{episodeId}")
    public ResponseEntity<SeasonDto> removeOneEpisodeFromSeason(@PathVariable UUID seasonId, @PathVariable UUID episodeId) {
        return ResponseEntity.ok(seasonService.removeOneEpisodeFromSeason(seasonId, episodeId));
    }

    @PreAuthorize("hasRole('edufy_Admin')")
    @DeleteMapping("/{seasonId}/removeepisodes")
    public ResponseEntity<SeasonDto> removeEpisodesFromSeason(@PathVariable UUID seasonId, @RequestBody List<UUID> episodeIds) {
        return ResponseEntity.ok(seasonService.removeEpisodesFromSeason(seasonId, episodeIds));
    }

}
