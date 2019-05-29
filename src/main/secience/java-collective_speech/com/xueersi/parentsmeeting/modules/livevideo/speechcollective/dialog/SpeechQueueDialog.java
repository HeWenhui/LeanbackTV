package com.xueersi.parentsmeeting.modules.livevideo.speechcollective.dialog;

import android.content.DialogInterface;

import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveLoggerFactory;
import com.xueersi.ui.dialog.BaseAlertDialog;

import java.util.ArrayList;
@Deprecated
public class SpeechQueueDialog {
    private static SpeechQueueDialog instance;
    private Logger logger = LiveLoggerFactory.getLogger("SpeechQueueDialog");

    public static SpeechQueueDialog getInstance() {
        if (instance == null) {
            instance = new SpeechQueueDialog();
        }
        return instance;
    }

    private BaseAlertDialog curBaseAlertDialog;
    private ArrayList<BaseAlertDialog> baseAlertDialogs = new ArrayList<>();

    public void showDialog(BaseAlertDialog baseAlertDialog) {
        logger.d("showDialog:cur=" + (curBaseAlertDialog == null));
        if (curBaseAlertDialog != null) {
            baseAlertDialogs.add(baseAlertDialog);
        } else {
            curBaseAlertDialog = baseAlertDialog;
            baseAlertDialog.setDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    curBaseAlertDialog = null;
                    logger.d("showDialog:size=" + baseAlertDialogs.size());
                    if (!baseAlertDialogs.isEmpty()) {
                        BaseAlertDialog dialog = baseAlertDialogs.remove(0);
                        showDialog(dialog);
                    }
                }
            });
            baseAlertDialog.showDialog(false, true);
        }
    }
}
