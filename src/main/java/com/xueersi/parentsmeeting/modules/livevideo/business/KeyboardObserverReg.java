package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.KeyboardPopWindow;

import java.util.ArrayList;

/**
 * Created by lyqai on 2018/8/2.
 */

public class KeyboardObserverReg {
    Logger logger = LoggerFactory.getLogger("KeyboardObserverReg");
    ArrayList<KeyboardPopWindow.KeyboardObserver> observers = new ArrayList<>();
    private KeyboardPopWindow keyboardPopWindow;
    Activity activity;

    public KeyboardObserverReg(Activity activity) {
        this.activity = activity;
//        ProxUtil.getProxUtil().put(activity, KeyboardObserverReg.class, this);
    }

    public void addKeyboardObserver(KeyboardPopWindow.KeyboardObserver keyboardObserver) {
        observers.add(keyboardObserver);
    }

    public void removeKeyboardObserver(KeyboardPopWindow.KeyboardObserver keyboardObserver) {
        observers.remove(keyboardObserver);
    }

    public void initView(final View view) {
//        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            @Override
//            public boolean onPreDraw() {
//                view.getViewTreeObserver().removeOnPreDrawListener(this);
//                keyboardPopWindow = new KeyboardPopWindow(activity);
//                keyboardPopWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
//                keyboardPopWindow.setKeyboardObserver(new KeyboardPopWindow.KeyboardObserver() {
//                    @Override
//                    public void onKeyboardHeightChanged(int height, int orientation) {
//                        logger.d("onKeyboardHeightChanged:height=" + height + ",orientation=" + orientation);
//                        for (int i = 0; i < observers.size(); i++) {
//                            observers.get(i).onKeyboardHeightChanged(height, orientation);
//                        }
//                    }
//                });
//                return false;
//            }
//        });
    }
}
