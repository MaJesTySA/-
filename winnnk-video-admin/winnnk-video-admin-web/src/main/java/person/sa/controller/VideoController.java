package person.sa.controller;

import com.sun.org.apache.bcel.internal.generic.JSR;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import person.sa.enums.VideoStatusEnum;
import person.sa.pojo.Bgm;
import person.sa.service.VideoService;
import person.sa.utils.JSONResult;
import person.sa.utils.PagedResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Controller
@RequestMapping("video")
public class VideoController {

    @Value("${FILE_SPACE}")
    private String FILE_SPACE;
    @Autowired
    private VideoService videoService;

    @GetMapping("/showAddBgm")
    public String showAddBgm(){
        return "video/addBgm";
    }

    @GetMapping("/showBgmList")
    public String showBgmList(){
        return "video/bgmList";
    }

    @PostMapping("/queryBgmList")
    @ResponseBody
    public PagedResult queryBgmList(Integer page){
        return videoService.queryBgmList(page,10);
    }

    @GetMapping("/showReportList")
    public String showReportList() {
        return "video/reportList";
    }

    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page) {
        PagedResult result = videoService.queryReportList(page, 10);
        return result;
    }

    @PostMapping("/forbidVideo")
    @ResponseBody
    public JSONResult forbidVideo(String videoId) {
        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
        return JSONResult.ok();
    }

    @PostMapping("/delBgm")
    @ResponseBody
    public JSONResult delBgm(String bgmId){
        videoService.deleteBmg(bgmId);
        return JSONResult.ok();
    }

    @PostMapping("/bgmUpload")
    @ResponseBody
    public JSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {
        // 文件保存的命名空间
//		String fileSpace = File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
//		String fileSpace = "C:" + File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
        // 保存到数据库中的相对路径
       // String uploadPathDB = File.separator + "bgm";
        String uploadPathDB = "/bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    //String finalPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
                    // C:\\projects\\winnnkvideo\\mvc-bgm/bgm/music.mp3
                    String finalPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                   // uploadPathDB += (File.separator + fileName);
                    // /bgm/music.mp3
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return JSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        return JSONResult.ok(uploadPathDB);
    }
    //这里没有Transactional事物，因为已经被切面切到了，添加了事物
    @PostMapping("/addBgm")
    @ResponseBody
    public JSONResult addBgm(Bgm bgm){
        videoService.addBgm(bgm);
        return JSONResult.ok();
    }
}
