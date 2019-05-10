package com.lm.web;

import com.lm.service.ItemsService;


import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: LiMing
 * @Date: 2019/1/17 21:03
 * @Description:
 **/
@RestController
@RequestMapping("/image")
public class ItemsController {

    @Autowired
    private ItemsService itemsService;


    @Autowired
    private com.lm.service.impl.ImgCensorImpl imgCensor;

    @RequestMapping("/findAll")
    public void findAll(@RequestParam(value = "path") String path, HttpServletRequest request, HttpServletResponse response){
        //String path = ItemsController.class.getClassLoader().getResource("images/1.jpg").getPath();

        //System.out.println(path);
       /* ExecutorService exec = Executors.newCachedThreadPool();//工头
        Future<JSONObject> submit = exec.submit(new MyThread(path));*/
        JSONObject jsonObject = itemsService.checkPornograp(path);
        response.setContentType("text/html;charset=utf-8");
        try {
            if(jsonObject!=null){
                response.getWriter().write(jsonObject.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("/findAllImage")
    public void findAllImage(String path , HttpServletRequest request, HttpServletResponse response){
        //String path = ItemsController.class.getClassLoader().getResource("images/1.jpg").getPath();

        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<String> strings = parameterMap.keySet();
        List<String> key = new ArrayList<String>();
        List<String> value = new ArrayList<String>();
        for (String string : strings) {
            key.add(string);
            value.add(parameterMap.get(string)[0]);
        }
        String str="";
        if(key.size()>1){
            for (int i = 0; i < key.size(); i++) {
                if(i==0){
                    str+=value.get(i)+"&";
                }else if(i==key.size()-1){
                    str+=key.get(i)+"="+value.get(i);
                }
                else {
                    str+=key.get(i)+"="+value.get(i)+"&";
                }
            }
        }else {
            str=value.get(0);
        }

        //System.out.println(str);
        //System.out.println(path);
        //System.out.println(path);
       /* ExecutorService exec = Executors.newCachedThreadPool();//工头
        Future<JSONObject> submit = exec.submit(new MyThread(path));*/
        com.alibaba.fastjson.JSONObject jsonObject = imgCensor.ImageAll(str);
        response.setContentType("text/html;charset=utf-8");
        try {
            if(jsonObject!=null){
                response.getWriter().write(jsonObject.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
