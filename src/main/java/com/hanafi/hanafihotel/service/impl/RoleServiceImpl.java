package com.hanafi.hanafihotel.service.impl;

import com.hanafi.hanafihotel.exception.RoleAlreadyExistsException;
import com.hanafi.hanafihotel.exception.RoleNotFoundException;
import com.hanafi.hanafihotel.exception.UserAlreadyExistsException;
import com.hanafi.hanafihotel.model.Role;
import com.hanafi.hanafihotel.model.User;
import com.hanafi.hanafihotel.repository.RoleRepository;
import com.hanafi.hanafihotel.service.RoleService;
import com.hanafi.hanafihotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    @Override
    public List<Role> getAllRoles() {

        return roleRepository.findAll();
    }

    @Override
    public Role createRole(Role role) {
        String roleName = "ROLE_"+ role.getName().toUpperCase();
        Role newRole = new Role(roleName);
        if(roleRepository.existsByName(roleName)){
            throw new RoleAlreadyExistsException(roleName+" role already exists.");
        }
        return roleRepository.save(newRole);
    }

    @Override
    public void deleteRole(Long roleId) {
        if(roleRepository.existsById(roleId)){
            this.removeAllUsersFromRole(roleId);
            roleRepository.deleteById(roleId);
        }else{
            throw new RoleNotFoundException("Role id is not found.");
        }

    }

    @Override
    public Role getRoleByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    public Role getRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(()->new RoleNotFoundException("Role is not found."));
    }

    @Override
    public User removeUserFromRole(Long userId, Long roleId) {
        User user = userService.getUserById(userId);
        Role role = getRoleById(roleId);
        if(role.getUsers().contains(user)){
            role.removeUserFromRoles(user);
            roleRepository.save(role);
            return user;
        }

        throw new UsernameNotFoundException("User not found.");
    }

    @Override
    public User assignRoleToUser(Long userId, Long roleId) {
        Role  role = getRoleById(roleId);
        User user = userService.getUserById(userId);
        if(user.getRoles().contains(role)){
            throw new UserAlreadyExistsException(user.getFirstName()+" is already assigned in role "+role.getName());
        }
        role.assignRoleToUser(user);
        roleRepository.save(role);
        return user;
    }

    @Override
    public Role removeAllUsersFromRole(Long roleId) {
        Role role = getRoleById(roleId);
        role.removeAllUsersFromRole();
        return roleRepository.save(role);
    }
}
