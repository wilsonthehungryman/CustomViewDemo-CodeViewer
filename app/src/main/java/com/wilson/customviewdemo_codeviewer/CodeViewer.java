package com.wilson.customviewdemo_codeviewer;

// https://www.youtube.com/watch?v=ktbYUrlN_Ws
// https://www.youtube.com/watch?v=-8M5nDABiqg
// Measure starts first

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: document your custom view class.
 */
public class CodeViewer extends View {
    private int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom, mContentWidth, mContentHeight;
    private int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor;
    private int mLanguage, mTabLength;
    private boolean mWrapLines;
    private String rawSourceCode, mTab;
    private Paint mDefaultPaint;
    private StringBuilder mStringBuilder;
    private Pattern mBlockBeginSign, mBlockEndSign, mKeyWords;
    private Matcher matcher;

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
            mWrapLines      = a.getBoolean(R.styleable.CodeViewer_wrapLines, true);
            mLanguage       = a.getInteger(R.styleable.CodeViewer_language, 0);
            mTabLength      = a.getInteger(R.styleable.CodeViewer_tabLength, 4);
            mKeywordColor   = a.getColor(R.styleable.CodeViewer_keywordColor, Color.RED);
            mVariableColor  = a.getColor(R.styleable.CodeViewer_variableColor, Color.BLUE);
            mPrimitiveColor = a.getColor(R.styleable.CodeViewer_primitiveColor, Color.RED);
            mLiteralColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GREEN);

            rawSourceCode = "";

            mTab = new String(new char[mTabLength]).replace("\0", " ");
            mStringBuilder = new StringBuilder();
            mBlockBeginSign = Pattern.compile("canvas");
            mBlockEndSign = Pattern.compile("\\}");
            mKeyWords = Pattern.compile("boolean|do|if|private|this|break|double|implements|protected|throw|byte|else|import|public|throws|case|enum|instanceof|return|transient|catch|extends|int|short|try|char|final|interface|static|void|class|finally|long|strictfp|volatile|const|float|native|super|while");

            mDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDefaultPaint.setColor(Color.BLACK);
            mDefaultPaint.setTextSize(40);
            mDefaultPaint.setTextAlign(Paint.Align.LEFT);

        }finally{
            a.recycle();
        }
    }

    public void setSourceCode(String code){
        rawSourceCode = code;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;

        String[] lines = rawSourceCode.split("\\n");
        int level = 0;
        float linePointer =  mPaddingTop + mDefaultPaint.getTextSize();

        for(String line : lines){
            int count = level;
            mStringBuilder.setLength(0);
            while(count > 0){ mStringBuilder.append(mTab); count--; }
            mStringBuilder.append(line.trim());

            matcher = mBlockBeginSign.matcher(mStringBuilder.toString());
            if(matcher.matches()){
                level++;
            }

            matcher = mBlockEndSign.matcher(mStringBuilder.toString());
            if(matcher.matches()){
                level--;
                mStringBuilder.delete(0, mTabLength);
            }

            // Draw the text.
            canvas.drawText(mStringBuilder.toString(),
                    mPaddingLeft,
                    linePointer,
                    mDefaultPaint);
            linePointer += mDefaultPaint.getTextSize() + 5;
        }

//        // Draw the text.
//        canvas.drawText(rawSourceCode,
//                mPaddingLeft,
//                mPaddingTop + mDefaultPaint.getTextSize(),
//                mDefaultPaint);
//        paddingLeft + (contentWidth - mTextWidth) / 2,
//        paddingTop + (contentHeight + mTextHeight) / 2,
    }

}
