package com.app.myapp.role;

import com.app.myapp.exception.NotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role createRole(Role role) {
        Boolean isExist = checkRoleExistsByName(role.getName().toString());
        if (isExist) {
            throw new NotFoundException("ROLE ALREADY EXIST");
        }
        return roleRepository.save(role);
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(String id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).orElse(null);
    }

    public Role updateRole(String id, Role role) {
        return roleRepository.findById(id)
                .map(existingRole -> {
                    existingRole.setName(role.getName());
                    return roleRepository.save(existingRole);
                }).orElse(null);
    }

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
