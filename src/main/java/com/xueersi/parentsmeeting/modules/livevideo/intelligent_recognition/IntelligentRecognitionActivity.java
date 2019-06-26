package com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xueersi.parentsmeeting.modules.livevideo.R;
import com.xueersi.parentsmeeting.modules.livevideo.intelligent_recognition.widget.IntelligentRecognitionFragment;

public class IntelligentRecognitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelligent_recognition);
        IntelligentRecognitionRecord intelligentRecognitionRecord =
                getIntent().getParcelableExtra("intelligentRecognitionRecord");
        Bundle bundle = new Bundle();
        bundle.putParcelable("intelligentRecognitionRecord", intelligentRecognitionRecord);
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.container,
                        IntelligentRecognitionFragment.newInstance(bundle),
                        "f1")
                .commit();
    }
}
