package com.xueersi.parentsmeeting.modules.livevideo.activity.item;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * Created by linyuqiang on 2016/8/5.
 */
public class FlowerItem implements AdapterItemInterface<FlowerEntity> {
    protected ImageView ivMessageFlower;
    protected TextView tvMessageFlower;
    protected Context context;
    protected View root;

    public FlowerItem(Context context) {
        this.context = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_message_flower;
    }

    @Override
    public void initViews(View root) {
        this.root = root;
        ivMessageFlower = (ImageView) root.findViewById(R.id.iv_livevideo_message_flower);
        tvMessageFlower = (TextView) root.findViewById(R.id.tv_livevideo_message_flower);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(FlowerEntity entity, int position, Object objTag) {
        ivMessageFlower.setImageBitmap(DrawableHelper.bitmapFromResource(context.getResources(), entity.getId()));
        SpannableString spanttt = new SpannableString(entity.getTip());
        CharacterStyle characterStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.white));
        spanttt.setSpan(characterStyle, 0, entity.getTip().length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvMessageFlower.setText(spanttt);
        spanttt = new SpannableString(entity.getGold() + "");
        characterStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_F13232));
        spanttt.setSpan(characterStyle, 0, spanttt.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvMessageFlower.append(spanttt);
        spanttt = new SpannableString("金币");
        characterStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.white));
        spanttt.setSpan(characterStyle, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvMessageFlower.append(spanttt);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) root.findViewById(R.id.ck_livevideo_message_flower);
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
    }
}
