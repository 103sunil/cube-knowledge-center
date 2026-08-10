package com.example.cube.modules.knowledge.dto;

import lombok.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class KnowledgeUpdateRequest {

    @NotBlank
    private String title;

    private String description;

    @NotEmpty
    private List<String> keywords;
}