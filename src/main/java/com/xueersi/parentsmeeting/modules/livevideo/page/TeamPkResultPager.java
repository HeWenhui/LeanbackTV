package com.xueersi.parentsmeeting.modules.livevideo.page;

import android.content.Context;
import android.view.View;

import com.xueersi.parentsmeeting.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.CoinAwardDisplayer;
import com.xueersi.xesalib.utils.log.Loger;

/**
 * Created by chenkun on 2018/4/12
 * 战队 pk 结果页
 */
public class TeamPkResultPager extends BasePager{
    private static final String TAG = "TeamPkResultPager";

    public TeamPkResultPager(Context context){
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(mContext, R.layout.page_livevideo_teampk_pkresult, null);
        return view;
    }

    @Override
    public void initData() {
        Loger.e(TAG,"======> initData called");
    }
}
