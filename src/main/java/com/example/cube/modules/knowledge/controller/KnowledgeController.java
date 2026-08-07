package com.example.cube.modules.knowledge.controller;

import com.example.cube.modules.knowledge.dto.KnowledgeCreateRequest;
import com.example.cube.modules.knowledge.dto.KnowledgeResponse;
import com.example.cube.modules.knowledge.dto.RejectRequest;
import com.example.cube.modules.knowledge.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    @PostMapping
    public ResponseEntity<KnowledgeResponse> create(@Valid @RequestBody KnowledgeCreateRequest request,
                                                      Authentication auth) {
        return ResponseEntity.ok(knowledgeService.create(request, auth));
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