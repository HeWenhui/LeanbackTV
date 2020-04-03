package com.xueersi.parentsmeeting.modules.livevideo.message.business;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by linyuqiang on 2017/2/9.
 */

public class LiveMessageEmojiParser {

    public static HashMap<String, Integer> map = new HashMap<>();

    static {
        map.put("[e]em_1[e]", R.drawable.emoji_1f60a);
        map.put("[e]em_2[e]", R.drawable.emoji_1f604);
        map.put("[e]em_3[e]", R.drawable.emoji_1f633);
        map.put("[e]em_4[e]", R.drawable.emoji_1f60c);
        map.put("[e]em_5[e]", R.drawable.emoji_1f601);
        map.put("[e]em_6[e]", R.drawable.emoji_1f61d);
        map.put("[e]em_7[e]", R.drawable.emoji_1f625);
        map.put("[e]em_8[e]", R.drawable.emoji_1f623);
        map.put("[e]em_9[e]", R.drawable.emoji_1f628);
        map.put("[e]em_10[e]", R.drawable.emoji_1f632);
        map.put("[e]em_11[e]", R.drawable.emoji_1f62d);
        map.put("[e]em_12[e]", R.drawable.emoji_1f602);
        map.put("[e]em_13[e]", R.drawable.emoji_1f631);
        map.put("[e]em_14[e]", R.drawable.emoji_1f47f);
        map.put("[e]em_15[e]", R.drawable.emoji_1f44d);
        map.put("[e]em_16[e]", R.drawable.emoji_1f44c);
        map.put("[e]em_17[e]", R.drawable.emoji_270c);

        map.put("[e]em_18[e]", R.drawable.emoji_heart);
        map.put("[e]em_19[e]", R.drawable.emoji_sml);
        map.put("[e]em_20[e]", R.drawable.emoji_get);
        map.put("[e]em_get[e]", R.drawable.emoji_hff_list);
    }

    /**
     * 把字符串进行转义（表情显示图片）
     *
     * @param content
     * @param mContext
     * @param bounds   噢！是写错了，已经加进去了[e]1f60a[/e][e]1f60a[/e][e]1f60a[/e][e]1f33a[/e][e]1f33a[/e]
     * @return [e]em_2[e] \[e\](.*?)\[/e\]
     */
    public static SpannableStringBuilder convertToHtml(String content, Context mContext, int bounds) {
        String regex = "\\[e\\]em(.*?)\\[e\\]";
        Pattern pattern = Pattern.compile(regex);
        String emo = "";
        Resources resources = mContext.getResources();
        Matcher matcher = pattern.matcher(content);
        SpannableStringBuilder sBuilder = new SpannableStringBuilder(content);
        Drawable drawable = null;
        ImageSpan span = null;
        while (matcher.find()) {
            emo = matcher.group();
            try {
                int id = map.get(emo);
                if (id != 0) {
                    drawable = resources.getDrawable(id);
                    int scale = 1;
                    if (drawable != null) {
                        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
                            scale = drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                        }
                        drawable.setBounds(0, 0, bounds * scale, bounds);
                        span = new VerticalImageSpan(drawable);
                        sBuilder.setSpan(span, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        return sBuilder;
    }
}
