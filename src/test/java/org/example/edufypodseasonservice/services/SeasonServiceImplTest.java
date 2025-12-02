package org.example.edufypodseasonservice.services;

import org.example.edufypodseasonservice.converters.UserInfo;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class SeasonServiceImplTest {


    @Mock
    private SeasonRepository seasonRepositoryMock;
    @Mock
    private EpisodeApiClient episodeApiClientMock;

    @Mock
    private UserInfo userInfoMock;

    private final SeasonDtoConverter seasonDtoConverter = new SeasonDtoConverter();

    @InjectMocks
    private SeasonServiceImpl seasonService;

    private Season season;
    private SeasonDto seasonDto;


    private final UUID seasonId = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID podcastId = UUID.fromString("00000000-0000-0000-0000-000000000002");
    private final UUID episodeId = UUID.fromString("00000000-0000-0000-0000-000000000003");
    private final UUID episodeId2 = UUID.fromString("00000000-0000-0000-0000-000000000004");


    @BeforeEach
    void setUp() {
        seasonService = new SeasonServiceImpl(seasonRepositoryMock, seasonDtoConverter, episodeApiClientMock, userInfoMock);

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


    //getSeason
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

    //getAllSeasons
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

    //getSeasonsByPodcast
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

    //getFirstSeason
    @Test
    void getFirstSeason_ShouldReturnFirstSeason() {
        when(seasonRepositoryMock.findFirstByPodcastIdOrderBySeasonNumberAsc(podcastId)).thenReturn(Optional.of(season));

        SeasonDto result = seasonService.getFirstSeason(podcastId);

        assertEquals(season.getId(), result.getId());
    }

    @Test
    void getFirstSeason_ShouldThrow_WhenPodcastIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.getFirstSeason(null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("PodcastId must be provided", ex.getReason());
    }

    @Test
    void getFirstSeason_ShouldThrow_WhenNotFound() {
        when(seasonRepositoryMock.findFirstByPodcastIdOrderBySeasonNumberAsc(podcastId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.getFirstSeason(podcastId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists for podcastId: " + podcastId + ".", ex.getReason());
    }

    //getLatestSeason
    @Test
    void getLatestSeason_ShouldReturnFirstSeason() {
        when(seasonRepositoryMock.findFirstByPodcastIdOrderBySeasonNumberDesc(podcastId)).thenReturn(Optional.of(season));

        SeasonDto result = seasonService.getLatestSeason(podcastId);

        assertEquals(season.getId(), result.getId());
    }

    @Test
    void getLatestSeason_ShouldThrow_WhenPodcastIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.getLatestSeason(null));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("PodcastId must be provided", ex.getReason());
    }

    @Test
    void getLatestSeason_ShouldThrow_WhenNotFound() {
        when(seasonRepositoryMock.findFirstByPodcastIdOrderBySeasonNumberDesc(podcastId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.getLatestSeason(podcastId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists for podcastId: " + podcastId + ".", ex.getReason());
    }

    //addSeason
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
    void addSeason_ShouldSaveAndReturnSeasonWithDefaultValuesWhenBlank() {
        seasonDto.setDescription("");
        seasonDto.setThumbnailUrl("");
        seasonDto.setImageUrl("");

        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(podcastId, 1)).thenReturn(false);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Season result = seasonService.addSeason(seasonDto);

        assertNotNull(result);
        assertEquals(seasonDto.getName(), result.getName());
        assertEquals("https://default/thumbnail.url", result.getThumbnailUrl());
        assertEquals("https://default/image.url", result.getImageUrl());
        assertEquals(seasonDto.getDescription(), result.getDescription());
        assertEquals(seasonDto.getPodcastId(), result.getPodcastId());
        verify(seasonRepositoryMock, times(1)).save(any(Season.class));
    }


    @Test
    void addSeason_ShouldSaveAndReturnSeasonWithDefaultValuesWhenNull() {
        seasonDto.setDescription(null);
        seasonDto.setThumbnailUrl(null);
        seasonDto.setImageUrl(null);
        seasonDto.setEpisodes(null);

        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(podcastId, 1)).thenReturn(false);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Season result = seasonService.addSeason(seasonDto);

        assertNotNull(result);
        assertEquals(seasonDto.getName(), result.getName());
        assertEquals("", result.getDescription());
        assertEquals("https://default/thumbnail.url", result.getThumbnailUrl());
        assertEquals("https://default/image.url", result.getImageUrl());
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
    void addSeason_ShouldThrow_WhenNameIsBlank() {
        seasonDto.setName("");
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name is required", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addSeason_ShouldThrow_WhenPodcastIdNull() {
        seasonDto.setPodcastId(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("PodcastId is required", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addSeason_ShouldThrow_WhenSeasonNumberNull() {
        seasonDto.setSeasonNumber(null);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Season number is required", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addSeason_ShouldThrow_WhenSeasonNumberAlreadyExists() {
        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(seasonDto.getPodcastId(), seasonDto.getSeasonNumber())).thenReturn(true);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Season with that number already exists", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addSeason_ShouldThrow_WhenGetEpisodesNotNull() {
        seasonDto.getEpisodes().add(episodeId);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Episodes can't be added from this endpoint", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addSeason_ShouldThrow_WhenGetEpisodesIsNotEmpty() {
        seasonDto.getEpisodes().add(episodeId);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.addSeason(seasonDto));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Episodes can't be added from this endpoint", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    //updateSeason
    @Test
    void updateSeason_ShouldUpdateAndReturnSeason() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seasonDto.setName("Updated Name");
        seasonDto.setDescription("New Desc");
        seasonDto.setImageUrl("new.png");
        seasonDto.setThumbnailUrl("newthumb.png");
        seasonDto.setSeasonNumber(2);

        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(podcastId, 2)).thenReturn(false);

        Season result = seasonService.updateSeason(seasonDto);

        assertEquals("Updated Name", result.getName());
        assertEquals("New Desc", result.getDescription());
        assertEquals("new.png", result.getImageUrl());
        assertEquals("newthumb.png", result.getThumbnailUrl());
        assertEquals(2, result.getSeasonNumber());
        verify(seasonRepositoryMock).save(any());
    }

     @Test
    void updateSeason_ShouldUpdateAndReturnSeasonIgnoringNullValues() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        seasonDto.setName(null);
        seasonDto.setDescription(null);
        seasonDto.setImageUrl(null);
        seasonDto.setThumbnailUrl(null);
        seasonDto.setSeasonNumber(null);
        seasonDto.setPodcastId(null);
        seasonDto.setEpisodes(null);

        Season result = seasonService.updateSeason(seasonDto);

        assertEquals(season.getName(), result.getName());
        assertEquals(season.getDescription(), result.getDescription());
        assertEquals(season.getImageUrl(), result.getImageUrl());
        assertEquals(season.getThumbnailUrl(), result.getThumbnailUrl());
        assertEquals(season.getSeasonNumber(), result.getSeasonNumber());
        assertEquals(season.getPodcastId(), result.getPodcastId());
        assertEquals(season.getEpisodes(), result.getEpisodes());
        verify(seasonRepositoryMock).save(any());
    }

    @Test
    void updateSeason_ShouldThrow_WhenIdNull() {
        seasonDto.setId(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("SeasonId is required", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void updateSeason_ShouldThrow_WhenSeasonNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists with id: " + seasonId + ".", ex.getReason());
        verify(seasonRepositoryMock).findById(seasonId);
    }

    @Test
    void updateSeason_ShouldThrow_WhenUpdatedNameBlank() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        seasonDto.setName("");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Name can not be left blank.", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void updateSeason_ShouldThrow_WhenPodcastIdIsChanged() {
        seasonDto.setPodcastId(UUID.randomUUID());
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Update of podcastId not allowed.", exception.getReason());

        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void updateSeason_ShouldThrow_WhenSeasonNumberAlreadyExists() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        seasonDto.setSeasonNumber(2);

        when(seasonRepositoryMock.existsByPodcastIdAndSeasonNumber(podcastId, 2)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Season with that number already exists", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void updateSeason_ShouldThrow_WhenNewDescriptionBlank() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        seasonDto.setDescription("");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Description can not be left blank.", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void updateSeason_ShouldThrow_WhenEpisodesProvided() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        seasonDto.getEpisodes().add(episodeId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.updateSeason(seasonDto));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Episodes can't be added from this endpoint", ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
    }

    //deleteSeason
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
    void deleteSeason_ShouldThrow_WhenIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () ->
                seasonService.deleteSeason(null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Id must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void deleteSeason_ShouldThrow_WhenNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> seasonService.deleteSeason(seasonId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void deleteSeason_ShouldDeleteAndNotCallEpisodeApi_WhenSeasonHasNoEpisodes() {
        season.getEpisodes().clear();

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        String result = seasonService.deleteSeason(seasonId);

        assertThat(result).contains(seasonId.toString());
        verify(episodeApiClientMock, never()).removeSeasonFromEpisode(any(), any());
        verify(seasonRepositoryMock, times(1)).deleteById(seasonId);
    }

    //addEpisodesToSeason
    @Test
    void addEpisodesToSeason_ShouldAddEpisodesAndReturnDto() {
        season.getEpisodes().clear();
        List<UUID> newEpisodes = List.of(episodeId, episodeId2);

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SeasonDto result = seasonService.addEpisodesToSeason(seasonId, newEpisodes);

        assertEquals(2, result.getEpisodes().size());
        assertEquals(episodeId, result.getEpisodes().get(0));
        assertEquals(episodeId2, result.getEpisodes().get(1));
        verify(seasonRepositoryMock, times(1)).save(season);
        verify(episodeApiClientMock, times(2)).addSeasonToEpisode(any(), eq(seasonId));
    }

    @Test
    void addEpisodesToSeason_ShouldThrow_WhenSeasonIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addEpisodesToSeason(null, List.of(episodeId)));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Season ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void addEpisodesToSeason_ShouldThrow_WhenEpisodeIdsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addEpisodesToSeason(seasonId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("At least one episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void addEpisodesToSeason_ShouldThrow_WhenEpisodeIdsIsEmpty() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addEpisodesToSeason(seasonId, List.of()));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("At least one episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void addEpisodesToSeason_ShouldThrow_WhenSeasonNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addEpisodesToSeason(seasonId, List.of(episodeId)));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists with id: " + seasonId + ".", ex.getReason());
        verify(seasonRepositoryMock).findById(seasonId);
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addEpisodesToSeason_ShouldNotDuplicateExistingEpisodes() {
        UUID existingEp = episodeId;
        UUID newEp = episodeId2;
        season.setEpisodes(new ArrayList<>(List.of(existingEp)));

        List<UUID> incoming = List.of(existingEp, newEp);

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SeasonDto result = seasonService.addEpisodesToSeason(seasonId, incoming);

        assertEquals(2, result.getEpisodes().size());
        verify(episodeApiClientMock, times(1)).addSeasonToEpisode(eq(newEp), eq(seasonId));
        verify(seasonRepositoryMock).save(any());
    }

    //addOneEpisodeToSeason
    @Test
    void addOneEpisodeToSeason_ShouldAddEpisode() {
        season.getEpisodes().clear();
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.addOneEpisodeToSeason(seasonId, episodeId);

        assertEquals(1, result.getEpisodes().size());
        assertTrue(result.getEpisodes().contains(episodeId));
        verify(episodeApiClientMock, times(1)).addSeasonToEpisode(episodeId, seasonId);
        verify(seasonRepositoryMock, times(1)).save(season);
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenSeasonIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addOneEpisodeToSeason(null, episodeId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Season ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenEpisodeIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addOneEpisodeToSeason(seasonId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenEpisodeDoesNotExist() {
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addOneEpisodeToSeason(seasonId, episodeId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Episode " + episodeId + " not found", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock);
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenSeasonNotFound() {
        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addOneEpisodeToSeason(seasonId, episodeId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists with id: " + seasonId + ".", ex.getReason());
        verify(seasonRepositoryMock).findById(seasonId);
        verify(seasonRepositoryMock, never()).save(any());
    }

    @Test
    void addOneEpisodeToSeason_ShouldThrow_WhenEpisodeAlreadyInSeason() {
        season.getEpisodes().add(episodeId);

        when(episodeApiClientMock.episodeExists(episodeId)).thenReturn(true);
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.addOneEpisodeToSeason(seasonId, episodeId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Episode " + episodeId + " already exists in season " + seasonId, ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
        verify(episodeApiClientMock, never()).addSeasonToEpisode(any(), any());
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

    //removeEpisodesFromSeason
    @Test
    void removeEpisodesFromSeason_ShouldRemoveEpisodesAndReturnDto() {
        season.setEpisodes(new ArrayList<>(List.of(episodeId, episodeId2)));

        List<UUID> toRemove = List.of(episodeId);

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.removeEpisodesFromSeason(seasonId, toRemove);

        assertEquals(1, result.getEpisodes().size());
        assertFalse(result.getEpisodes().contains(episodeId));
        assertTrue(result.getEpisodes().contains(episodeId2));

        verify(seasonRepositoryMock).save(season);
        verify(episodeApiClientMock).removeSeasonFromEpisode(episodeId, seasonId);
    }

    @Test
    void removeEpisodesFromSeason_ShouldDoNothingIfEpisodeNotInSeason() {
        season.setEpisodes(new ArrayList<>(List.of(episodeId)));

        List<UUID> toRemove = List.of(episodeId2);

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.removeEpisodesFromSeason(seasonId, toRemove);

        assertEquals(1, result.getEpisodes().size());
        assertTrue(result.getEpisodes().contains(episodeId));

        verify(seasonRepositoryMock).save(season);
        verify(episodeApiClientMock, never()).removeSeasonFromEpisode(any(), any());
    }

    @Test
    void removeEpisodesFromSeason_ShouldThrow_WhenSeasonIdNull() {
        List<UUID> toRemove = List.of(episodeId);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeEpisodesFromSeason(null, toRemove));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Season ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void removeEpisodesFromSeason_ShouldThrow_WhenEpisodeIdsNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeEpisodesFromSeason(seasonId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("At least one episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void removeEpisodesFromSeason_ShouldThrow_WhenEpisodeIdsEmpty() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeEpisodesFromSeason(seasonId, new ArrayList<>()));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("At least one episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void removeEpisodesFromSeason_ShouldThrow_WhenSeasonNotFound() {
        List<UUID> toRemove = List.of(episodeId);

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeEpisodesFromSeason(seasonId, toRemove));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists with id: " + seasonId + ".", ex.getReason());

        verify(seasonRepositoryMock).findById(seasonId);
        verify(seasonRepositoryMock, never()).save(any());
        verifyNoInteractions(episodeApiClientMock);
    }

    //removeOneEpisodeFromSeason
    @Test
    void removeOneEpisodeFromSeason_ShouldRemoveEpisode() {
        season.setEpisodes(new ArrayList<>(List.of(episodeId)));

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));
        when(seasonRepositoryMock.save(any(Season.class))).thenAnswer(i -> i.getArgument(0));

        SeasonDto result = seasonService.removeOneEpisodeFromSeason(seasonId, episodeId);

        assertEquals(0, result.getEpisodes().size());
        verify(seasonRepositoryMock).save(season);
        verify(episodeApiClientMock).removeSeasonFromEpisode(episodeId, seasonId);
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldThrow_WhenSeasonIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeOneEpisodeFromSeason(null, episodeId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Season ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldThrow_WhenEpisodeIdNull() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeOneEpisodeFromSeason(seasonId, null));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("Episode ID must be provided", ex.getReason());
        verifyNoInteractions(seasonRepositoryMock, episodeApiClientMock);
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldThrow_WhenSeasonNotFound() {
        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeOneEpisodeFromSeason(seasonId, episodeId));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("No season exists with id: " + seasonId + ".", ex.getReason());
        verify(seasonRepositoryMock).findById(seasonId);
        verify(seasonRepositoryMock, never()).save(any());
        verifyNoInteractions(episodeApiClientMock);
    }

    @Test
    void removeOneEpisodeFromSeason_ShouldThrow_WhenEpisodeNotInSeason() {
        season.setEpisodes(new ArrayList<>());

        when(seasonRepositoryMock.findById(seasonId)).thenReturn(Optional.of(season));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> seasonService.removeOneEpisodeFromSeason(seasonId, episodeId));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Episode " + episodeId + " dosen't exists in season " + seasonId, ex.getReason());
        verify(seasonRepositoryMock, never()).save(any());
        verify(episodeApiClientMock, never()).removeSeasonFromEpisode(any(), any());
    }

}