package com.example.cube.modules.knowledge.controller;

import com.example.cube.modules.knowledge.dto.KnowledgeCreateRequest;
import com.example.cube.modules.knowledge.dto.KnowledgeResponse;
import com.example.cube.modules.knowledge.dto.KnowledgeSubmissionResponse;
import com.example.cube.modules.knowledge.dto.KnowledgeUpdateRequest;
import com.example.cube.modules.knowledge.dto.RejectRequest;
import com.example.cube.modules.knowledge.service.KnowledgeService;
import com.example.cube.modules.knowledge.service.KnowledgeSubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSubmissionService knowledgeSubmissionService;

    @PostMapping
    public ResponseEntity<KnowledgeResponse> create(@Valid @RequestBody KnowledgeCreateRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(knowledgeService.create(request, auth));
    }

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KnowledgeSubmissionResponse> submit(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("keywords") List<String> keywords,
            @RequestParam("files") List<MultipartFile> files,
            Authentication auth) {
        return ResponseEntity.ok(knowledgeSubmissionService.submit(title, description, keywords, files, auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(knowledgeService.getById(id, auth));
    }

    @GetMapping("/search")
    public ResponseEntity<List<KnowledgeResponse>> search(@RequestParam("q") String query, Authentication auth) {
        return ResponseEntity.ok(knowledgeService.search(query, auth));
    }

    @GetMapping
    public ResponseEntity<Page<KnowledgeResponse>> listPending(@RequestParam(defaultValue = "PENDING") String status,
                                                                 Pageable pageable,
                                                                 Authentication auth) {
        return ResponseEntity.ok(knowledgeService.listPending(auth, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<KnowledgeResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody KnowledgeUpdateRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(knowledgeService.update(id, request, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        knowledgeService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<KnowledgeResponse> approve(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(knowledgeService.approve(id, auth));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<KnowledgeResponse> reject(@PathVariable Long id,
                                                      @Valid @RequestBody RejectRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(knowledgeService.reject(id, request.getReason(), auth));
    }
}