package com.xueersi.parentsmeeting.modules.livevideo.englishname;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieImageAsset;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.tal.user.fusion.entity.TalAccErrorMsg;
import com.tal.user.fusion.entity.TalAccReq;
import com.tal.user.fusion.entity.TalAccResp;
import com.tal.user.fusion.http.TalAccApiCallBack;
import com.tal.user.fusion.manager.TalAccApiFactory;
import com.xueersi.common.base.AbstractBusinessDataCallBack;
import com.xueersi.common.base.XesActivity;
import com.xueersi.common.sharedata.ShareDataManager;
import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.lib.framework.utils.XESToastUtils;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.activity.LiveVideoLoadActivity;
import com.xueersi.parentsmeeting.modules.livevideo.config.LiveVideoConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.business.EnglishNameBusiness;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.config.EnglishNameConfig;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.dialog.EnglishNameConfirmDialog;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.entity.EngLishNameEntity;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.item.SettingEnglishNameBarItem;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.item.SettingEnglishNameIndexItem;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.item.SettingEnglishNameItem;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.item.SettingEnglishNameSearchItem;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.item.SettingEnglishRemmondItem;
import com.xueersi.parentsmeeting.modules.livevideo.englishname.utils.EnglishNameListener;
import com.xueersi.parentsmeeting.modules.livevideo.entity.LottieEffectInfo;
import com.xueersi.parentsmeeting.modules.livevideo.page.item.RecyclerViewSpacesItemDecoration;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 设置英文名
 */
@Route(path = "/groupclass/englishname")
public class SettingEnglishLandActivity extends XesActivity {
    /**
     * 播放器请求
     */
    public static final int VIDEO_REQUEST = 210;
    /** 版本号 */
    private TextView tvVersion;

    /** 版权 */
    private TextView tvCopyRight;
    RCommonAdapter contentAdapter;
    /** 名字布局 */
    private RecyclerView recyclerView;
    private List<EngLishNameEntity> listName;
    private List<EngLishNameEntity> listRecommendName;
    private List<EngLishNameEntity> listIndex;
    private List<EngLishNameEntity> listSearchName = new ArrayList<>();

    /** 性别 */
    ImageView ivBoy,ivGirl;

    /** 名字导航 */
    // AreaIndexBarView areaIndexBarView;
    List<String> listWord;
//    List<AreaIndexBarView.IndexEntity> lstIndexEntity;

    LottieAnimationView mLottieView;
    LottieAnimationView lottieViewSex;
     RecyclerView recyclerViewIndex;
    RCommonAdapter contentAdapterIndex;

    RecyclerView rvRecommend;
    RCommonAdapter contentAdapterRecommend;

    CoordinatorLayout clNameContent;
    LinearLayout llControl;
     GridLayoutManager manager;
    TextView tvPreSex;
    TextView tvSubmit;
    String filePath = "file:///generate.txt";

    EnglishNameBusiness englishNameBll;

    EditText etSearch;

    RecyclerView recyclerSearch;
    RCommonAdapter adapterSearch;
    TextView tvRecommendHint;

    String selectName = "";
    int sex = 0;

    EnglishNameConfirmDialog englishNameConfirmDialog;

    ImageButton imgBtnClose;

    boolean isLive = true;
    String where = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(lp);
        setContentView(R.layout.layout_live_group_class_setting_english_name);
        englishNameBll = new EnglishNameBusiness(mContext);
         initData();
            //testData();
            initView();
            startLottie();
            initListener();

    }

    private void initData() {
        isLive = getIntent().getExtras().getBoolean("engish1v2Type",true);
        where= getIntent().getExtras().getString("where");
        boolean isNeed =mShareDataManager.getBoolean(LiveVideoConfig.LIVE_GOUP_1V2_ENGLISH_CHECK,false,ShareDataManager.SHAREDATA_USER);
        if(isNeed) {
            continueToVideo();
        }
    }

    private void continueToVideo(){
        if(isLive) {
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LiveVideoActivity.intentTo(SettingEnglishLandActivity.this, getIntent().getExtras());
        } else {
            com.xueersi.parentsmeeting.modules.livevideo.fragment.LivePlaybackVideoActivity.intentTo(SettingEnglishLandActivity.this, getIntent().getExtras(),

                    where, VIDEO_REQUEST);
        }
        finish();
    }

    private void initListener(){
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueToVideo();

            }
        });
        // 男孩选择
        ivBoy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexSelect();
                sex = LiveVideoConfig.LIVE_GROUP_CLASS_USER_SEX_BOY;
                testData();
            }
        });
        // 女孩选择
        ivGirl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sexSelect();
                sex = LiveVideoConfig.LIVE_GROUP_CLASS_USER_SEX_GIRL;
                testData();
            }
        });
        // 返回性别选择
        tvPreSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                secShow();
            }
        });
        // 提交名字选择
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  finish();
                if(TextUtils.isEmpty(selectName)) {
                 showDialog();
                 return;
                }
                TalAccReq.EditUserInfoReq req=new TalAccReq.EditUserInfoReq();
                req.sex = sex+"";
                req.en_name = selectName;
                TalAccApiFactory.getTalAccRequestApi().editUserInfo(req, new TalAccApiCallBack<TalAccResp.StringResp>() {
                    @Override
                    public void onSuccess(TalAccResp.StringResp stringResp) {
                        XESToastUtils.showToast(stringResp.result);
                        continueToVideo();

                    }

                    @Override
                    public void onError(TalAccErrorMsg resp) {
                        super.onError(resp);
                    }
                });
            }
        });
    }

    private void showDialog(){
        if(englishNameConfirmDialog==null) {
            englishNameConfirmDialog = new EnglishNameConfirmDialog(mContext,mBaseApplication,false);
        }
        englishNameConfirmDialog.initData("jack",englishNameListener);
        englishNameConfirmDialog.showDialog();
    }
    private void secShow(){
        ivBoy.setVisibility(View.VISIBLE);
        ivGirl.setVisibility(View.VISIBLE);
        lottieViewSex.playAnimation();
        clNameContent.setVisibility(View.GONE);
        llControl.setVisibility(View.GONE);
    }

    private void sexSelect(){
        ivBoy.setVisibility(View.GONE);
        ivGirl.setVisibility(View.GONE);
        lottieViewSex.playAnimation();
        clNameContent.setVisibility(View.VISIBLE);
        llControl.setVisibility(View.VISIBLE);
    }


    EnglishNameListener englishNameListener = new EnglishNameListener() {
        @Override
        public void select(int type, int position, String text) {
            selectName = text;
            // 推荐名字选中
            if(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_RECOMMEND == type) {
                selectRecomand(position,text);
                // 导航选中
            } else if(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_BAR == type) {
                selectIndex(position,text);

                // 名字选中
            } else if(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_SELECT == type) {
                selectName(position,text);

            }else if(EnglishNameConfig.GROUP_CLASS_ENGLISH_NAME_SEARCH == type) {
                selectSerch(position,text);
            }
        }

        @Override
        public void dialogCancel() {
            continueToVideo();


        }
    };



    private void initView() {
        recyclerView = findViewById(R.id.rv_setting_english_name_list);
        mLottieView = findViewById(R.id.lav_groupclass_setting_english_name_backgroud);
        lottieViewSex = findViewById(R.id.lav_groupclass_setting_english_name_sex);
        recyclerViewIndex = findViewById(R.id.rv_setting_english_name_index_list);
        ivBoy = findViewById(R.id.lv_groupclass_setting_english_name_sex_boy);
        ivGirl = findViewById(R.id.lv_groupclass_setting_english_name_sex_girl);
        clNameContent = findViewById(R.id.cdl_groupclass_setting_english_name_content);
        llControl = findViewById(R.id.ll_groupclass_setting_english_name_bottom);
        rvRecommend = findViewById(R.id.rv_setting_english_name_recommend);
        tvPreSex = findViewById(R.id.tv_groupclass_setting_english_name_pre_sex);
        tvSubmit = findViewById(R.id.tv_groupclass_setting_english_name_sumit_data);
        etSearch = findViewById(R.id.et_groupclass_setting_english_name_search);
        recyclerSearch= findViewById(R.id.rv_setting_english_name_search_list);
        tvRecommendHint = findViewById(R.id.tv_setting_english_name_recommend_hint);
        imgBtnClose = findViewById(R.id.imgbtn_live_setting_english_name_close);
        rvRecommend.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        recyclerViewIndex.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL,false));
        //      areaIndexBarView = findViewById(R.id.aiv_setting_english_name_list_index);
        manager = new GridLayoutManager(mContext, 4);
        manager.setSpanSizeLookup(new GridSpanSizeLookup());
        recyclerView.setLayoutManager(manager);
        GridLayoutManager searchManager = new GridLayoutManager(mContext, 4);
        searchManager.setSpanSizeLookup(new GridSpanSearchSizeLookup());
        recyclerSearch.setLayoutManager(searchManager);

        setRecyclerViewDecoration();
//        areaIndexBarView.setOnitemSelectListener(new AreaIndexBarView.OnitemSelectListener() {
//            @Override
//            public void onItemSelected(int position) {
//                if (position < 0 || position >= lstIndexEntity.size()) return;
//                AreaIndexBarView.IndexEntity indexEntity = lstIndexEntity.get(position);
//                int areaListIndex = indexEntity.getAreaListIndex();
//                manager.scrollToPositionWithOffset(areaListIndex, 0);
//            }
//        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof GridLayoutManager) {
                    GridLayoutManager manager = (GridLayoutManager) layoutManager;
                    int position = manager.findFirstVisibleItemPosition();
                    EngLishNameEntity engLishNameEntity = listName.get(position);
                    //   areaIndexBarView.selectIndex(engLishNameEntity.getBarIndex());
                    selectIndex(engLishNameEntity.getIndexPostion(),"");
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        etSearch.addTextChangedListener(onSearchChange);
    }
    TextWatcher onSearchChange = new TextWatcher() {

        private int strLength;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            setSearchData(arg0.toString());
        }
    };

    private void setSearchData(String text){
        selectName = "";
        if(TextUtils.isEmpty(text)) {
            listSearchName.clear();
            tvRecommendHint.setVisibility(View.VISIBLE);
            rvRecommend.setVisibility(View.VISIBLE);
            recyclerViewIndex.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerSearch.setVisibility(View.GONE);
        } else {
            listSearchName.clear();
            tvRecommendHint.setVisibility(View.GONE);
            rvRecommend.setVisibility(View.GONE);
            recyclerViewIndex.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            recyclerSearch.setVisibility(View.VISIBLE);
            EngLishNameEntity entity = null;
            for (int i = 0; i < listName.size(); i++) {
                if (!listName.get(i).isIndex()) {
                    if (listName.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                        entity = new EngLishNameEntity();
                        entity.setName(listName.get(i).getName());
                        listSearchName.add(entity);
                    }
                }
            }

        }
        fillSearchData();
    }


    private void fillSearchData(){
        if (adapterSearch == null) {
            adapterSearch = new RCommonAdapter(mContext, listSearchName);
            adapterSearch.addItemViewDelegate(4, new SettingEnglishNameSearchItem(mContext,englishNameListener));
            recyclerSearch.setAdapter(adapterSearch);
        } else {
            adapterSearch.updateData(listSearchName);
        }
    }



    private void startLottie() {
        final LottieEffectInfo bubbleEffectInfo = new LottieEffectInfo("liveroom_background/images",
                "liveroom_background/data.json");
        ImageAssetDelegate imageAssetDelegate = new ImageAssetDelegate() {
            @Override
            public Bitmap fetchBitmap(LottieImageAsset lottieImageAsset) {
                return bubbleEffectInfo.fetchBitmapFromAssets(
                        mLottieView,
                        lottieImageAsset.getFileName(),
                        lottieImageAsset.getId(),
                        lottieImageAsset.getWidth(),
                        lottieImageAsset.getHeight(),
                        mContext);
            }
        };
        mLottieView.setAnimationFromJson(bubbleEffectInfo.getJsonStrFromAssets(mContext), "setting_english_backgroud");
        mLottieView.setImageAssetDelegate(imageAssetDelegate);
        mLottieView.useHardwareAcceleration(true);
        mLottieView.loop(true);
        mLottieView.playAnimation();
        lottieViewSex.useHardwareAcceleration(true);
        lottieViewSex.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                lottieViewSex.playAnimation();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    lottieViewSex.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    lottieViewSex.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

    }

    /**
     * 数据填充
     */
    private void fillData() {
        if (contentAdapter == null) {
            contentAdapter = new RCommonAdapter(mContext, listName);
            contentAdapter.addItemViewDelegate(1, new SettingEnglishNameIndexItem(mContext,englishNameListener));
            contentAdapter.addItemViewDelegate(4, new SettingEnglishNameItem(mContext,englishNameListener));
            recyclerView.setAdapter(contentAdapter);
        } else {
            contentAdapter.updateData(listName);
        }

        if (contentAdapterIndex == null) {
            contentAdapterIndex = new RCommonAdapter(mContext, listIndex);
            contentAdapterIndex.addItemViewDelegate(1, new SettingEnglishNameBarItem(mContext,englishNameListener));
            recyclerViewIndex.setAdapter(contentAdapterIndex);
        } else {
            contentAdapterIndex.updateData(listIndex);
        }

        if (contentAdapterRecommend == null) {
            contentAdapterRecommend = new RCommonAdapter(mContext, listRecommendName);
            contentAdapterRecommend.addItemViewDelegate(1, new SettingEnglishRemmondItem(mContext,englishNameListener));
            rvRecommend.setAdapter(contentAdapterRecommend);
        } else {
            contentAdapterRecommend.updateData(listRecommendName);
        }
        //   areaIndexBarView.setData(listWord);
    }

    /**
     * 设置距离
     */
    public void setRecyclerViewDecoration() {
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION,  SizeUtils.Dp2Px(mContext, 5));//top间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 0);//底部间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 0);//左间距
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, SizeUtils.Dp2Px(mContext, 3));//右间距
        rvRecommend.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        recyclerView.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        recyclerSearch.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
    }

    public class GridSpanSearchSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            if(listSearchName.size()==0){
                return 0;
            }
            return listSearchName.get(position).getSpanNum();
        }
    }

    public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            if(listName.size()==0){
                return 0;
            }
            return listName.get(position).getSpanNum();
        }
    }
    private void selectSerch(int positon,String text) {
        for(int i=0;i<listSearchName.size();i++) {
            if(i==positon){
                listSearchName.get(i).setSelect(true);
            } else {
                listSearchName.get(i).setSelect(false);
            }
        }
        for(int i=0;i<listName.size();i++) {
            listName.get(i).setSelect(false);
        }
        for(int i=0;i<listRecommendName.size();i++) {
            listRecommendName.get(i).setSelect(false);
        }
        adapterSearch.updateData(listSearchName);
        contentAdapterRecommend.updateData(listRecommendName);
        contentAdapter.updateData(listName);

    }

    private void selectRecomand(int positon,String text) {
        for(int i=0;i<listRecommendName.size();i++) {
            if(i==positon){
                listRecommendName.get(i).setSelect(true);
            } else {
                listRecommendName.get(i).setSelect(false);
            }
        }
        for(int i=0;i<listName.size();i++) {
            listName.get(i).setSelect(false);
        }
        contentAdapterRecommend.updateData(listRecommendName);
        contentAdapter.updateData(listName);

    }
    private void selectIndex(int positon,String text) {
        for(int i=0;i<listIndex.size();i++) {
            if(i==positon){
                listIndex.get(i).setSelect(true);
            } else {
                listIndex.get(i).setSelect(false);
            }
        }
        contentAdapterIndex.updateData(listIndex);

        int areaListIndex = listIndex.get(positon).getIndexPostion();
        manager.scrollToPositionWithOffset(areaListIndex, areaListIndex);


    }
    private void selectName(int positon,String text) {
        for(int i=0;i<listName.size();i++) {
            if(i==positon){
                listName.get(i).setSelect(true);
            } else {
                listName.get(i).setSelect(false);
            }
        }
        for(int i=0;i<listRecommendName.size();i++) {
            listRecommendName.get(i).setSelect(false);
        }
        contentAdapterRecommend.updateData(listRecommendName);
        contentAdapter.updateData(listName);
    }
    AbstractBusinessDataCallBack businessDataCallBack = new AbstractBusinessDataCallBack() {
        @Override
        public void onDataSucess(Object... objData) {
            listName = (List<EngLishNameEntity>) objData[0];
            EngLishNameEntity recommendName = null;

            if(listName!=null && listName.size()>0) {
                for (int i = 0; i < listName.size(); i++) {
                    if(listRecommendName.size()==4) {
                        break;
                    }
                    if(!TextUtils.isEmpty(listName.get(i).getName())) {
                        recommendName = new EngLishNameEntity();
                        recommendName.setName(listName.get(i).getName());
                        listRecommendName.add(recommendName);
                    }
                }
            }
            fillData();
        }
    };
    private void testData() {

        // lstIndexEntity = new ArrayList<>();
        //   AreaIndexBarView.IndexEntity indexEntity = null;
        listName = new ArrayList<>();
        listIndex = new ArrayList<>();
        EngLishNameEntity entity = null;
        String[] word = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        listWord = new ArrayList<>();
        listRecommendName = new ArrayList<>();
        EngLishNameEntity index = null;

        int indexPosition = 0;
        for (int j = 0; j < word.length; j++) {
            listWord.add(word[j]);
            index = new EngLishNameEntity();
            index.setWordIndex(word[j]);

            listIndex.add(index);

            if(true) {
                continue;
            }
            //  indexEntity = new AreaIndexBarView.IndexEntity();
            //  indexEntity.setAreaBarIndex(j);
            for (int i = 0; i < 60; i++) {
                entity = new EngLishNameEntity();
                entity.setIndexPostion(j);
                if (i == 0) {
                    index.setIndexPostion(indexPosition);
                    //indexEntity.setAreaListIndex(j+listName.size());
                    entity.setIndex(true);
                    entity.setSpanNum(4);
                    entity.setWordIndex(word[j]);
                } else {
                    entity.setName("tom" + i);
                }
                listName.add(entity);
                indexPosition++;
            }
            // lstIndexEntity.add(indexEntity);
        }
        englishNameBll.getDefaultName(listIndex,sex,businessDataCallBack);
    }



    public static void startSettingEnglishName(Context context) {
        Intent intent = new Intent(context, SettingEnglishLandActivity.class);
        context.startActivity(intent);
    }
}
