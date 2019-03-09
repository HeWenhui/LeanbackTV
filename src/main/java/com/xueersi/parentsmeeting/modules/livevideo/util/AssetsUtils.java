package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetsUtils {

    /**
     * 从assets 中获取字符串
     *
     * @param context
     * @return
     */
    public static String getJsonStrFromAssets(Context context, String jsonFilePath) {
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
}
