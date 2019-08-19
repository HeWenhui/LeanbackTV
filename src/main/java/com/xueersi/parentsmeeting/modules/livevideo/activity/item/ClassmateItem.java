package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassmateEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * 免费课列表ITEM
 * Created by ZouHao on 2016/3/30.
 */
public class ClassmateItem implements AdapterItemInterface<ClassmateEntity> {
    private Context mContext;
    TextView tvClassmateName;
    ImageView ivClassmateHead;

    public ClassmateItem(Context context) {
        this.mContext = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_classmate_sign;
    }

    @Override
    public void initViews(View root) {
        tvClassmateName = (TextView) root.findViewById(R.id.tv_livevideo_classmate_name);
        ivClassmateHead = (ImageView) root.findViewById(R.id.iv_livevideo_classmate_head);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(ClassmateEntity entity, int position, Object objTag) {
        tvClassmateName.setText(entity.getName());

        RequestOptions options = new RequestOptions();
        options.error(R.drawable.ic_default_head_square)
                .placeholder(R.drawable
                        .ic_default_head_square)
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        ImageLoader.with(ContextManager.getContext()).load(entity.getImg())
                .placeHolder(R.drawable.ic_default_head_square)
                .error(R.drawable.ic_default_head_square).into(ivClassmateHead);
    }
}
