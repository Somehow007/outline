package com.ujs.outline.controller;

import com.ujs.outline.domain.ImageCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

/**
 * 验证码控制类
 */
@RestController
public class ValidateCodeController {
    private static final Logger logger = LoggerFactory.getLogger(ValidateCodeController.class);

    //定义存入session的key
    public static final String SESSION_KEY = "SESSION_IMAGE_CODE";

    /** 处理session */
    private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/code/image")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setHeader("Cache-Control", "no-store");
        response.setContentType("image/jpeg");
        try {
            ServletWebRequest servletWebRequest = new ServletWebRequest(request);
            ImageCode imageCode = createImageCode(servletWebRequest);
            sessionStrategy.setAttribute(servletWebRequest,SESSION_KEY,imageCode);
            ImageIO.write(imageCode.getImage(), "JPEG", response.getOutputStream());
            logger.info("成功生成验证码");
        } catch (Exception e) {
            logger.error("生成验证码失败", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }


    }

    private ImageCode createImageCode(ServletWebRequest servletWebRequest) {
        int width = 67;
        int height = 23;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = image.getGraphics();

        Random random = new Random();

        g.setColor(getRandColor(200,250));
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        g.setColor(getRandColor(160,200));
        for(int i=0; i < 155; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            g.drawLine(x, y, x + xl, y + yl);
        }

        String sRand = "";
        for(int i =0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand += rand;
            g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            g.drawString(rand, 13 * i + 6, 16);

        }
        g.dispose();
        return new ImageCode(sRand, 60, image);
    }

    /**
     *  生成随机背景条纹
     * @param fc
     * @param bc
     * @return
     */
    private Color getRandColor(int fc, int bc) {

        Random random = new Random();
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt((bc - fc));
        int g = fc + random.nextInt((bc - fc));
        int b = fc + random.nextInt((bc - fc));
        return new Color(r, g, b);
    }
}