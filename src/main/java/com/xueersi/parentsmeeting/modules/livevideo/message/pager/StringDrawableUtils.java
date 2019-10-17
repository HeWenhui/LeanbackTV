package com.xueersi.parentsmeeting.modules.livevideo.message.pager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.widget.VerticalImageSpan;

import static com.xueersi.parentsmeeting.modules.livevideo.question.business.evendrive.BaseEvenDriveCommonPager.EVEN_DRIVE_ICON_SPAN;

public class StringDrawableUtils {

    public static SpannableString addEvenDriveMessageNum(Context mContext,
                                                         SpannableString spanttt,
                                                         String sEvenNum,
                                                         int type) {
        /** 中学连对激励对应的图片 */
        final int[] evenDriveNumDrawable = new int[]{
                0,
                0,
                R.drawable.bg_livevideo_even_drive_message_2,
                R.drawable.bg_livevideo_even_drive_message_3,
                R.drawable.bg_livevideo_even_drive_message_4,
                R.drawable.bg_livevideo_even_drive_message_5,
                R.drawable.bg_livevideo_even_drive_message_6,
                R.drawable.bg_livevideo_even_drive_message_7,
//            R.drawable.livevideo_evendrive_livemessage_octuple,
//            R.drawable.livevideo_evendrive_livemessage_nonuple,
                R.drawable.bg_livevideo_even_drive_message_king,
                R.drawable.bg_livevideo_even_drive_message_top};
        VerticalImageSpan verticalImageSpan = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int evenNum = 0;
            try {
                evenNum = Integer.valueOf(sEvenNum);
                if (evenNum >= 2) {
                    Drawable drawable;
                    if (evenNum >= 2 && evenNum < 8) {
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[evenNum]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    } else if (8 <= evenNum && evenNum <= 24) {
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[8]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    } else if (evenNum > 24) {
                        drawable = mContext.getResources().getDrawable(evenDriveNumDrawable[9]);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        verticalImageSpan = new VerticalImageSpan(drawable);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (verticalImageSpan != null) {
            spanttt.setSpan(verticalImageSpan, 0, EVEN_DRIVE_ICON_SPAN.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            spanttt = new SpannableString(spanttt.subSequence(EVEN_DRIVE_ICON_SPAN.length(), spanttt.length()));
        }
        return spanttt;
    }

}
