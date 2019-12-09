package com.xueersi.parentsmeeting.modules.livevideo.chpk.adapter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.xueersi.lib.framework.are.ContextManager;
import com.xueersi.lib.framework.drawable.DrawableHelper;
import com.xueersi.lib.imageloader.ImageLoader;
import com.xueersi.lib.imageloader.SingleConfig;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.entity.TeamEnergyAndContributionStarEntity;

/**
 * Created by lenovo on 2019/1/28.
 */

public    class TeamStarHolder extends RecyclerView.ViewHolder {
    ImageView ivHead;
    TextView tvName;
    TextView tvEnergy;

    public TeamStarHolder(View itemView) {
        super(itemView);
        ivHead = itemView.findViewById(R.id.iv_teampk_pkresult_student_head);
        tvName = itemView.findViewById(R.id.tv_teampk_pkresult_contribution_name);
        tvEnergy = itemView.findViewById(R.id.tv_teampk_student_add_energy);
    }

    public void bindData(TeamEnergyAndContributionStarEntity.ContributionStar data) {
        ImageLoader.with(ContextManager.getContext()).load(data.getAvaterPath()).asBitmap(new SingleConfig.BitmapListener
                () {
            @Override
            public void onSuccess(Drawable drawable) {
                Bitmap headBitmap = null;
                if (drawable instanceof GifDrawable) {
                    headBitmap = ((GifDrawable) drawable).getFirstFrame();
                }
                else {
                    headBitmap = DrawableHelper.drawable2bitmap(drawable);
                }
                if (headBitmap != null) {
                    Bitmap resultBitmap = scaleBitmap(headBitmap, Math.min(headBitmap.getWidth(), headBitmap
                            .getHeight()) / 2);
                    ivHead.setImageBitmap(resultBitmap);
                }
            }

            @Override
            public void onFail() {
            }
        });

        if (TextUtils.isEmpty(data.getRealname())){
            tvName.setText(data.getNickname());
        }
        else {
            tvName.setText(data.getRealname());
        }
        tvEnergy.setText("+" + data.getEnergy());
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