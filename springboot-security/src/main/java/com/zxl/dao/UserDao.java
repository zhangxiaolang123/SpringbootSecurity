package com.zxl.dao;


import com.zxl.domain.SysUser;
/**
 * Created by zxl on 19/5/2.
 */
public interface UserDao {
    public SysUser findByUserName(String username);
}
