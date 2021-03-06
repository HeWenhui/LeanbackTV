package com.xueersi.parentsmeeting.modules.livevideo.util;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

public class ViewUtil {

    /**
     * 得到子view在他上面ViewGroup的位置
     *
     * @param child
     * @param group
     * @return
     */
    public static int[] getLoc(View child, ViewGroup group) {
        int[] loc = new int[2];
        int left = child.getLeft();
        int top = child.getTop();
        ViewParent parent = child.getParent();
        while (parent != null && parent != group) {
            if (parent instanceof ViewGroup) {
                ViewGroup group2 = (ViewGroup) parent;
                left += group2.getLeft();
                top += group2.getTop();
                parent = parent.getParent();
            } else {
                break;
            }
        }
        loc[0] = left;
        loc[1] = top;
        return loc;
    }

    public static void setText(TextView textView, String text) {
        if (!("" + text).equals("" + textView.getText())) {
            textView.setText(text);
        }
    }

    public static void setViewVisibility(View v, int visibility) {
        if (v != null && v.getVisibility() != visibility) {
            v.setVisibility(visibility);
        }
    }
}
