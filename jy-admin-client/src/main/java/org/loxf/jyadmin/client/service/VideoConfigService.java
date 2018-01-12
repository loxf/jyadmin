package org.loxf.jyadmin.client.service;

import org.loxf.jyadmin.base.bean.BaseResult;
import org.loxf.jyadmin.base.bean.PageResult;
import org.loxf.jyadmin.client.dto.VideoConfigDto;

public interface VideoConfigService {
    public BaseResult<String> addVideo(VideoConfigDto dto);
    public BaseResult<String> delVideo(String videoId);
    public BaseResult<String> editVideo(VideoConfigDto dto);
    public BaseResult<VideoConfigDto> queryVideo(String videoId);
    public PageResult<VideoConfigDto> pager(VideoConfigDto catalogDto);
    public BaseResult updateProgress(VideoConfigDto dto);

    /**
     * @param fileId
     * @param type m3u8 m3u8_hd m3u8_sd
     * @return
     */
    public BaseResult<String> queryUrl(String fileId, String type);
}
