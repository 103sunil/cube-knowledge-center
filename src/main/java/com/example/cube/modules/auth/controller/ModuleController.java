package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.ModuleRequest;
import com.example.cube.modules.auth.dto.ModuleResponse;
import com.example.cube.modules.auth.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<ModuleResponse> create(@Valid @RequestBody ModuleRequest request, Authentication auth) {
        return ResponseEntity.ok(moduleService.create(request, auth));
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> list(Authentication auth) {
        return ResponseEntity.ok(moduleService.list(auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(moduleService.getById(id, auth));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponse> update(@PathVariable Long id,
                                                  @Valid @RequestBody ModuleRequest request,
                                                  Authentication auth) {
        return ResponseEntity.ok(moduleService.update(id, request, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        moduleService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }
}
