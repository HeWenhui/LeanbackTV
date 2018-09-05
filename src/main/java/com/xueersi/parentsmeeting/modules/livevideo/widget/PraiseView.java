package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;

import com.xueersi.parentsmeeting.modules.livevideo.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
*点赞飘屏动画
*@author chekun
*created  at 2018/7/20 10:22
*/
public class PraiseView extends View
{

    private Drawable[] randomImgs;

    private List<PraiseMovie> praiselist;

    public PraiseView(Context context)
    {
        this(context, null);
    }

    public PraiseView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        randomImgs = new Drawable[5];
        randomImgs[0] = ResourcesCompat.getDrawable(getResources(),R.drawable.livevideo_arts_praise_1,null);
        randomImgs[1] = ResourcesCompat.getDrawable(getResources(),R.drawable.livevideo_arts_praise_2,null);
        randomImgs[2] = ResourcesCompat.getDrawable(getResources(),R.drawable.livevideo_arts_praise_3,null);
        randomImgs[3] = ResourcesCompat.getDrawable(getResources(),R.drawable.livevideo_arts_praise_4,null);
        randomImgs[4] = ResourcesCompat.getDrawable(getResources(),R.drawable.livevideo_arts_praise_5,null);
        praiselist = new ArrayList<PraiseMovie>();
    }

    public void addHeart()
    {
        addHearts(1);
    }

    public void addHearts(int number)
    {
        for (int i = 0; i < number; i++)
        {
            int index = getRandomIndex(0, 5);
            Drawable drawable = randomImgs[index];
            PraiseMovie movie = new PraiseMovie(drawable, 1600);
            praiselist.add(movie);
        }
        invalidate();
    }

    Random random = new Random();

    public int getRandomIndex(int start, int end)
    {
        int value = random.nextInt(end - start);
        return value + start;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int width = getMeasuredWidth();

        int height = getMeasuredHeight();

        for (PraiseMovie movie : praiselist)
        {
            movie.draw(canvas, width, height);
        }
    }

    public void start()
    {

    }

    @Override
    public void computeScroll()
    {
        int datasize = praiselist.size();

        if (datasize > 0)
        {
            for (int i = datasize - 1; i >= 0; i--)
            {
                PraiseMovie movie = praiselist.get(i);

                if (movie.isFinish())
                {
                    praiselist.remove(i);
                }
                else
                {
                    movie.move();
                }
            }

            postInvalidate();
        }

    }


}
