package com.zxl.dao;


import com.zxl.domain.Permission;

import java.util.List;

/**
 * Created by zxl on 19/5/2.
 */
public interface PermissionDao {
    public List<Permission> findAll();
    public List<Permission> findByAdminUserId(int userId);
}
