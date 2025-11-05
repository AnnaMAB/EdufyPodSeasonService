package org.example.edufypodseasonservice.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
public class Season {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "episode_id", columnDefinition = "char(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;
    @Column(length = 50, nullable = false)
    private String name;
    @Column(length = 500, nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "podcast_id")
    @JsonIgnoreProperties({"episodes", "genres"})
    private Podcast podcast;
    private String thumbnailUrl;
    private String imageUrl;


}
