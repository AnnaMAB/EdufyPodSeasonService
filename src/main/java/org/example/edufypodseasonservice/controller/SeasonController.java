package org.example.edufypodproducerservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("/podcasts")
public class ProducerController {

    private final PodcastServiceImpl podcastService;
    private final EpisodeServiceImpl episodeService;
    private final GenreServiceImpl genreService;

    @Autowired
    public ProducerController(PodcastServiceImpl podcastService, EpisodeServiceImpl episodeService, GenreServiceImpl genreService) {
        this.podcastService = podcastService;
        this.episodeService = episodeService;
        this.genreService = genreService;
    }



    @PostMapping("/addpodcast")
    public ResponseEntity<Podcast> addPodcast(@RequestBody PodcastDto podcastDto) {
        return ResponseEntity.ok(podcastService.addPodcast(podcastDto));
    }


    @PutMapping("/updatepodcast")
    public ResponseEntity<Podcast> updatePodcast(@RequestBody PodcastDto podcastDto) {
        return ResponseEntity.ok(podcastService.updatePodcast(podcastDto));
    }


    @DeleteMapping("/deletepodcast/{id}")
    public ResponseEntity<String> deletePodcast(@PathVariable UUID id) {
        return ResponseEntity.ok(podcastService.deletePodcast(id));
    }


    @PostMapping("/addepisode")
    public ResponseEntity<Episode> addEpisode(@RequestBody EpisodeDto episodeDto) {
        return ResponseEntity.ok(episodeService.addEpisode(episodeDto));
    }


    @PutMapping("/updateepisode")
    public ResponseEntity<Episode> updateEpisode(@RequestBody EpisodeDto episodeDto) {
        return ResponseEntity.ok(episodeService.updateEpisode(episodeDto));
    }


    @DeleteMapping("/deleteepisode/{id}")
    public ResponseEntity<String> deleteEpisode(@PathVariable UUID id) {
        return ResponseEntity.ok(episodeService.deleteEpisode(id));
    }


    @PostMapping("/addgenre")
    public ResponseEntity<Genre> addGenre(@RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreService.addGenre(genreDto));
    }


    @PutMapping("/updategenre")
    public ResponseEntity<Genre> updateGenre(@RequestBody GenreDto genreDto) {
        return ResponseEntity.ok(genreService.updateGenre(genreDto));
    }


    @DeleteMapping("/deletegenre/{id}")
    public ResponseEntity<String> deleteGenre(@PathVariable UUID id) {
        return ResponseEntity.ok(genreService.deleteGenre(id));
    }

}
