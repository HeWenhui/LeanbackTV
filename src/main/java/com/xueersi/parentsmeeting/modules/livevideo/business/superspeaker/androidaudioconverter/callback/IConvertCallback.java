package com.xueersi.parentsmeeting.modules.livevideo.business.superspeaker.androidaudioconverter.callback;

import java.io.File;

public interface IConvertCallback {
    
    void onSuccess(File convertedFile);
    
    void onFailure(Exception error);

}