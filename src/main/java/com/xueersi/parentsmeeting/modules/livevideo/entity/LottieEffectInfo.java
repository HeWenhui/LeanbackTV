package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.airbnb.lottie.LottieAnimationView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @author chenkun
 * lottie 资源处理 封装
 */
public class LottieEffectInfo {

    /**
     * 文件夹位置
     **/
    private String imgDir;
    /**
     * json文件路径
     **/
    private String jsonFilePath;
    /**
     * 需要动态生成的 图片名称
     **/
    private List<String> targetFileName;

    public String getImgDir() {
        return imgDir;
    }

    public LottieEffectInfo(String imgDir, String jsonFilePath, String... targetFileNames) {
        this.imgDir = imgDir;
        this.jsonFilePath = jsonFilePath;
        this.targetFileName = Arrays.asList(targetFileNames);
    }

    public void setTargetFileFilter(String[] targetFileNames) {
        this.targetFileName = Arrays.asList(targetFileNames);
    }

    public Bitmap fetchBitmap(LottieAnimationView animationView, String fileName,
                              String bitmapId, int width, int height) {
        Bitmap resultBitMap = null;
        if (targetFileName != null && targetFileName.contains(fileName)) {
            return fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
        } else {
            resultBitMap = getBitMap(fileName);
        }
        return resultBitMap;
    }

    protected Bitmap getBitMap(String fileName) {
        Bitmap resultBitMap = null;
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
        return resultBitMap;
    }


    /**
     * 从asset 中 获取lottie所需要的图片
     *
     * @param fileName
     * @param width
     * @param height
     * @param context
     * @return
     */
    public Bitmap fetchBitmapFromAssets(LottieAnimationView animationView,
                                        String fileName, String bitmapId, int width,
                                        int height, Context context) {
        Bitmap resultBitMap = null;
        if (targetFileName != null && targetFileName.contains(fileName)) {
            return fetchTargetBitMap(animationView, fileName, bitmapId, width, height);
        } else {
            InputStream in = null;
            try {
                in = context.getAssets().open(imgDir + File.separator + fileName);
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
     * 从assets文件夹中获取图片资源
     * @param fileName
     * @param context
     * @return
     */
    protected Bitmap getBitMapFromAssets(String fileName, Context context) {
        Bitmap resultBitMap = null;
        InputStream in = null;
        try {
            in = context.getAssets().open(imgDir + File.separator + fileName);
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
        return resultBitMap;
    }


    /**
     * 获取 json 文件字符串内容
     *
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
     * 从assets 中获取 json
     *
     * @param context
     * @return
     */
    public String getJsonStrFromAssets(Context context) {
        String jsonStr = null;
        BufferedReader reader = null;
        try {
            InputStream in = context.getAssets().open(jsonFilePath);
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
    public Bitmap fetchTargetBitMap(LottieAnimationView animationView, String fileName,
                                    String bitmapId, int width, int height) {
        return null;
    }


    public static Bitmap scaleBitmap(Bitmap input, float scaleRatio) {
        Bitmap result = null;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleRatio, scaleRatio);
        result = Bitmap.createBitmap(input, 0, 0,
                input.getWidth(), input.getHeight(), matrix, true);
        return result;
    }


    public static Bitmap circleBitmap(Bitmap input, int radius) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            Rect src = new Rect(0, 0, input.getWidth(), input.getHeight());
            Rect dst = new Rect(0, 0, radius * 2, radius * 2);
            Path path = new Path();
            path.addCircle(radius, radius, radius, Path.Direction.CCW);
            canvas.clipPath(path);
            Paint paint = new Paint();
            paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            canvas.drawBitmap(input, src, dst, paint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
