
package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.Pools;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xueersi.lib.framework.utils.SizeUtils;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PraiseMessageEntity;

import java.util.LinkedList;
import java.util.List;

/**
 * 垂直滚动弹幕
 */
public class VerticalBarrageView extends LinearLayout implements Handler.Callback {

    private final int barrageCount = 4;

    private LinkedList<PraiseMessageEntity> barrageQueue = new LinkedList<>();


    private final LayoutInflater inflater;

    private Handler handler = new Handler(Looper.getMainLooper(), this);

    private int currentBarrage = 0;

    private Pools.SimplePool<View> itemViewPool = new Pools.SimplePool<>(4);

    public VerticalBarrageView(Context context) {
        this(context, null);
    }

    public VerticalBarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_livevideo_praise_intera_barrage, this);

        setOrientation(LinearLayout.VERTICAL);

        LayoutTransition transition = new LayoutTransition();
        ObjectAnimator appearAnimator = ObjectAnimator.ofFloat(null, "alpha", 0f, 1f);
        appearAnimator.setDuration(transition.getDuration(LayoutTransition.APPEARING));
        transition.setAnimator(LayoutTransition.APPEARING, appearAnimator);


        ObjectAnimator disappearAnimator = ObjectAnimator.ofFloat(null, "alpha", 1f, 0f);
        disappearAnimator.setDuration(LayoutTransition
                .DISAPPEARING);
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappearAnimator);

        this.setLayoutTransition(transition);
    }

    public void addBarrages(List<PraiseMessageEntity> barrages) {
        barrageQueue.addAll(barrages);
    }

    public void start() {
        handler.sendEmptyMessage(0);


    }

    public void stop() {
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (!barrageQueue.isEmpty()) {
            if (this.getChildCount() >= barrageCount) {
                this.removeViewAt(0);
            }
            PraiseMessageEntity messageEntity = barrageQueue.poll();
            View view = obtainTextView(messageEntity);
            this.addView(view);
            handler.sendEmptyMessageDelayed(0, 1000);
        } else {
            handler.removeMessages(0);
        }
        return true;
    }

    private View obtainTextView(PraiseMessageEntity messageEntity) {
        View view = itemViewPool.acquire();
        ViewHolder holder = null;
        if (view == null) {
            view = inflater.inflate(R.layout.item_livevideo_praiselist_intera_barrage, null);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.topMargin = SizeUtils.Dp2Px(getContext(), 10);
            view.setLayoutParams(params);

            holder = new ViewHolder();
            holder.typeIcon = view.findViewById(R.id.iv_praise_intera_barrage_item_type);
            holder.messageContentView = view.findViewById(R.id.tv_praise_intera_barrage_item_content);
            holder.giftType = view.findViewById(R.id.iv_praise_intera_barrage_item_gift_type);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        int messageType = messageEntity.getMessageType();
        if (messageType == PraiseMessageEntity.TYPE_SPECIAL_GIFT) {
            holder.typeIcon.setImageResource(R.drawable.ic_livevideo_praise_intera_gift);
        } else if (messageType == PraiseMessageEntity.TYPE_CLASS) {
            holder.typeIcon.setImageResource(R.drawable.ic_livevideo_praise_intera_class);
        } else {
            holder.typeIcon.setVisibility(View.GONE);
        }
        holder.messageContentView.setText(messageEntity.getMessageContent());

        holder.giftType.setImageResource(R.drawable.livevideo_bubble_small_chemistry_icon_normal);

        return view;
    }

    static class ViewHolder {
        ImageView typeIcon;
        TextView messageContentView;
        ImageView giftType;
    }

}
