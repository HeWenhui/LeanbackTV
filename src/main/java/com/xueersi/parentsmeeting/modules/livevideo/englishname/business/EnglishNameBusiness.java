package com.xueersi.parentsmeeting.modules.livevideo.englishname.business;

import android.content.Context;
import android.text.TextUtils;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.BaseBll;
import com.xueersi.common.business.UserBll;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.XSAsykTask;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.internal.RtcEngineMessage;

public class EnglishNameBusiness extends BaseBll {

    Context mContext;

    EnlishNameParser mEnlishNameParser;
    public EnglishNameBusiness (Context mContext){
        super(mContext);
    this.mContext = mContext;
        mEnlishNameParser = new EnlishNameParser();
    }

    /**
     * 读取地区信息
     *
     * @return
     */
    public void getDefaultName(final List<EngLishNameEntity> indexList,final int sexType, final AbstractBusinessDataCallBack cityCallBack) {
        new XSAsykTask() {
            @Override
            public void preTask() {
            }

            @Override
            public void postTask() {
                String areaText = getStringFromAssets("generate.txt");
               // CityEntity entity = mPsswordSettingHttpResponseParser.getCityLst(areaText);
                List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(areaText,sexType,indexList);
                cityCallBack.onDataSucess(nameList);
            }

            @Override
            public void doInBack() {

            }
        }.execute(true);
    }

    public void checkName(){
        mShareDataManager.put(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK,false,ShareDataManager.SHAREDATA_USER);
        String userName = UserBll.getInstance().getMyUserInfoEntity().getEnglishName();
        if(TextUtils.isEmpty(userName)) {
            return;
        }
        List<EngLishNameEntity>  listName = new ArrayList<>();
        List<EngLishNameEntity> listIndex = new ArrayList<>();
        EngLishNameEntity entity = null;
        String[] word = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        EngLishNameEntity index = null;
        String areaText = getStringFromAssets("generate.txt");
        int indexPosition = 0;
        for (int j = 0; j < word.length; j++) {
            index = new EngLishNameEntity();
            index.setWordIndex(word[j]);
            listIndex.add(index);
        }
        List<EngLishNameEntity> nameList = mEnlishNameParser.pareseEnglishName(areaText,1,listIndex);
        List<EngLishNameEntity> nameList2 = mEnlishNameParser.pareseEnglishName(areaText,2,listIndex);
        nameList.addAll(nameList2);

        for (int i = 0; i < nameList.size(); i++) {
            if(TextUtils.equals(nameList.get(i).getName(),userName)) {
                mShareDataManager.put(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK,true,ShareDataManager.SHAREDATA_USER);
                return;
            }
        }
    }



    /***
     * 读取XML
     *
     * @param fileName
     * @return
     */
    private String getStringFromAssets(String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(mContext.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
