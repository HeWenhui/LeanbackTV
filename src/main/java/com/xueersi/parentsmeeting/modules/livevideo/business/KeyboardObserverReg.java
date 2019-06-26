package com.xueersi.parentsmeeting.modules.livevideo.business;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.ProxUtil;
import com.xueersi.parentsmeeting.modules.livevideo.widget.KeyboardPopWindow;

import java.util.ArrayList;

import cn.dreamtobe.kpswitch.IPanelHeightTarget;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;

/**
 * Created by linyuqiang on 2018/8/2.
 * 键盘事件
 */
public class KeyboardObserverReg implements LiveProvide {
    Logger logger = LoggerFactory.getLogger("KeyboardObserverReg");
    ArrayList<KeyboardPopWindow.KeyboardObserver> observers = new ArrayList<>();
    private KeyboardPopWindow keyboardPopWindow;
    Activity activity;

    public KeyboardObserverReg(Activity activity) {
        this.activity = activity;
        ProxUtil.getProxUtil().put(activity, KeyboardObserverReg.class, this);
    }

    public void addKeyboardObserver(KeyboardPopWindow.KeyboardObserver keyboardObserver) {
        observers.add(keyboardObserver);
    }

    public void removeKeyboardObserver(KeyboardPopWindow.KeyboardObserver keyboardObserver) {
        observers.remove(keyboardObserver);
    }

    public void initView(final View view) {
        KeyboardUtil.attach(activity, new IPanelHeightTarget() {
            @Override
            public void refreshHeight(int panelHeight) {

            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public void onKeyboardShowing(boolean showing) {

            }
        }, new KeyboardUtil.OnKeyboardShowingListener() {
            @Override
            public void onKeyboardShowing(boolean isShowing) {
                int height = 0;
                if (isShowing) {
                    height = KeyboardUtil.getValidPanelHeight(activity);
                }
                logger.d("onKeyboardShowing:isShowing=" + isShowing + ",height=" + height);
                for (int i = 0; i < observers.size(); i++) {
                    observers.get(i).onKeyboardHeightChanged(height, Configuration.ORIENTATION_LANDSCAPE);
                }
            }
        });
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
