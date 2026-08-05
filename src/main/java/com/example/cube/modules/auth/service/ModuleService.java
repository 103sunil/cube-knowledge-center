package com.example.cube.modules.auth.service;

import com.example.cube.modules.auth.dto.ModuleRequest;
import com.example.cube.modules.auth.dto.ModuleResponse;
import com.example.cube.modules.auth.entity.ModuleMaster;
import com.example.cube.modules.auth.repository.ModuleMasterRepository;
import com.example.cube.common.exception.AccessDeniedAppException;
import com.example.cube.common.exception.DuplicateResourceException;
import com.example.cube.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private static final String MODULE = "AUTH";

    private final ModuleMasterRepository moduleMasterRepository;
    private final AccessControlService accessControlService;

    @Transactional
    public ModuleResponse create(ModuleRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_MODULES");
        if (moduleMasterRepository.findAll().stream().anyMatch(m -> m.getModuleCode().equalsIgnoreCase(request.getModuleCode()))) {
            throw new DuplicateResourceException("Module code already exists: " + request.getModuleCode());
        }
        ModuleMaster module = ModuleMaster.builder()
                .moduleCode(request.getModuleCode())
                .moduleName(request.getModuleName())
                .description(request.getDescription())
                .build();
        return toResponse(moduleMasterRepository.save(module));
    }

    public List<ModuleResponse> list(Authentication auth) {
        requirePermission(auth, "MANAGE_MODULES");
        return moduleMasterRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ModuleResponse getById(Long moduleId, Authentication auth) {
        requirePermission(auth, "MANAGE_MODULES");
        return toResponse(findOrThrow(moduleId));
    }

    @Transactional
    public ModuleResponse update(Long moduleId, ModuleRequest request, Authentication auth) {
        requirePermission(auth, "MANAGE_MODULES");
        ModuleMaster module = findOrThrow(moduleId);
        module.setModuleName(request.getModuleName());
        module.setDescription(request.getDescription());
        return toResponse(moduleMasterRepository.save(module));
    }

    @Transactional
    public void delete(Long moduleId, Authentication auth) {
        requirePermission(auth, "MANAGE_MODULES");
        ModuleMaster module = findOrThrow(moduleId);
        moduleMasterRepository.delete(module);
    }

    private ModuleMaster findOrThrow(Long moduleId) {
        return moduleMasterRepository.findById(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module not found: " + moduleId));
    }

    private void requirePermission(Authentication auth, String accessCode) {
        if (!accessControlService.hasPermission(auth.getName(), MODULE, accessCode)) {
            throw new AccessDeniedAppException("Not authorized to " + accessCode);
        }
    }

    private ModuleResponse toResponse(ModuleMaster m) {
        return ModuleResponse.builder()
                .moduleId(m.getModuleId())
                .moduleCode(m.getModuleCode())
                .moduleName(m.getModuleName())
                .description(m.getDescription())
                .build();
    }
}
