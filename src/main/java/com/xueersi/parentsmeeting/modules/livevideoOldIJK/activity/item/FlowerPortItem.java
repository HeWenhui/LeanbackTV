package com.xueersi.parentsmeeting.modules.livevideoOldIJK.activity.item;

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

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.FlowerEntity;
import com.xueersi.ui.adapter.AdapterItemInterface;

/**
 * Created by linyuqiang on 2016/8/5.
 */
public class FlowerPortItem implements AdapterItemInterface<FlowerEntity> {
    protected ImageView ivMessageFlower;
    protected TextView tvMessageFlower, tvMessageFlower2;
    protected Context context;
    public View root;
    public CheckBox checkBox;

    public FlowerPortItem(Context context) {
        this.context = context;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_livevideo_message_flower_port;
    }

    @Override
    public void initViews(View root) {
        this.root = root;
        ivMessageFlower = (ImageView) root.findViewById(R.id.iv_livevideo_message_flower);
        tvMessageFlower = (TextView) root.findViewById(R.id.tv_livevideo_message_flower);
        tvMessageFlower2 = (TextView) root.findViewById(R.id.tv_livevideo_message_flower2);
    }

    @Override
    public void bindListener() {

    }

    @Override
    public void updateViews(FlowerEntity entity, int position, Object objTag) {
        ivMessageFlower.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), entity.getId()));
        tvMessageFlower.setText(entity.getTip());
        SpannableString spanttt = new SpannableString(entity.getGold() + "");
        CharacterStyle characterStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_F13232));
        spanttt.setSpan(characterStyle, 0, spanttt.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvMessageFlower2.append(spanttt);
        spanttt = new SpannableString("金币");
        characterStyle = new ForegroundColorSpan(context.getResources().getColor(R.color.COLOR_333333));
        spanttt.setSpan(characterStyle, 0, 2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvMessageFlower2.append(spanttt);
        checkBox = (CheckBox) root.findViewById(R.id.ck_livevideo_message_flower);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
    }
}
