package com.admanager.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminUserRoleMapper {

    int insert(@Param("userId") String userId,
               @Param("roleId") String roleId,
               @Param("grantedBy") String grantedBy,
               @Param("grantedAt") LocalDateTime grantedAt);

    int deleteByUserAndRole(@Param("userId") String userId,
                            @Param("roleId") String roleId);

    List<String> selectRoleCodesByUserId(@Param("userId") String userId);

    long countUsersByRoleCode(@Param("roleCode") String roleCode);
}
