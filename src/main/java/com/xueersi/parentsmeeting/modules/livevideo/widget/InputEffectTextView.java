package com.xueersi.parentsmeeting.modules.livevideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 打字效果  文本展示控件
 *   具备 居中显示功能
 * @author chenkun
 * @version 1.0, 2018/4/11 下午4:04
 */

public class InputEffectTextView extends View {

    private   int LINE_V_GAP = 20;// 行间距
    private   String  mTextColor = "#73510A";
    private   int textsize   = 12; // 字体大小
    private   int lineMaxCharacterNum = 13; //每行大字符数
    private static  final  long  SINGLE_CHARACTER_DURATION = 200;//打字效果，字符显示 时间间隔
    private Paint paint;
    private InputTask task;
    private float mWidth = 0;
    private float mHeight = 0;
    private String mContentText;   //

    public InputEffectTextView(Context context) {
        this(context, null);
    }

    public InputEffectTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputEffectTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(Color.parseColor(mTextColor));
        float scale=getContext().getResources().getDisplayMetrics().density;
        int textSize =  (int)(textsize*scale+0.5f);
        paint.setTextSize(textSize);
    }



    class Line {

        String       contentText;
        StringBuffer currentTextBuf;
        float        startX;
        float        startY;
        int          characterNum2Draw;

        Line(String text, float startX, float startY) {
            this.contentText = text;
            this.startX = startX;
            this.startY = startY;
            currentTextBuf = new StringBuffer();
        }

        public void drawText(Canvas canvas) {
            try {

            }catch (Exception e){
                e.printStackTrace();
            }
            String str2Draw = getDrawStr();
            if(!TextUtils.isEmpty(str2Draw)){
                canvas.drawText(getDrawStr(), startX, startY, paint);
            }
        }

        private  String getDrawStr(){

           if(characterNum2Draw == contentText.length()){
                return contentText;
            }
            currentTextBuf.setLength(0);
            if(characterNum2Draw > contentText.length()){
                characterNum2Draw = contentText.length();
            }
            currentTextBuf.append(contentText.substring(0,characterNum2Draw));

            return  currentTextBuf.toString();
        }


        public void setCharacterNum2Draw(int characterNum){
            characterNum2Draw = characterNum;
        }

    }


    List<Line> lines = new ArrayList<Line>();
    List<String> lineStrList;


    public void setLineText(List<String> list){
        this.lineStrList = list;
        spilt2Lines();
        startInputEffect();
        invalidate();
    }



    public interface InputEffectListener {
        void onFinish();
    }


    private InputEffectListener  mInputEffectListener;
    /**
     * 设置文本内容
     * @param text
     */
    public void setText(String text,InputEffectListener inputStateListener){

        if(TextUtils.isEmpty(text)){
            return;
        }
        this.mInputEffectListener = inputStateListener;
        mContentText = text;
        List<String> resultList = getStrList(text,lineMaxCharacterNum);
        setLineText(resultList);
    }



    private List<String> getStrList(String text, int length) {
        int size = text.length() / length;
        if (text.length() % length != 0) {
            size += 1;
        }
        return getStrList(text, length, size);
    }

    private List<String> getStrList(String text, int length, int size) {
        List<String> list = new ArrayList<String>();
        for (int index = 0; index < size; index++) {
            String childStr = substring(text, index * length,
                    (index + 1) * length);
            list.add(childStr);
        }
        return list;
    }

    private String substring(String text, int f, int t) {
        if (f > text.length())
            return null;
        if (t > text.length()) {
            return text.substring(f, text.length());
        } else {
            return text.substring(f, t);
        }
    }


    private  int characterIndex = 1;

    class InputTask implements Runnable{

        private boolean canceled = false;

        @Override
        public void run() {
            if(characterIndex <= mContentText.length() && !canceled){
                int lineIndex = characterIndex / lineMaxCharacterNum;
              /*  Log.e("InputEffectTextView","========>lineIndex:"+lineIndex+":"
                        +lines.size()+":"+characterIndex+":"+mContentText.length());*/
                // 绘制已展示完的行
                if(lineIndex > 0){
                    for (int i = 0; i < lineIndex; i++) {
                        lines.get(i).setCharacterNum2Draw(lineMaxCharacterNum);
                    }
                }
                if(characterIndex > (lineIndex * lineMaxCharacterNum) && (characterIndex % lineMaxCharacterNum == 0)){
                    if(lineIndex < lines.size()){
                        lines.get(lineIndex).setCharacterNum2Draw(lineMaxCharacterNum);
                    }
                }else{
                    if(lineIndex < lines.size()){
                        lines.get(lineIndex).setCharacterNum2Draw(characterIndex % lineMaxCharacterNum);
                    }else{
                        lines.get(lines.size()-1).setCharacterNum2Draw(lineMaxCharacterNum);
                    }
                }
                invalidate();
                characterIndex++;
                postDelayed(this,SINGLE_CHARACTER_DURATION);
            }else {
                if(mInputEffectListener != null){
                    mInputEffectListener.onFinish();
                }
            }

        }

        public void cancel() {
            canceled = true;
        }
    }


    private void startInputEffect() {
        if(task == null){
            task = new InputTask();
        }
       this.post(task);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int realWitdhMeasureSpec;
        int realHeightMeasureSpec;

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        if(widthMode == MeasureSpec.EXACTLY){
            if(width < mWidth){
                realWitdhMeasureSpec = MeasureSpec.makeMeasureSpec((int) mWidth,MeasureSpec.EXACTLY);
            }else{
                realWitdhMeasureSpec = widthMeasureSpec;
            }
        }else{
            realWitdhMeasureSpec = MeasureSpec.makeMeasureSpec((int) mWidth,widthMode);
        }

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(heightMode == MeasureSpec.EXACTLY){

             if(height < mHeight){
                 realHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mHeight,MeasureSpec.EXACTLY);
             }else {
                 realHeightMeasureSpec = heightMeasureSpec;
             }

        }else{
            realHeightMeasureSpec = MeasureSpec.makeMeasureSpec((int) mHeight,heightMode);
        }

        super.onMeasure(realWitdhMeasureSpec, realHeightMeasureSpec);
    }



    public void spilt2Lines() {

        float maxLineWidth = 0f;
        float tempLineWidth;
        float contentHeight = 0f;
        //find  maxLineWidth
        for (int i = 0; i < lineStrList.size(); i++) {
            tempLineWidth = paint.measureText(lineStrList.get(i));
            if (tempLineWidth > maxLineWidth) {
                maxLineWidth = tempLineWidth;
            }
        }

        mWidth = maxLineWidth;

        Rect fontBound = new Rect();
        paint.getTextBounds(lineStrList.get(0), 0, lineStrList.get(0).length(), fontBound);
        Line line;
        int lineHeight = fontBound.bottom - fontBound.top;
        for (int i = 0; i < lineStrList.size(); i++) {
            float startX = (maxLineWidth - paint.measureText(lineStrList.get(i))) / 2;
            float startY = i * (lineHeight + LINE_V_GAP) + lineHeight;

            if(i == lineStrList.size() -1){
                contentHeight = startY + lineHeight - LINE_V_GAP;
            }
            line = new Line(lineStrList.get(i), startX, startY);
            lines.add(line);
        }
        mHeight = contentHeight;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(lines != null && lines.size() > 0){
            for (Line line : lines) {
                line.drawText(canvas);
            }
        }
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseRes();

    }

    private void releaseRes() {
        if(task != null){
            task.cancel();
            removeCallbacks(task);
        }
    }
}
