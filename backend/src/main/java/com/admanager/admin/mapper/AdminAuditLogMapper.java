package com.admanager.admin.mapper;

import com.admanager.admin.model.AdminAuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AdminAuditLogMapper {

    int insert(AdminAuditLog log);

    List<AdminAuditLog> selectPaged(@Param("offset") int offset,
                                    @Param("limit") int limit,
                                    @Param("action") String action,
                                    @Param("operatorId") String operatorId,
                                    @Param("resourceType") String resourceType,
                                    @Param("resourceId") String resourceId,
                                    @Param("from") LocalDateTime from,
                                    @Param("to") LocalDateTime to);

    long countPaged(@Param("action") String action,
                    @Param("operatorId") String operatorId,
                    @Param("resourceType") String resourceType,
                    @Param("resourceId") String resourceId,
                    @Param("from") LocalDateTime from,
                    @Param("to") LocalDateTime to);

    List<AdminAuditLog> selectDistinctOperators(@Param("operatorKeyword") String operatorKeyword,
                                                @Param("from") LocalDateTime from,
                                                @Param("to") LocalDateTime to,
                                                @Param("limit") int limit);

    List<String> selectDistinctActions(@Param("from") LocalDateTime from,
                                       @Param("to") LocalDateTime to,
                                       @Param("limit") int limit);

    List<String> selectDistinctResourceTypes(@Param("from") LocalDateTime from,
                                             @Param("to") LocalDateTime to,
                                             @Param("limit") int limit);
}
