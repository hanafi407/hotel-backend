package com.hanafi.hanafihotel.controller;

import com.hanafi.hanafihotel.exception.RoleAlreadyExistsException;
import com.hanafi.hanafihotel.exception.RoleNotFoundException;
import com.hanafi.hanafihotel.model.Role;
import com.hanafi.hanafihotel.model.User;
import com.hanafi.hanafihotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        return new ResponseEntity<>(roleService.getAllRoles(), HttpStatus.FOUND);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRoles(@RequestBody Role role) {
        try {
            roleService.createRole(role);
            return ResponseEntity.ok("Create role success.");
        } catch (RoleAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public ResponseEntity<String> deleteRole(@PathVariable("roleId") Long roleId) {
        try {
            roleService.deleteRole(roleId);
            return ResponseEntity.ok("Role deleted is success.");
        } catch (RoleNotFoundException e) {
            return ResponseEntity.status((HttpStatus.NOT_FOUND)).body(e.getMessage());
        }
    }

    @PostMapping("/remove-all-users-from-roles/{roleId}")
    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId) {
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove-user-from-role")
    public User removeUserFromRole(
            @RequestParam("roleId") Long roleId,
            @RequestParam("userId") Long userId

    ) {
        return roleService.removeUserFromRole(userId, roleId);
    }

    @PostMapping("/assign-role-to-user")
    public User assignRoleToUser(
            @RequestParam("roleId") Long roleId,
            @RequestParam("userId") Long userId

    ) {
        return roleService.assignRoleToUser(userId, roleId);
    }

}
