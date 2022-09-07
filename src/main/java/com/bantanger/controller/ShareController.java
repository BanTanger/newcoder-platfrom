package com.bantanger.controller;

import com.bantanger.config.WkConfig;
import com.bantanger.entity.Event;
import com.bantanger.event.EventProducer;
import com.bantanger.util.CommunityConstant;
import com.bantanger.util.CommunityUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Description
 * @Date 2022/9/6 18:08
 */

@Controller
public class ShareController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(WkConfig.class);

    @Autowired
    private EventProducer eventProducer;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${wk.image.storage}")
    private String wkImageStorage;

    /**
     * 异步方式，因为生成图片的周期很长
     * 设置事件，将逻辑丢个kafka执行
     *
     * @return
     */
    @GetMapping("/share")
    @ResponseBody
    public String share(String htmlUrl) {
        // 文件名随机
        String fileName = CommunityUtil.generateUUID();

        Event event = new Event()
                .setTopic(TOPIC_SHARE)
                .setData("htmlUrl", htmlUrl)
                .setData("fileName", fileName)
                .setData("suffix", ".png");
        eventProducer.fireEvent(event);

        // 返回访问路径
        HashMap<String, Object> map = new HashMap<>();
        map.put("shareUrl", domain + contextPath + "/share/image/" + fileName);

        return CommunityUtil.getJSONString(0, "操作成功", map);
    }

    @GetMapping("/share/image/{fileName}")
    @ResponseBody
    public void getShareImage(@PathVariable("fileName") String fileName,
                              HttpServletResponse response) {
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("文件名不能为空！");
        }
        // 生成图片的名称
        File file = new File(wkImageStorage + "/" + fileName + ".png");
        // 设置response响应类型为图片
        response.setContentType("image/png");

        try ( // io流读取图片
              OutputStream os = response.getOutputStream();
              FileInputStream fis = new FileInputStream(file);
        ) {
            byte[] buffer = new byte[1024]; // 缓冲区设置
            int b = 0; // 游标设置
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取长图失败：" + e.getMessage());
        }

    }
}
