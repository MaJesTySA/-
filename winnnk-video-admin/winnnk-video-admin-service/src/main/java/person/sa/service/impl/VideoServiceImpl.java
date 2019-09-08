package person.sa.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.io.FileUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import person.sa.enums.BGMOperatorTypeEnum;
import person.sa.mapper.BgmMapper;
import person.sa.mapper.UsersReportMapperCustom;
import person.sa.mapper.VideosMapper;
import person.sa.pojo.Bgm;
import person.sa.pojo.BgmExample;
import person.sa.pojo.Videos;
import person.sa.pojo.vo.Reports;
import person.sa.service.VideoService;
import person.sa.utils.JsonUtils;
import person.sa.utils.PagedResult;
import person.sa.web.util.ZKCurator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private BgmMapper bgmMapper;
    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private ZKCurator zkCurator;
    @Autowired
    UsersReportMapperCustom usersReportMapperCustom;
    @Autowired
    private Sid sid;
    @Value("${FILE_SPACE}")
    private String FILE_SPACE;

    @Override
    public void addBgm(Bgm bgm) {
        String bgmId = sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);
        Map<String, String> map = new HashMap<>();
        map.put("optType", BGMOperatorTypeEnum.ADD.type);
        map.put("path", bgm.getPath());
        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));
    }

    @Override
    public PagedResult queryBgmList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        BgmExample example = new BgmExample();
        List<Bgm> list = bgmMapper.selectByExample(example);
        PageInfo<Bgm> pageList = new PageInfo<>(list);
        PagedResult result = new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setPage(page);
        result.setRecords(pageList.getTotal());
        return result;
    }

    @Override
    public void deleteBmg(String id) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(id);
        //删除本地音频
        File file = new File(FILE_SPACE + bgm.getPath());
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        bgmMapper.deleteByPrimaryKey(id);
        Map<String, String> map = new HashMap<>();
        map.put("optType", BGMOperatorTypeEnum.DELETE.type);
        map.put("path", bgm.getPath());
        zkCurator.sendBgmOperator(id, JsonUtils.objectToJson(map));

    }

    @Override
    public PagedResult queryReportList(Integer page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Reports> reportsList = usersReportMapperCustom.selectAllVideoReport();
        PageInfo<Reports> pageList = new PageInfo<Reports>(reportsList);
        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(reportsList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Override
    public void updateVideoStatus(String videoId, int status) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setStatus(status);
        videosMapper.updateByPrimaryKeySelective(video);
    }
}
