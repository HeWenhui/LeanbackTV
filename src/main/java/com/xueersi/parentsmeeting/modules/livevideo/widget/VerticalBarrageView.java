
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
import com.xueersi.lib.log.LoggerFactory;
import com.xueersi.lib.log.logger.Logger;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.PraiseMessageEntity;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * 垂直滚动弹幕
 */
public class VerticalBarrageView extends LinearLayout implements Handler.Callback {
    Logger logger = LoggerFactory.getLogger("VerticalBarrageView");

    private final int barrageCount = 4;

    private TreeSet<PraiseMessageEntity> barrageQueue = new TreeSet<>();


    private final LayoutInflater inflater;

    private Handler handler = new Handler(Looper.getMainLooper(), this);

    private int currentBarrage = 0;

    private Pools.SimplePool<View> itemViewPool = new Pools.SimplePool<>(4);

    public interface OnBarrageScrollListener {
        void onBarrageScrollItem(PraiseMessageEntity praiseMessageEntity);
    }

    private OnBarrageScrollListener listener;

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

    public void setListener(OnBarrageScrollListener listener) {
        this.listener = listener;
    }


    public void appendBarrages(PraiseMessageEntity praiseMessageEntity) {
        barrageQueue.add(praiseMessageEntity);
    }


    public void addBarrages(List<PraiseMessageEntity> praiseMessageEntities) {
        barrageQueue.addAll(praiseMessageEntities);
    }

    public void start() {
        handler.sendEmptyMessage(0);
    }

    public void stop() {
        handler.removeMessages(0);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (!barrageQueue.isEmpty()) {
            if (this.getChildCount() >= barrageCount) {
                this.removeViewAt(0);
            }
            PraiseMessageEntity messageEntity = barrageQueue.pollFirst();
            logger.d("message scroll=" + messageEntity.getMessageContent());
            View view = obtainTextView(messageEntity);
            this.addView(view);

            int childCount = this.getChildCount();
            if (childCount >= 4) {
                View childAt = getChildAt(0);
                childAt.setAlpha(0.3f);

                childAt = getChildAt(1);
                childAt.setAlpha(0.5f);
            }
            if (listener != null) {
                listener.onBarrageScrollItem(messageEntity);
            }
        }
        handler.sendEmptyMessageDelayed(0, 1000);
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
            holder.typeIcon.setVisibility(View.VISIBLE);
            holder.typeIcon.setImageResource(R.drawable.ic_livevideo_praise_intera_gift);
            int giftType = messageEntity.getGiftType();
            holder.giftType.setVisibility(View.VISIBLE);
            if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_PHYSICAL) {
                holder.giftType.setImageResource(R.drawable.livevideo_bubble_small_physics_icon_normal);
            } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_CHEMISTRY) {
                holder.giftType.setImageResource(R.drawable.livevideo_bubble_small_chemistry_icon_normal);
            } else if (giftType == PraiseMessageEntity.SPECIAL_GIFT_TYPE_MATH) {
                holder.giftType.setImageResource(R.drawable.livevideo_bubble_small_math_icon_normal);
            }

        } else if (messageType == PraiseMessageEntity.TYPE_PRAISE) {
            holder.typeIcon.setVisibility(View.GONE);
            holder.giftType.setVisibility(View.GONE);
        } else if (messageType == PraiseMessageEntity.TYPE_CLASS) {
            holder.typeIcon.setVisibility(View.VISIBLE);
            holder.giftType.setVisibility(View.GONE);
            holder.typeIcon.setImageResource(R.drawable.ic_livevideo_praise_intera_class);
        } else {
            holder.typeIcon.setVisibility(View.GONE);
            holder.giftType.setVisibility(View.GONE);
        }
        holder.messageContentView.setText(messageEntity.getMessageContent());

        return view;
    }

    static class ViewHolder {
        ImageView typeIcon;
        TextView messageContentView;
        ImageView giftType;
    }

}
