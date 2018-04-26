package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * lottie 资源
 */
public class LottieEffectInfo {

    private String imgDir;//文件夹位置
    private String jsonFilePath; // json文件路径;
    private List<String> targetFileName;

    public String getImgDir() {
        return imgDir;
    }

    public LottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        this.imgDir = imgDir;
        this.jsonFilePath = jsonFilePath;
        this.targetFileName = Arrays.asList(targetFileNames);
    }

    public Bitmap fetchBitmap(String fileName, int width, int height) {
        Bitmap resultBitMap = null;
        if (targetFileName != null && targetFileName.contains(fileName)) {
            return fetchTargetBitMap(fileName, width, height);
        } else {
            InputStream in = null;
            try {
                in = new FileInputStream(imgDir + File.separator + fileName);
                resultBitMap = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return resultBitMap;
    }

    /**
     * 获取 json 文件字符串内容
     * @return
     */
    public String getJsonStr() {
        String jsonStr = null;
        BufferedReader reader = null;
        try {
            InputStream in = new FileInputStream(jsonFilePath);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            jsonStr = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonStr;
    }

    /**
     * 处理需 动态生成的图片
     *
     * @param fileName
     * @param width
     * @param height
     * @return
     */
    public Bitmap fetchTargetBitMap(String fileName, int width, int height) {
        return null;
    }

}
