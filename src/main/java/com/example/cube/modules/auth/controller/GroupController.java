package com.example.cube.modules.auth.controller;

import com.example.cube.modules.auth.dto.GroupRequest;
import com.example.cube.modules.auth.dto.GroupResponse;
import com.example.cube.modules.auth.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody GroupRequest request, Authentication auth) {
        return ResponseEntity.ok(groupService.create(request, auth));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponse>> list(Authentication auth) {
        return ResponseEntity.ok(groupService.list(auth));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getById(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(groupService.getById(id, auth));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody GroupRequest request,
                                                 Authentication auth) {
        return ResponseEntity.ok(groupService.update(id, request, auth));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        groupService.delete(id, auth);
        return ResponseEntity.noContent().build();
    }
}
