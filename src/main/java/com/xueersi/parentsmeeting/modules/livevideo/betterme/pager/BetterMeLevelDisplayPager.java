package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.item.BetterMeLevelEntity;
import com.xueersi.parentsmeeting.modules.livevideo.page.LiveBasePager;

import java.util.ArrayList;
import java.util.List;

/**
 * 英语小目标 段位展示
 *
 * @author zhangyuansun
 * created  at 2018/11/26
 */
public class BetterMeLevelDisplayPager extends LiveBasePager {
    private ViewPager mViewPager;
    private LinearLayout llPagerIndicator;
    private BetterMeLevelDisplayPagerAdapter mPagerAdapter;
    private List<BetterMeLevelEntity> mLevelEntityList = new ArrayList<>();
    public static String[] LEVEL_NAMES = new String[]{
            "倔强青铜",
            "勤奋白银",
            "刻苦黄金",
            "恒心铂金",
            "笃学钻石",
            "最强学霸"
    };
    public static int[] LEVEL_IMAGE_RESS = new int[]{
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor,
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor,
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor,
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor,
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor,
            R.drawable.app_livevideo_enteampk_boy_bg_img_nor
    };
    public static String[] LEVEL_UPSTAR_DESCRIPTIONS = new String[]{
            "每完成3次目标升1星",
            "每完成3次目标升1星",
            "每完成4次目标升1星",
            "每完成5次目标升1星",
            "每完成6次目标升1星",
            "每完成6次目标升1星"
    };
    public static String[] LEVEL_UPLEVEL_DESCRIPTIONS = new String[]{
            "满3星升级",
            "满4星升级",
            "满4星升级",
            "满5星升级",
            "满6星升级",
            "满6星升级"
    };
    public static int LEVEL_NUMBER = 6;

    public BetterMeLevelDisplayPager(Context context) {
        super(context);
    }

    public BetterMeLevelDisplayPager(Context context, boolean isNewView) {
        super(context, isNewView);
        initData();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_level_display, null);
        mViewPager = view.findViewById(R.id.vp_livevideo_betterme_introduction);
        llPagerIndicator = view.findViewById(R.id.dot_horizontal);
        return view;
    }

    @Override
    public void initData() {
        for (int i = 0; i < LEVEL_NUMBER; i++) {
            BetterMeLevelEntity levelEntity = new BetterMeLevelEntity();
            levelEntity.setLevelName(LEVEL_NAMES[i]);
            levelEntity.setLevelDrawableRes(LEVEL_IMAGE_RESS[i]);
            levelEntity.setUpStardescription(LEVEL_UPSTAR_DESCRIPTIONS[i]);
            levelEntity.setUpLeveldescription(LEVEL_UPLEVEL_DESCRIPTIONS[i]);
            mLevelEntityList.add(levelEntity);
        }
        mPagerAdapter = new BetterMeLevelDisplayPagerAdapter(mLevelEntityList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new PageIndicator(mContext, llPagerIndicator, mLevelEntityList.size() / 2));
    }

    class BetterMeLevelDisplayPagerAdapter extends PagerAdapter {
        List<BetterMeLevelEntity> mLevelList;
        List<View> mViewList;

        public BetterMeLevelDisplayPagerAdapter(List<BetterMeLevelEntity> mLevelList) {
            this.mLevelList = mLevelList;
            mViewList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return mLevelList.size() / 2;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = View.inflate(mContext, R.layout.item_livevideo_betterme_level_display, null);
            TextView tvLevelNameLeft = view.findViewById(R.id.tv_livevideo_betterme_level_name_left);
            ImageView ivLevelImgLeft = view.findViewById(R.id.iv_livevideo_betterme_level_img_left);
            TextView tvUpStarDecribtionLeft = view.findViewById(R.id.tv_livevideo_betterme_upstar_describtion_left);
            TextView tvUpLevelDecribtionLeft = view.findViewById(R.id.tv_livevideo_betterme_uplevel_describtion_left);

            TextView tvLevelNameRight = view.findViewById(R.id.tv_livevideo_betterme_level_name_right);
            ImageView ivLevelImgRight = view.findViewById(R.id.iv_livevideo_betterme_level_img_right);
            TextView tvUpStarDecribtionRight = view.findViewById(R.id.tv_livevideo_betterme_upstar_describtion_right);
            TextView tvUpLevelDecribtionRight = view.findViewById(R.id.tv_livevideo_betterme_uplevel_describtion_right);

            BetterMeLevelEntity levelEntityLeft = mLevelList.get(position * 2);
            tvLevelNameLeft.setText(levelEntityLeft.getLevelName());
            ivLevelImgLeft.setImageResource(levelEntityLeft.getLevelDrawableRes());
            tvUpStarDecribtionLeft.setText(levelEntityLeft.getUpStardescription());
            tvUpLevelDecribtionLeft.setText(levelEntityLeft.getUpLeveldescription());

            if ((position * 2 + 1) < mLevelList.size()) {
                BetterMeLevelEntity levelEntityRight = mLevelList.get(position * 2 + 1);
                tvLevelNameRight.setText(levelEntityRight.getLevelName());
                ivLevelImgRight.setImageResource(levelEntityRight.getLevelDrawableRes());
                tvUpStarDecribtionRight.setText(levelEntityRight.getUpStardescription());
                tvUpLevelDecribtionRight.setText(levelEntityRight.getUpLeveldescription());
            }
            mViewList.add(view);
            container.addView(mViewList.get(position));
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViewList.get(position));
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    /**
     * ViewPager圆点指示器
     */
    class PageIndicator implements ViewPager.OnPageChangeListener {
        private int mPageCount;//页数
        private List<ImageView> mImgList;//保存img总个数
        private int img_select;
        private int img_unSelect;

        public PageIndicator(Context context, LinearLayout linearLayout, int pageCount) {
            this.mPageCount = pageCount;

            mImgList = new ArrayList<>();
            img_select = R.drawable.ic_check_box_checked_red;
            img_unSelect = R.drawable.ic_check_box_unchecked;
            final int imgSize = 25;

            for (int i = 0; i < mPageCount; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //为小圆点左右添加间距
                params.leftMargin = 10;
                params.rightMargin = 10;
                //给小圆点一个默认大小
                params.height = imgSize;
                params.width = imgSize;
                if (i == 0) {
                    imageView.setBackgroundResource(img_select);
                } else {
                    imageView.setBackgroundResource(img_unSelect);
                }
                //为LinearLayout添加ImageView
                linearLayout.addView(imageView, params);
                mImgList.add(imageView);
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mPageCount; i++) {
                //选中的页面改变小圆点为选中状态，反之为未选中
                if ((position % mPageCount) == i) {
                    (mImgList.get(i)).setBackgroundResource(img_select);
                } else {
                    (mImgList.get(i)).setBackgroundResource(img_unSelect);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {}
    }
}
