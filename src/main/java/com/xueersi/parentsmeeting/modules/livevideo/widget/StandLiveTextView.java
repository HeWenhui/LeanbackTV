package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.tencent.cos.xml.utils.StringUtils;

/**
 * Created by linyuqiang on 2018/4/28.
 * 站立直播，4个中文，8个英文字体
 */
public class StandLiveTextView extends TextView {

    public StandLiveTextView(Context context) {
        super(context);
//        setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    public StandLiveTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        String str = text.toString();
        String newText = getShortName(str);
        super.setText(newText, type);
    }

    /**
     * 站立直播名称显示，中文4个，英文8个
     *
     * @param name
     * @return
     */
    public static String getShortName(String name) {
        if (name == null) {
            return "";
        }
        String newText = name;
        boolean isChinese = isChinese(name);
        int length = name.length();
        if (isChinese) {
            if (length > 4) {
                newText = name.substring(0, 4) + "...";
            }
        } else {
            if (length > 8) {
                newText = name.substring(0, 8) + "...";
            }
        }
        return newText;
    }

    public static boolean isChinese(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            byte[] bytes = ("" + chars[i]).getBytes();
            if (bytes.length > 2) {
                return true;
            }
        }
        return false;
    }

}
