package person.sa.service;

import person.sa.pojo.Bgm;
import person.sa.utils.PagedResult;

public interface VideoService {
    void addBgm(Bgm bgm);

    PagedResult queryBgmList(Integer page, Integer pageSize);

    void deleteBmg(String id);

    PagedResult queryReportList(Integer page, int i);

    void updateVideoStatus(String videoId, int status);
}
