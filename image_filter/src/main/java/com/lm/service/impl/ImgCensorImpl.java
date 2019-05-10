package com.lm.service.impl;

/**
 * @Author: LiMing
 * @Date: 2019/4/24 14:55
 * @Description:
 **/
/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */

import com.alibaba.fastjson.JSONObject;
import com.lm.redis.RedisCacheManager;
import com.lm.service.ImgCensor;
import com.lm.token.AuthService;
import com.lm.util.Base64Util;
import com.lm.util.GsonUtils;
import com.lm.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AIP图像组合API
 */
@Service
public class ImgCensorImpl implements ImgCensor {

    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */


    @Autowired
    RedisCacheManager redisCacheManager;


    @Autowired
    AuthService authService;


    // 官网获取的 API Key 更新为你注册的
    @Value("${API_KEY}")
    private  String API_KEY;


    // 官网获取的 Secret Key 更新为你注册的
    @Value("${SECRET_KEY}")
    private  String SECRET_KEY;


    @Value("${APIurl}")
    private String APIurl;


    public JSONObject ImageAll(String path)  {
        // 图像组合APIurl
        String imgCensorUrl = APIurl;
        //String filePath = "########本地图片文件#######";
        try {
            //请求参数
           /* Map<String, Object> sceneConf = new HashMap<String, Object>();
            Map<String, Object> ocrConf = new HashMap<String, Object>();
            ocrConf.put("recognize_granularity", "big");
            ocrConf.put("language_type", "CHN_ENG");
            ocrConf.put("detect_direction", true);
            ocrConf.put("detect_language", true);
            sceneConf.put("ocr", ocrConf);*/

            Map<String, Object> input = new HashMap<String, Object>();
            List<Object> scenes = new ArrayList<Object>();
            /*
            //通用文字识别
            scenes.add("ocr");
            //
            scenes.add("face");
            scenes.add("public");
            //政治敏感识别
            scenes.add("politician");*/
            //色情识别
            scenes.add("antiporn");
            //暴恐识别
           /* scenes.add("terror");
            scenes.add("webimage");
            scenes.add("disgust");
            //广告检测
            scenes.add("watermark");*/
            String imageUrl = path;
            //System.out.println(path);
            byte[] imgData = getImageFromNetByUrl(imageUrl);
            String imgStr = Base64Util.encode(imgData);
            input.put("image", imgStr);//与image二者选一
            //input.put("imgUrl", imageUrl);//与image二者选一
            input.put("scenes", scenes);
            //input.put("sceneConf", sceneConf);

            String params = GsonUtils.toJson(input);
            //System.out.println(params);
            /**
             * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
             */
            String accessToken;

            //从redis中获取token
            Object accessToken1 = redisCacheManager.get("accessToken");


            if(accessToken1==null){
                List<String> list = authService.getAuth(API_KEY, SECRET_KEY);
                accessToken=list.get(0);
                redisCacheManager.set("accessToken", accessToken,Integer.parseInt(list.get(1)));
            }else {
                accessToken=(String) accessToken1;
            }

            String result = HttpUtil.post(imgCensorUrl, accessToken, params);
            //System.out.println(result);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 压缩图片
     * @param strUrl
     * @return
     */
    public static byte[] getImageFromNetByUrl(String strUrl){
        try {
            URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            if(conn.getInputStream().available()!=0){
                InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
                //字节流转图片对象
                Image bi = ImageIO.read(inStream);
                //构建图片流
                BufferedImage tag = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
                //绘制改变尺寸后的图
                tag.getGraphics().drawImage(bi, 0, 0, 50, 50, null);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(tag, "jpg", os);
                InputStream is = new ByteArrayInputStream(os.toByteArray());
                byte[] btImg = readInputStream(is);//得到图片的二进制数据
                return btImg;
            }else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024*3];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }
}

