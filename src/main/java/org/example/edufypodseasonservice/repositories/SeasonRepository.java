package org.example.edufypodseasonservice.repositories;


import org.example.edufypodseasonservice.entities.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SeasonRepository extends JpaRepository<Season, UUID> {


}
