package com.hanafi.hanafihotel.service;


import com.hanafi.hanafihotel.model.Role;
import com.hanafi.hanafihotel.model.User;

import java.util.List;

public interface RoleService {
    List<Role> getAllRoles();
    Role createRole(Role role);
    void deleteRole(Long id);
    Role getRoleByName(String name);
    Role getRoleById(Long id);
    User removeUserFromRole(Long userId, Long roleId);
    User assignRoleToUser(Long userId,Long roleId);
    Role removeAllUsersFromRole(Long roleId);


}
