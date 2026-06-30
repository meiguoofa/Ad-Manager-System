package com.admanager.admin.mapper;

import com.admanager.admin.model.AdminSession;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface AdminSessionMapper {

    int insert(AdminSession session);

    AdminSession selectActiveByIdAndHash(@Param("id") String id,
                                         @Param("tokenHash") String tokenHash,
                                         @Param("now") LocalDateTime now);

    AdminSession selectActiveById(@Param("id") String id,
                                  @Param("now") LocalDateTime now);

    int revokeActiveByIdAndHash(@Param("id") String id,
                                @Param("tokenHash") String tokenHash,
                                @Param("now") LocalDateTime now,
                                @Param("revokedAt") LocalDateTime revokedAt);

    int revokeById(@Param("id") String id,
                   @Param("revokedAt") LocalDateTime revokedAt);

    int revokeActiveByUserId(@Param("userId") String userId,
                             @Param("now") LocalDateTime now,
                             @Param("revokedAt") LocalDateTime revokedAt);
}
