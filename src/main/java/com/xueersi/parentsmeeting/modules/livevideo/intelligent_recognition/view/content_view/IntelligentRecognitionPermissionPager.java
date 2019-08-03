package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.view.content_view;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.airbnb.lottie.AssertUtil;
import com.tal.speech.speechrecognizer.PhoneScore;
import com.xueersi.common.permission.XesPermission;
import com.xueersi.common.permission.config.PermissionConfig;
import com.xueersi.common.util.FontCache;
import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.util.LiveActivityPermissionCallback;

import java.util.List;

import static com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.utils.IntelligentConstants.INTELLIGENT_LOTTIE_PATH;

/**
 * 检测英语智能测评权限问题
 */
public class IntelligentRecognitionPermissionPager extends BaseIntelligentRecognitionPager {

    public IntelligentRecognitionPermissionPager(FragmentActivity context) {
        super(context);
    }

    private boolean checkPermission() {
        PackageManager pkm = mContext.getPackageManager();
        boolean isDefault = (PackageManager.PERMISSION_GRANTED ==
                pkm.checkPermission("android.permission.MODIFY_AUDIO_SETTINGS", mContext.getPackageName())
                && PackageManager.PERMISSION_GRANTED ==
                pkm.checkPermission("android.permission.RECORD_AUDIO", mContext.getPackageName()));
        logger.i("isDefault " + isDefault);
        return isDefault;
    }

    @Override
    protected void performOpenViewStart() {
        if (checkPermission()) {
            super.performOpenViewStart();
        } else {
            if (settingViewGroup != null && settingViewGroup.getVisibility() != View.VISIBLE) {
                settingViewGroup.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void performStartWaveLottie() {
        if (checkPermission()) {
            super.performStartWaveLottie();
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        settingViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean have = XesPermission.checkPermission(mActivity, new LiveActivityPermissionCallback() {

                            @Override
                            public void onFinish() {

                            }

                            @Override
                            public void onDeny(String permission, int position) {

                            }

                            @Override
                            public void onGuarantee(String permission, int position) {
                                settingViewGroup.setVisibility(View.GONE);
                            }
                        },
                        PermissionConfig.PERMISSION_CODE_AUDIO);

            }
        });
    }

    /**
     * 更新火焰数量图片
     *
     * @param fireNum
     * @return
     */
    @Override
    protected Bitmap creatFireBitmap(String fireNum, String lottieId, int color) {
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(AssertUtil.open(INTELLIGENT_LOTTIE_PATH + "images/" + lottieId));
            Bitmap creatBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(creatBitmap);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setTextSize(bitmap.getHeight());
            paint.setColor(color);
            Typeface fontFace = FontCache.getTypeface(mContext, "fangzhengcuyuan.ttf");
            paint.setTypeface(fontFace);
            canvas.drawText(fireNum, 0, bitmap.getHeight(), paint);
            bitmap.recycle();
            bitmap = creatBitmap;
            return bitmap;
        } catch (Exception e) {
            logger.e("creatFireBitmap", e);
        }
        return null;
    }

    @Override
    protected SpannableString getSpannableString(final List<PhoneScore> list, String showContent) {
        SpannableString spannableString = new SpannableString(showContent);
        for (int i = 0; i < list.size(); i++) {
            PhoneScore phoneScore = list.get(i);
            if (phoneScore == null) {
                logger.i("phoneScore null");
                continue;
            }
            String _word = phoneScore.getWord();
            for (WordInfo _wordInfo : wordList) {
//                WordInfo _wordInfo = wordList.get(i);
                if (_wordInfo == null) {
                    logger.i("wordInfo null");
                    continue;
                }
                String contentWord = _wordInfo.getWord();
                if (isNotNullEquals(_word, contentWord)) {
                    int score = phoneScore.getScore();
                    int contentPos = _wordInfo.getPos();
                    int color;// = mActivity.getResources().getColor(R.color.COLOR_303134);
                    color = Color.parseColor("#303134");
                    if (score >= 80) {//
                        color = mActivity.getResources().getColor(R.color.COLOR_56D80A);
                        color = Color.parseColor("#56D80A");
                    } else if (score < 60) {
                        color = mActivity.getResources().getColor(R.color.COLOR_FF5C37);
                        color = Color.parseColor("#FF5C37");
//                            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.alpha(color));
                    }
//            logger.i("color :" + color + " word:" + contentPos + " length:" + word.length());
//            Color.alpha(1l);
                    ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
                    logger.i("contentPos:" + contentPos + " contentWord:" + contentWord + " contentWord.length:"
                            + contentWord.length() + " phoneScore:_word:" + _word + " word.length():" + _word.length());
                    int contentEndPos = contentPos + contentWord.length();
                    contentEndPos = contentEndPos > showContent.length() ? showContent.length() : contentEndPos;
                    spannableString.setSpan(foregroundColorSpan,
                            contentPos, contentEndPos, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return spannableString;
    }

    /**
     * 得到测评语句的每个单词和单词所在起始位置
     *
     * @param content
     */
    protected void handleContentWordList(String content) {
        wordList.clear();
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (int i = 0; i < content.length(); i++) {
            char ch = content.charAt(i);
            if (isChar(ch)) {
                sb.append(ch);
            } else {
                WordInfo wordInfo = new WordInfo();
                wordInfo.setPos(index);
                wordInfo.setWord(sb.toString());
                wordList.add(wordInfo);
                sb = new StringBuilder();
                index = i + 1;
            }

        }
        if (isChar(content.charAt(content.length() - 1))) {
            WordInfo wordInfo = new WordInfo();
            wordInfo.setPos(index);
            wordInfo.setWord(sb.toString());
            wordList.add(wordInfo);
        }
        for (int i = 0; i < wordList.size(); i++) {
            logger.i("i= " + i + " word:" + wordList.get(i).getWord() + " pos:" + wordList.get(i).getPos());
        }
    }

    boolean isChar(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }
}
