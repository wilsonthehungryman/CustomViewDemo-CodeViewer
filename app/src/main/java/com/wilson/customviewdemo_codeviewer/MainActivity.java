package com.wilson.customviewdemo_codeviewer;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private CodeViewer codeViewer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        } catch (Exception e) {
            e.printStackTrace();
        }

        codeViewer = (CodeViewer)findViewById(R.id.codeViewer);
        codeViewer.setSourceCode("private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom, mContentWidth, mContentHeight;\n" +
                "private int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor, mCommentColor;\n" +
                "    private int mLanguage, mTabLength;\n" +
                "    private boolean mWrapLines;\n" +
                "    private String rawSourceCode, mTab;\n" +
                "    private Paint mDefaultPaint, mKeyWordPaint, mCommentPaint, mLiteralPaint;\n" +
                "    private StringBuilder mStringBuilder;\n" +
                "    private Pattern mBlockBeginSign, mBlockEndSign, mKeyWords, mComment, mNumeric;\n" +
                "    private Matcher matcher;\n" +
                "@Override\n" +
                "    protected void onDraw(Canvas canvas) {\n" +
                "        super.onDraw(canvas);\n" +
                "\n" +
                "        // TODO: consider storing these as member variables to reduce\n" +
                "        // allocations per draw cycle.\n" +
                "        mPaddingLeft = getPaddingLeft();\n" +
                "          mPaddingTop = getPaddingTop();\n" +
                "         mPaddingRight = getPaddingRight();\n" +
                "        mPaddingBottom = getPaddingBottom();\n" +
                "        mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;\n" +
                "        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;\n" +
                "\n" +
                "        // Draw the text.\n" +
                "        canvas.drawText(rawSourceCode,\n" +
                "                mPaddingLeft + (mContentWidth) / 2,\n" +
                "                mPaddingTop + (mContentHeight) / 2,\n" +
                "                mDefaultPaint);\n" +
                "        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;\n" +
                "\n" +
                "        int final number = 19.19" +
                "\n" +
                "        String literal = \" something\"" +
                "    }");
    }
}
