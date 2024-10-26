package com.app.myapp.role;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.app.myapp.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @CacheEvict(value = "roles:all", allEntries = true)
    public Role createRole(Role role) {
        Boolean isExist = checkRoleExistsByName(role.getName().toString());
        if (isExist) {
            throw new NotFoundException("ROLE ALREADY EXIST");
        }
        return roleRepository.save(role);
    }

    @Cacheable(value = "roles:all")
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Cacheable(value = "roles:id", key = "#id")
    public Role getRoleById(String id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "roles:username", key = "#name")
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @CachePut(value = "roles:id", key = "#id")
    public Role updateRole(String id, Role role) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(role.getName());
                    return roleRepository.save(existingRole);
                }).orElse(null);
    }

    @CacheEvict(value = "roles:id", key = "#id")
    public boolean deleteRole(String id) {
        Boolean isExist = checkRoleExistsById(id);
        if (!isExist) {
            throw new NotFoundException("ROLE NOT FOUND");
        }
        roleRepository.deleteById(id);
        return true;
    }

    private Boolean checkRoleExistsById(String roleId) {
        return roleRepository.existsById(roleId);
    }

    private Boolean checkRoleExistsByName(String name) {
        return roleRepository.existsByName(name);
    }
}
