package org.example.edufypodcastservice.services;

import jakarta.transaction.Transactional;
import org.example.edufypodcastservice.dto.EpisodeDto;
import org.example.edufypodcastservice.entities.Episode;
import org.example.edufypodcastservice.entities.Podcast;
import org.example.edufypodcastservice.mapper.EpisodeDtoConverter;
import org.example.edufypodcastservice.repositories.EpisodeRepository;
import org.example.edufypodcastservice.repositories.PodcastRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Service
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final EpisodeDtoConverter episodeDtoConverter;
    private final PodcastRepository podcastRepository;

    @Autowired
    public EpisodeServiceImpl(EpisodeRepository episodeRepository, EpisodeDtoConverter episodeDtoConverter, PodcastRepository podcastRepository) {
        this.episodeRepository = episodeRepository;
        this.episodeDtoConverter = episodeDtoConverter;
        this.podcastRepository = podcastRepository;
    }

    @Transactional
    @Override
    public Episode addEpisode(EpisodeDto episodeDto) {
        Episode episode = new Episode();
        if (episodeDto.getTitle() == null || episodeDto.getTitle().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Title is required");
        }
        if (episodeDto.getUrl() == null || episodeDto.getUrl().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Url is required");
        }
        if (episodeDto.getDescription() == null || episodeDto.getDescription().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
        }
        if (episodeDto.getDurationSeconds() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration is required");
        }
        if (episodeDto.getPodcast() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PodcastId is required");
        }
        if (episodeDto.getThumbnailUrl() != null && !episodeDto.getThumbnailUrl().isEmpty()) {
            episode.setThumbnailUrl(episodeDto.getThumbnailUrl());
        }
        if (episodeDto.getImageUrl() != null && !episodeDto.getImageUrl().isEmpty()) {
            episode.setImageUrl(episodeDto.getImageUrl());
        }
        if (episodeDto.getPodcastId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PodcastId is required");
        }
        Podcast podcast = podcastRepository.findById(episodeDto.getPodcastId()).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No podcast exists with id: %s.", episodeDto.getPodcast())
            );
        });
        episode.setTitle(episodeDto.getTitle());
        episode.setUrl(episodeDto.getUrl());
        episode.setDescription(episodeDto.getDescription());
        episode.setDurationSeconds(episodeDto.getDurationSeconds());
        episode.setReleaseDate(LocalDate.now());
        episode.setPodcast(podcast);
        podcast.getEpisodes().add(episode);
        return episodeRepository.save(episode);
    }

    @Transactional
    @Override
    public Episode updateEpisode(EpisodeDto episodeDto) {
        if(episodeDto.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Episode id is required");
        }
        Episode episode = episodeRepository.findById(episodeDto.getId()).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No episode exists with id: %s.", episodeDto.getId())
            );
        });

        if (episodeDto.getTitle() != null && !episodeDto.getTitle().equals(episode.getTitle())) {
            if(episodeDto.getTitle().isEmpty()) {
               // F_LOG.warn("{} tried to update a workout with invalid title.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Title can not be left blank."
                );
            }
            episode.setTitle(episodeDto.getTitle());
        }
        if (episodeDto.getUrl() != null && !episodeDto.getUrl().equals(episode.getUrl())) {
            if(episodeDto.getUrl().isEmpty()) {
               // F_LOG.warn("{} tried to update a workout with invalid title.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Url can not be left blank."
                );
            }
            episode.setUrl(episodeDto.getUrl());
        }
        if (episodeDto.getDescription() != null && !episodeDto.getDescription().equals(episode.getDescription())) {
            if(episodeDto.getDescription().isEmpty()) {
               // F_LOG.warn("{} tried to update a workout with invalid title.", role);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Description can not be left blank."
                );
            }
            episode.setDescription(episodeDto.getDescription());
        }
        if (episodeDto.getDurationSeconds() != null && !episodeDto.getDurationSeconds().equals(episode.getDurationSeconds())) {
            episode.setDurationSeconds(episodeDto.getDurationSeconds());
        }
        if (episodeDto.getReleaseDate() != null && !episodeDto.getReleaseDate().equals(episode.getReleaseDate())) {
            // F_LOG.warn("{} tried to update a workout with invalid title.", role);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Release date can not be changed."
            );
        }
        if (episodeDto.getThumbnailUrl() != null && !episodeDto.getThumbnailUrl().equals(episode.getThumbnailUrl())) {
            episode.setThumbnailUrl(episodeDto.getThumbnailUrl());
        }
        if (episodeDto.getImageUrl() != null && !episodeDto.getImageUrl().equals(episode.getImageUrl())) {
            episode.setImageUrl(episodeDto.getImageUrl());
        }
        if (episodeDto.getPodcastId() != null && !episodeDto.getPodcastId().equals(episode.getPodcast().getId())) {
            episode.getPodcast().getEpisodes().remove(episode);
            Podcast podcast = podcastRepository.findById(episodeDto.getPodcastId()).orElseThrow(() -> {
                //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
                return new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No podcast exists with id: %s.", episodeDto.getPodcast())
                );
            });
            episode.setPodcast(podcast);
            podcast.getEpisodes().add(episode);
        }
        return episodeRepository.save(episode);
    }

    @Transactional
    @Override
    public String deleteEpisode(UUID episodeId) {
        if (episodeId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No episode exists with id: %s.", episodeId)
            );
        }
        episodeRepository.deleteById(episodeId);
        return String.format("Episode with Id: %s has been successfully deleted.", episodeId);
    }

    @Override
    public EpisodeDto getEpisode(UUID episodeId) {
        if (episodeId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Id must be provided"
            );
        }
        Episode episode = episodeRepository.findById(episodeId).orElseThrow(() -> {
            //   F_LOG.warn("{} tried to book a workout with id {} that doesn't exist.", role, workoutToBook.getId());
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("No episode exists with id: %s.", episodeId)
            );
        });
        return episodeDtoConverter.convertToFullEpisodeDto(episode);
    }

    @Override
    public List<EpisodeDto> getAllEpisodes() {
        List<Episode> episodes = episodeRepository.findAll();
        List<EpisodeDto> episodeDtos = new ArrayList<>();
        for (Episode episode : episodes) {
            episodeDtos.add(episodeDtoConverter.convertToFullEpisodeDto(episode));
        }
        return episodeDtos;
    }

    @Override
    public List<EpisodeDto> getEpisodesByPodcastId(UUID podcastId) {
        List<Episode> episodes = episodeRepository.findAllByPodcast_Id(podcastId);
        List<EpisodeDto> episodeDtos = new ArrayList<>();
        for (Episode episode : episodes) {
            episodeDtos.add(episodeDtoConverter.convertToLimitedEpisodeDto(episode));
        }
        return episodeDtos;
    }
}
