package com.xueersi.parentsmeeting.modules.livevideo.englishname.business;

import android.content.Context;

import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.lib.framework.utils.XSAsykTask;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class EnglishNameBusiness {

    Context mContext;

    EnlishNameParser mEnlishNameParser;
    public EnglishNameBusiness (Context mContext){
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
