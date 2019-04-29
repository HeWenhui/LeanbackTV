package com.xueersi.parentsmeeting.widget.praise;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xueersi.common.base.BasePager;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideoOldIJK.page.LiveBasePager;
import com.xueersi.parentsmeeting.widget.praise.config.PraiseConfig;
import com.xueersi.parentsmeeting.widget.praise.entity.PraiseContentEntity;
import com.xueersi.parentsmeeting.widget.praise.item.LivePraiseItem;
import com.xueersi.ui.adapter.RCommonAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 表扬榜1
 *
 */
public class DarkPraisePager extends LiveBasePager {
    RecyclerView recyclerView;
    private RCommonAdapter contentAdapter;

    List<PraiseContentEntity> listContent;

    public DarkPraisePager(Context context) {
        super(context);
        listContent = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            PraiseContentEntity  entity = new PraiseContentEntity();
            if (i==0) {
                entity.setViewType(PraiseConfig.VIEW_TYPE_TITLE);
            } else {
                entity.setViewType(1);

            }
            entity.setName("i"+i);
            listContent.add(entity);
        }
        setContentData();

    }

    public DarkPraisePager(Context context, boolean isNewView) {
        super(context, isNewView);
    }

    public DarkPraisePager(Context context, Object obj, boolean isNewView) {
        super(context, obj, isNewView);
    }
    public class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {

            //return gridManager.getSpanCount();
            return listContent.get(position).getViewType();
        }
    }

    @Override
    public View initView() {
        mView = View.inflate(mContext, R.layout.page_livevideo_praise_list_dark, null);
        recyclerView = mView.findViewById(R.id.rv_livevideo_praise_list_dark_content);
        GridLayoutManager manager = new GridLayoutManager(mContext, 4);
        recyclerView.setLayoutManager(manager);
        return mView;
    }

    private void setContentData(){
        contentAdapter = new RCommonAdapter(mContext,listContent);
        contentAdapter.addItemViewDelegate(1,new LivePraiseItem());
        recyclerView.setAdapter(contentAdapter);
    }


}
