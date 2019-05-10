package com.lm.service.impl;

import com.baidu.aip.imagecensor.AipImageCensor;
import com.lm.service.ItemsService;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Author: LiMing
 * @Date: 2019/1/18 18:47
 * @Description:
 **/
@Service
public class ItemsServiceImpl implements ItemsService {


    //设置APPID/AK/SK
    /*private  String APP_ID=null;
    private  String API_KEY = null;
    private  String SECRET_KEY=null;*/
    private static final  String APP_ID="14822410";
    private static final  String API_KEY = "GrK2LW7BXsGsUIBSB1yVkDgF";
    private static final String SECRET_KEY = "WTe9i0tga0lWjdinrFhauLxpOrGnkDUm";

    public JSONObject checkPornograp(String path) {

        JSONObject res=null;
        byte[] btImg = getImageFromNetByUrl(path);
        try {

            AipImageCensor client = new AipImageCensor(APP_ID, API_KEY, SECRET_KEY);
            // 可选：设置网络连接参数
            client.setConnectionTimeoutInMillis(60000);
            client.setSocketTimeoutInMillis(60000);
            // 调用接口
            if(btImg!=null){
                res= client.antiPorn(btImg);
            }
            //System.out.println(res.toString(2));
        } catch (Exception e) {
            //checkPornograp(path);
            System.out.println(e);
        }
        return res;
    }

    /**
     * 根据地址获得数据的字节流
     * @param strUrl 网络连接地址
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
                BufferedImage tag = new BufferedImage(25, 25, BufferedImage.TYPE_INT_RGB);
                //绘制改变尺寸后的图
                tag.getGraphics().drawImage(bi, 0, 0, 25, 25, null);
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
