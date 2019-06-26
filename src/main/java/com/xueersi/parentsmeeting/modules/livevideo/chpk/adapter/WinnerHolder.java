package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

/**
 * Created by lenovo on 2019/1/26.
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.common.base.BaseApplication;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.ClassChestEntity;

public class WinnerHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    ImageView ivLuckyStar;
    TextView tvName;
    TextView tvCoin;
    TextView tvPatch;
    ImageView ivChip;

    public WinnerHolder(View itemView) {
        super(itemView);
        ivHead = itemView.findViewById(R.id.iv_teampk_open_box_winner_head);
        ivLuckyStar = itemView.findViewById(R.id.iv_teampk_open_box_lucky_guy);
        tvName = itemView.findViewById(R.id.tv_teampk_open_box_winner_name);
        tvCoin = itemView.findViewById(R.id.tv_teampk_open_box_winner_coin);
        tvPatch = itemView.findViewById(R.id.tv_teampk_lucky_start_patch);
        ivChip = itemView.findViewById(R.id.iv_teampk_aipatner_chip);
    }

    public void bindData(ClassChestEntity.SubChestEntity data, int postion) {
        ImageLoader.with(BaseApplication.getContext()).load(data.getAvatarPath())
                .placeHolder(R.drawable.livevideo_list_headportrait_ic_disable)
                .asBitmap(new SingleConfig.BitmapListener() {
                    @Override
                    public void onSuccess(Drawable drawable) {
                        Bitmap resultBitmap = null;
                        if (drawable instanceof BitmapDrawable) {
                            resultBitmap = ((BitmapDrawable) drawable).getBitmap();
                        } else if (drawable instanceof GifDrawable) {
                            resultBitmap = ((GifDrawable) drawable).getFirstFrame();
                        }
                        if (resultBitmap != null) {
                            Bitmap circleBitmap = scaleBitmap(resultBitmap, Math.min(resultBitmap.getWidth(),
                                    resultBitmap.getHeight()) / 2);
                            ivHead.setImageBitmap(circleBitmap);
                        }
                    }

                    @Override
                    public void onFail() {
                    }
                });
        ivLuckyStar.setVisibility(postion <= 4 ? View.VISIBLE : View.GONE);
        tvName.setText(data.getStuName());
        tvCoin.setText("+" + data.getGold());
        if (tvPatch != null) {
            tvPatch.setText("+" + data.getChipNum());
        }
        if (ivChip != null) {
            ImageLoader.with(BaseApplication.getContext()).load(data.getChipUrl()).into(ivChip);
        }
    }

    public static Bitmap scaleBitmap(Bitmap input, int radius) {
        Bitmap result = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Rect src = new Rect(0, 0, input.getWidth(), input.getHeight());
        Rect dst = new Rect(0, 0, radius * 2, radius * 2);
        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CCW);
        canvas.clipPath(path);
        Paint paint = new Paint();
        paint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(input, src, dst, paint);
        return result;
    }

}
