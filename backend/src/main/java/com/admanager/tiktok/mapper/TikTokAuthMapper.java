package com.admanager.tiktok.mapper;

import com.admanager.tiktok.model.TikTokAuth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TikTokAuthMapper {

    int insert(TikTokAuth auth);

    int update(TikTokAuth auth);
}
