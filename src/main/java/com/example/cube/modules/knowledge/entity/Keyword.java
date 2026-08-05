package com.example.cube.modules.knowledge.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Table(name = "KEYWORD")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "KEYWORD_ID")
    private Long keywordId;

    @Column(name = "KEYWORD_NAME", nullable = false, unique = true, length = 100)
    private String keywordName;
}
