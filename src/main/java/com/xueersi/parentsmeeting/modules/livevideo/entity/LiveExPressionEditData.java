package com.xueersi.parentsmeeting.modules.livevideo.entity;

import android.annotation.SuppressLint;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.widget.expressionView.entity.ExpressionAllInfoEntity;
import com.xueersi.parentsmeeting.widget.expressionView.entity.ExpressionInfoEntity;
import com.xueersi.parentsmeeting.widget.expressionView.manager.ExPressionEditDataInter;
import com.xueersi.parentsmeeting.widget.expressionView.util.Expressions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linyuqiang 直播表情编辑数据类
 */

public class LiveExPressionEditData implements ExPressionEditDataInter {

    /*** 创建表情分组list集合 */
    private List<ExpressionInfoEntity> mExpressionList;
    /** 创建表情分组map集合 */
    private Map<String, ExpressionInfoEntity> mExPressionMap;
    /** 创建表情所有list集合 */
    private List<ExpressionAllInfoEntity> mExpressionAllInfoLists;
    /** 创建表情所有map集合 */
    private Map<Integer, ExpressionAllInfoEntity> mExPressionAllMaps;
    /*** 普通表情的图片地址||底部图标资源地址 */
    private int[] expressionImages, expressionJpgImages, expressionGifImages;
    /*** 普通表情id和动态表情id */
    private String[] expressionIds, expressionGifIds;
    /*** 底部id */
    private int[] bottomImageResources;
    /*** 底部图标资源id */
    private int[] bottomResourceIds;
    /*** 普通表情名称和动态表情名称||底部图标资源名称 */
    private String[] expressionImageNames, expressionGifNames, bottomImageNames;


    public LiveExPressionEditData() {
        initNativeExpression();
        initFillExpressionList();
    }

    /**
     * 初始化表情本地资源
     */
    @SuppressLint("UseSparseArrays")
    private void initNativeExpression() {
        expressionImages = new int[]{R.drawable.emoji_1f60a, R.drawable.emoji_1f604, R.drawable.emoji_1f633,
                R.drawable.emoji_1f60c, R.drawable.emoji_1f601, R.drawable.emoji_1f61d, R.drawable.emoji_1f625,
                R.drawable.emoji_1f623, R.drawable.emoji_1f628, R.drawable.emoji_1f632, R.drawable.emoji_1f62d,
                R.drawable.emoji_1f602, R.drawable.emoji_1f631, R.drawable.emoji_1f47f, R.drawable.emoji_1f44d,
                R.drawable.emoji_1f44c, R.drawable.emoji_270c};
        expressionIds = Expressions.expressionImgIds;
        expressionImageNames = new String[]{"[e]em_1[e]","[e]em_2[e]","[e]em_3[e]","[e]em_4[e]","[e]em_5[e]",
                "[e]em_6[e]","[e]em_7[e]","[e]em_8[e]","[e]em_9[e]","[e]em_10[e]","[e]em_11[e]","[e]em_12[e]",
                "[e]em_13[e]","[e]em_14[e]","[e]em_15[e]","[e]em_16[e]","[e]em_17[e]"};

        expressionJpgImages = Expressions.exPressionJpgImages;
        expressionGifImages = Expressions.exPressionGifImages;
        expressionGifIds = Expressions.exPressionIds;
        expressionGifNames = Expressions.exPressionNames;

        bottomImageResources = Expressions.bottomImageResource;
        bottomResourceIds = Expressions.imageResourceId;
        bottomImageNames = Expressions.imageResourceName;
        mExpressionAllInfoLists = new ArrayList<ExpressionAllInfoEntity>();
        mExPressionAllMaps = new HashMap<Integer, ExpressionAllInfoEntity>();
    }

    private void initFillExpressionList() {
        mExpressionList = new ArrayList<ExpressionInfoEntity>();
        mExPressionMap = new HashMap<String, ExpressionInfoEntity>();

        // 填充普通表情数据
        for (int i = 0; i < expressionImages.length; i++) {
            mExpressionList
                    .add(new ExpressionInfoEntity(expressionIds[i], expressionImageNames[i], expressionImages[i]));
            mExPressionMap.put(expressionIds[i], new ExpressionInfoEntity(expressionIds[i], expressionImageNames[i],
                    expressionImages[i]));
        }
        ExpressionAllInfoEntity mEAllPtInfoEntity = new ExpressionAllInfoEntity(mExpressionList);
        mEAllPtInfoEntity.setExpressionNum(17);
        mEAllPtInfoEntity.setBottomImageId(bottomResourceIds[0]);
        mEAllPtInfoEntity.setBottomImageName(bottomImageNames[0]);
        mEAllPtInfoEntity.setBackgroundResource(bottomImageResources[0]);
        mEAllPtInfoEntity.setCatogaryId(Expressions.exPressionCatogary);
        mEAllPtInfoEntity.setExpressionInfoList(mExpressionList);
        mEAllPtInfoEntity.setExpressionInfoMap(mExPressionMap);
        mExpressionAllInfoLists.add(mEAllPtInfoEntity);
        mExPressionAllMaps.put(bottomResourceIds[0], mEAllPtInfoEntity);
        mExpressionList = new ArrayList<ExpressionInfoEntity>();
        mExPressionMap = new HashMap<String, ExpressionInfoEntity>();
        //填充gif数据
        for (int i = 0; i < expressionGifImages.length; i++) {
            mExpressionList.add(new ExpressionInfoEntity(expressionGifIds[i], expressionGifImages[i],
                    expressionJpgImages[i], expressionGifNames[i]));
            mExPressionMap.put(expressionGifIds[i], new ExpressionInfoEntity(expressionGifIds[i],
                    expressionGifImages[i], expressionJpgImages[i], expressionGifNames[i]));
        }

        ExpressionAllInfoEntity mEAllGifInfoEntity = new ExpressionAllInfoEntity(mExpressionList);
        mEAllGifInfoEntity.setExpressionNum(8);
        mEAllGifInfoEntity.setBottomImageId(bottomResourceIds[1]);
        mEAllGifInfoEntity.setBottomImageName(bottomImageNames[1]);
        mEAllGifInfoEntity.setBackgroundResource(bottomImageResources[1]);
        mEAllGifInfoEntity.setCatogaryId(Expressions.exGifCatogary);
        mEAllGifInfoEntity.setExpressionInfoList(mExpressionList);
        mEAllGifInfoEntity.setExpressionInfoMap(mExPressionMap);
//        mExpressionAllInfoLists.add(mEAllGifInfoEntity);
//        mExPressionAllMaps.put(bottomResourceIds[1], mEAllGifInfoEntity);
    }

    /**
     * @param mExpressionAllInfoLists
     * @author zxm
     * 如果扩展，创建的时候初始化的保留，将传进来的应该是全部加入,现在只是这么写，若扩展，重写set方法
     */
    public void setmExpressionAllInfoLists(List<ExpressionAllInfoEntity> mExpressionAllInfoLists) {
        this.mExpressionAllInfoLists = mExpressionAllInfoLists;
    }



    /**
     * @param mExPressionAllMaps
     * @author zxm
     * 如果扩展，创建的时候初始化的保留，这传进来的应该是全部加入，现在只是这么写，若扩展，重写set方法
     */
    public void setmExPressionAllMaps(Map<Integer, ExpressionAllInfoEntity> mExPressionAllMaps) {
        this.mExPressionAllMaps = mExPressionAllMaps;
    }

    /**
     * 获取表情list集合数据，为ExpressionManager调用
     *
     * @return
     * @author jixu
     */
    @Override
    public List<ExpressionAllInfoEntity> getExpressionAllList() {
        return mExpressionAllInfoLists;
    }

    /**
     * 获取表情map集合数据，为ExpressionManager调用
     *
     * @return
     * @author jixu
     */
    @Override
    public Map<Integer, ExpressionAllInfoEntity> getExpressionAllMap() {
        return mExPressionAllMaps;
    }

}
