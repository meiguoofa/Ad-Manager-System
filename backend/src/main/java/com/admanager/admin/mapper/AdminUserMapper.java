package com.admanager.admin.mapper;

import com.admanager.admin.model.AdminUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminUserMapper {

    AdminUser selectById(@Param("id") String id);

    AdminUser selectByUsername(@Param("username") String username);

    List<AdminUser> selectPaged(@Param("offset") int offset,
                                @Param("limit") int limit,
                                @Param("keyword") String keyword,
                                @Param("status") String status);

    long countPaged(@Param("keyword") String keyword,
                    @Param("status") String status);

    int insert(AdminUser user);

    int updateLoginFailure(@Param("id") String id,
                           @Param("loginFailCount") int loginFailCount,
                           @Param("lockedUntil") LocalDateTime lockedUntil,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int updateLoginSuccess(@Param("id") String id,
                           @Param("lastLoginAt") LocalDateTime lastLoginAt,
                           @Param("updatedAt") LocalDateTime updatedAt);

    int updateStatus(@Param("id") String id,
                     @Param("status") String status,
                     @Param("updatedAt") LocalDateTime updatedAt);

    int updatePasswordHash(@Param("id") String id,
                           @Param("passwordHash") String passwordHash,
                           @Param("updatedAt") LocalDateTime updatedAt);
}
