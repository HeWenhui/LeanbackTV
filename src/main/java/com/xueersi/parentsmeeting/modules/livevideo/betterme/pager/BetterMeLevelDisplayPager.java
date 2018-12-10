package com.xueersi.parentsmeeting.modules.livevideo.betterme.pager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.item.BetterMeLevelEntity;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.BetterMePager;
import com.xueersi.parentsmeeting.modules.livevideo.betterme.view.OnPagerClose;
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
    /**
     * 左箭头
     */
    private ImageView ivArrowLeft;
    /**
     * 右箭头
     */
    private ImageView ivArrowRight;
    /**
     * 关闭
     */
    private ImageView ivClose;
    private int currentPagerItem = 0;
    private ViewPager mViewPager;
    private LinearLayout llPagerIndicator;
    private BetterMeLevelDisplayPagerAdapter mPagerAdapter;
    private List<BetterMeLevelEntity> mLevelEntityList = new ArrayList<>();
    private OnPagerClose onPagerClose;
    /**
     * 段位名称
     */
    public static String[] LEVEL_NAMES = new String[]{
            "倔强青铜",
            "勤奋白银",
            "刻苦黄金",
            "恒心铂金",
            "笃学钻石",
            "最强学霸"
    };
    /**
     * 段位图片资源
     */
    public static int[] LEVEL_IMAGE_RES = new int[]{
            R.drawable.app_xiaomubiao_qingtong,
            R.drawable.app_xiaomubiao_baiyin,
            R.drawable.app_xiaomubiao_huangjin,
            R.drawable.app_xiaomubiao_bojin,
            R.drawable.app_xiaomubiao_zuanshi,
            R.drawable.app_xiaomubiao_wangzhe
    };
    /**
     * 如何升星
     */
    public static String[] LEVEL_UPSTAR_DESCRIPTIONS = new String[]{
            "每完成3次目标升1星",
            "每完成3次目标升1星",
            "每完成4次目标升1星",
            "每完成5次目标升1星",
            "每完成6次目标升1星",
            "每完成6次目标升1星"
    };
    /**
     * 如何升级
     */
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

    public BetterMeLevelDisplayPager(Context context, OnPagerClose onPagerClose) {
        super(context);
        this.onPagerClose = onPagerClose;
        initData();
        initListener();
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_betterme_level_display, null);
        mViewPager = view.findViewById(R.id.vp_livevideo_betterme_introduction);
        llPagerIndicator = view.findViewById(R.id.dot_horizontal);
        ivClose = view.findViewById(R.id.iv_livevideo_betterme_level_close);
        ivArrowLeft = view.findViewById(R.id.iv_livevideo_betterme_level_arrow_left);
        ivArrowRight = view.findViewById(R.id.iv_livevideo_betterme_level_arrow_right);
        return view;
    }

    @Override
    public void initData() {
        for (int i = 0; i < LEVEL_NUMBER; i++) {
            BetterMeLevelEntity levelEntity = new BetterMeLevelEntity();
            levelEntity.setLevelName(LEVEL_NAMES[i]);
            levelEntity.setLevelDrawableRes(LEVEL_IMAGE_RES[i]);
            levelEntity.setUpStardescription(LEVEL_UPSTAR_DESCRIPTIONS[i]);
            levelEntity.setUpLeveldescription(LEVEL_UPLEVEL_DESCRIPTIONS[i]);
            mLevelEntityList.add(levelEntity);
        }
        mPagerAdapter = new BetterMeLevelDisplayPagerAdapter(mLevelEntityList);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(new PageIndicator(mContext, llPagerIndicator, mLevelEntityList.size() / 2));
        ivArrowLeft.setEnabled(false);
    }

    @Override
    public void initListener() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPagerClose.onClose(BetterMeLevelDisplayPager.this);
                onPagerClose.onNext(BetterMePager.PAGER_INTRODUCTION);
            }
        });
        ivArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPagerItem > 0) {
                    mViewPager.setCurrentItem(--currentPagerItem);
                    ivArrowRight.setEnabled(true);
                }
            }
        });
        ivArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPagerItem < ((LEVEL_NUMBER - 1) / 2)) {
                    mViewPager.setCurrentItem(++currentPagerItem);
                    ivArrowLeft.setEnabled(true);
                }
            }
        });

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
            return (mLevelList.size() + 1) / 2;
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
            img_select = R.drawable.app_xiaomubiao_shellwindow_yema_img_yes;
            img_unSelect = R.drawable.app_xiaomubiao_shellwindow_yema_img_no;
            final int imgSize = SizeUtils.Dp2Px(mContext, 10);

            for (int i = 0; i < mPageCount; i++) {
                ImageView imageView = new ImageView(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                //为小圆点左右添加间距
                params.leftMargin = SizeUtils.Dp2Px(mContext, 3);
                ;
                params.rightMargin = SizeUtils.Dp2Px(mContext, 3);
                ;
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
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

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
            if (position == 0) {
                ivArrowLeft.setEnabled(false);
            } else {
                ivArrowLeft.setEnabled(true);
            }
            if (position == (LEVEL_NUMBER - 1) / 2) {
                ivArrowRight.setEnabled(false);
            } else {
                ivArrowRight.setEnabled(true);
            }
            currentPagerItem = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }
}
