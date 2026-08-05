package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.AccessRequest;
import com.example.cube.modules.auth.dto.AccessResponse;
import com.example.cube.modules.auth.service.AccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/modules/{moduleId}/access")
@RequiredArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @PostMapping
    public ResponseEntity<AccessResponse> create(@PathVariable Long moduleId,
                                                  @Valid @RequestBody AccessRequest request,
                                                  Authentication auth) {
        return ResponseEntity.ok(accessService.create(moduleId, request, auth));
    }

    @GetMapping
    public ResponseEntity<List<AccessResponse>> list(@PathVariable Long moduleId, Authentication auth) {
        return ResponseEntity.ok(accessService.listByModule(moduleId, auth));
    }

    @DeleteMapping("/{accessId}")
    public ResponseEntity<Void> delete(@PathVariable Long moduleId, @PathVariable Long accessId, Authentication auth) {
        accessService.delete(accessId, auth);
        return ResponseEntity.noContent().build();
    }
}
