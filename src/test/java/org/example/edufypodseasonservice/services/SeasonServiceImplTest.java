package org.example.edufypodseasonservice.services;

import org.example.edufypodseasonservice.dto.SeasonDto;
import org.example.edufypodseasonservice.entities.Season;
import org.example.edufypodseasonservice.external.EpisodeApiClient;
import org.example.edufypodseasonservice.mapper.SeasonDtoConverter;
import org.example.edufypodseasonservice.repositories.SeasonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SeasonServiceImplTest {


    @Mock
    private SeasonRepository seasonRepositoryMock;
    @Mock
    private EpisodeApiClient episodeApiClientMock;

    private final SeasonDtoConverter seasonDtoConverter = new SeasonDtoConverter();

    @InjectMocks
    private SeasonServiceImpl seasonService;

    private Season season;
    private SeasonDto seasonDto;


    private final UUID seasonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID podcastId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID episodeId = UUID.fromString("00000000-0000-0000-0000-000000000002");


    @BeforeEach
    void setUp() {
        seasonService = new SeasonServiceImpl(seasonRepositoryMock, seasonDtoConverter, episodeApiClientMock);

        season = new Season();
        season.setId(seasonId);
        season.setName("Test Season");
        season.setPodcastId(podcastId);
        season.setSeasonNumber(1);
        season.setDescription("Description");
        season.setEpisodes(new ArrayList<>());
        season.setImageUrl("image.png");
        season.setThumbnailUrl("thumb.png");

        seasonDto = new SeasonDto();
        seasonDto.setId(seasonId);
        seasonDto.setName("Test Season");
        seasonDto.setPodcastId(podcastId);
        seasonDto.setSeasonNumber(1);
        seasonDto.setDescription("Description");
        seasonDto.setEpisodes(new ArrayList<>());
        seasonDto.setImageUrl("image.png");
        seasonDto.setThumbnailUrl("thumb.png");
    }

    @Test
    void getSeason_ShouldReturnFullDto_WhenSeasonExists() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        SeasonDto result = seasonService.getSeason(seasonId);

        assertThat(result).usingRecursiveComparison()
                .isEqualTo(seasonDtoConverter.seasonFullDtoConvert(season));
        verify(seasonRepositoryMock, times(1)).findById(seasonId);
    }

    @Test
    void getSeason_ShouldThrow_WhenIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.getSeason(null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Id must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void getSeason_ShouldThrow_WhenNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.getSeason(seasonId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals(
                String.format("No season exists with id: %s.", seasonId),
                ex.getReason()
        );
        verify(seasonRepositoryMock, times(1)).findById(seasonId);
    }

    @Test
    void getAllSeasons_ShouldReturnListOfLimitedDtos() {
        Season season2 = new Season();
        season2.setId(UUID.randomUUID());
        season2.setName("Another Season");
        List<Season> allSeasons = Arrays.asList(season, season2);
        when(seasonRepositoryMock.findAllByOrderByPodcastIdAscSeasonNumberAsc()).thenReturn(allSeasons);

        List<SeasonDto> result = seasonService.getAllSeasons();

        assertEquals(2, result.size());
        assertEquals(season.getId(), result.get(0).getId());
        assertEquals(season2.getId(), result.get(1).getId());
        verify(seasonRepositoryMock, times(1)).findAllByOrderByPodcastIdAscSeasonNumberAsc();
    }

    @Test
    void getSeasonsByPodcast_ShouldReturnLimitedDtos_WhenFullFalse() {
        List<Season> seasons = Arrays.asList(season);
        when(seasonRepositoryMock.findByPodcastIdOrderBySeasonNumberAsc(podcastId)).thenReturn(seasons);

        List<SeasonDto> result = seasonService.getSeasonsByPodcast(podcastId, false);

        assertEquals(1, result.size());
        assertEquals(season.getId(), result.get(0).getId());
        assertEquals(season.getName(), result.get(0).getName());
        assertNotEquals(season.getImageUrl(), result.get(0).getImageUrl());
        assertNull(result.get(0).getDescription());
    }

    @Test
    void getSeasonsByPodcast_ShouldReturnFullDtos_WhenFullTrue() {
        List<Season> seasons = Arrays.asList(season);
        when(seasonRepositoryMock.findByPodcastIdOrderBySeasonNumberAsc(podcastId)).thenReturn(seasons);

        List<SeasonDto> result = seasonService.getSeasonsByPodcast(podcastId, true);

        assertEquals(1, result.size());
        assertEquals(season.getId(), result.get(0).getId());
        assertEquals(season.getDescription(), result.get(0).getDescription());
        assertEquals(season.getImageUrl(), result.get(0).getImageUrl());
    }

    @Test
    void getSeasonsByPodcast_ShouldThrow_WhenPodcastIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.getSeasonsByPodcast(null, true));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("PodcastId must be provided", ex.getReason());
    }
/*
    @Test
    void addSeason_ShouldSaveAndReturnSeason() {
        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(podcastId, 1)).thenReturn(false);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Season result = seasonService.addSeason(seasonDto);

        assertNotNull(result);
        assertEquals(seasonDto.getName(), result.getName());
        assertEquals(seasonDto.getPodcastId(), result.getPodcastId());
        verify(seasonRepositoryMock, times(1)).save(any(Season.class));
    }

    @Test
    void addSeason_ShouldThrow_WhenNameNull() {
        seasonDto.setName(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name is required", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addOneEpisodeToSeason_ShouldAddEpisode() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.addOneEpisodeToSeason(seasonId, episodeId);

        assertTrue(result.getEpisodes().contains(episodeId));
        verify(episodeApiClientMock, times(1)).addSeasonToEpisode(episodeId, seasonId);
        verify(seasonRepositoryMock, times(1)).save(season);
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenEpisodeExistsInSeason() {
        season.getEpisodes().add(episodeId);
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.addOneEpisodeToSeason(seasonId, episodeId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void deleteSeason_ShouldDelete_WhenExists() {
        season.getEpisodes().add(episodeId);
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        String result = seasonService.deleteSeason(seasonId);

        assertThat(result).contains(seasonId.toString());
        verify(episodeApiClientMock, times(1)).removeSeasonFromEpisode(episodeId, seasonId);
        verify(seasonRepositoryMock, times(1)).deleteById(seasonId);
    }

    @Test
    void deleteSeason_ShouldThrow_WhenNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.deleteSeason(seasonId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldRemoveEpisode() {
        season.getEpisodes().add(episodeId);
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.removeOneEpisodeFromSeason(seasonId, episodeId);

        assertFalse(result.getEpisodes().contains(episodeId));
        verify(episodeApiClientMock, times(1)).removeSeasonFromEpisode(episodeId, seasonId);
        verify(seasonRepositoryMock, times(1)).save(season);
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldThrow_WhenEpisodeNotExists() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.removeOneEpisodeFromSeason(seasonId, episodeId));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        verify(seasonRepositoryMock, never()).save(any());
    }*/
}