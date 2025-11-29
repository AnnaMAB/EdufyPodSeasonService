package org.example.edufypodseasonservice.repositories;

import jakarta.transaction.Transactional;
import org.example.edufypodseasonservice.entities.Season;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SeasonRepositoryTest {

    @Autowired
    private SeasonRepository seasonRepository;

    private final UUID podcastId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    private final UUID podcastId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");

    @BeforeEach
    @Transactional
    void setUp() {
        seasonRepository.deleteAll();


        Season season1 = new Season();
        season1.setName("Season 1");
        season1.setSeasonNumber(1);
        season1.setPodcastId(podcastId1);

        Season season2 = new Season();
        season2.setName("Season 2");
        season2.setSeasonNumber(2);
        season2.setPodcastId(podcastId1);

        Season season3 = new Season();
        season3.setName("Season 1 Podcast 2");
        season3.setSeasonNumber(1);
        season3.setPodcastId(podcastId2);

        seasonRepository.saveAll(List.of(season1, season2, season3));
    }


    @Test
    void testExistsByPodcastIdAndSeasonNumber() {
        boolean exists = seasonRepository.existsByPodcastIdAndSeasonNumber(podcastId1, 1);
        boolean notExists = seasonRepository.existsByPodcastIdAndSeasonNumber(podcastId1, 99);

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindAllByOrderByPodcastIdAscSeasonNumberAsc() {
        List<Season> seasons = seasonRepository.findAllByOrderByPodcastIdAscSeasonNumberAsc();

        assertThat(seasons.size()).isEqualTo(3);
        assertThat(seasons.get(0).getSeasonNumber()).isEqualTo(1);
        assertThat(seasons.get(1).getSeasonNumber()).isEqualTo(2);
        assertThat(seasons.get(2).getSeasonNumber()).isEqualTo(1);
        assertThat(seasons.get(0).getPodcastId().equals(seasons.get(1).getPodcastId()));

    }

    @Test
    void testFindByPodcastIdOrderBySeasonNumberAsc() {
        List<Season> seasons = seasonRepository.findByPodcastIdOrderBySeasonNumberAsc(podcastId1);

        assertThat(seasons.size()).isEqualTo(2);
        assertThat(seasons.get(0).getSeasonNumber()).isEqualTo(1);
        assertThat(seasons.get(1).getSeasonNumber()).isEqualTo(2);
    }

    @Test
    void testFindFirstByPodcastIdOrderBySeasonNumberAsc() {
        Optional<Season> firstSeason = seasonRepository.findFirstByPodcastIdOrderBySeasonNumberAsc(podcastId1);

        assertThat(firstSeason).isPresent();
        assertThat(firstSeason.get().getSeasonNumber()).isEqualTo(1);
    }

    @Test
    void testFindFirstByPodcastIdOrderBySeasonNumberDesc() {
        Optional<Season> latestSeason = seasonRepository.findFirstByPodcastIdOrderBySeasonNumberDesc(podcastId1);

        assertThat(latestSeason).isPresent();
        assertThat(latestSeason.get().getSeasonNumber()).isEqualTo(2);
    }
}

