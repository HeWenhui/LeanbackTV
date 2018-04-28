package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

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
        String newText = str;
        boolean isChinese = isChinese(str);
        int length = str.length();
        if (isChinese) {
            if (length > 4) {
                newText = str.substring(0, 4) + "...";
            }
        } else {
            if (length > 8) {
                newText = str.substring(0, 8) + "...";
            }
        }
        super.setText(newText, type);
    }

    protected boolean isChinese(String str) {
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
