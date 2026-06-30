package com.admanager.admin.mapper;

import com.admanager.admin.model.AdminRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminRoleMapper {

    AdminRole selectById(@Param("id") String id);

    AdminRole selectByRoleCode(@Param("roleCode") String roleCode);

    List<AdminRole> selectAll();

    List<AdminRole> selectByUserId(@Param("userId") String userId);
}
