package com.wilson.customviewdemo_codeviewer;

// https://www.youtube.com/watch?v=ktbYUrlN_Ws
// https://www.youtube.com/watch?v=-8M5nDABiqg
// Measure starts first

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class CodeViewer extends View {
    private int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor;
    private int mLanguage, mTabLength;
    private boolean mWrapLines;

    public CodeViewer(Context context) {
        super(context);
        init(null, 0);
    }

    public CodeViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CodeViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CodeViewer);

        try{
            mWrapLines     = a.getBoolean(R.styleable.CodeViewer_wrapLines, true);
            mLanguage      = a.getInteger(R.styleable.CodeViewer_language, 0);
            mTabLength     = a.getInteger(R.styleable.CodeViewer_tabLength, 4);
            mKeywordColor  = a.getColor(R.styleable.CodeViewer_keywordColor, Color.RED);
            mVariableColor = a.getColor(R.styleable.CodeViewer_variableColor, Color.BLUE);
            mPrimitiveColor = a.getColor(R.styleable.CodeViewer_primitiveColor, Color.RED);
            mLiteralColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GREEN);
        }finally{
            a.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the text.
//        canvas.drawText(mExampleString,
//                paddingLeft + (contentWidth - mTextWidth) / 2,
//                paddingTop + (contentHeight + mTextHeight) / 2,
//                mTextPaint);
    }

}
