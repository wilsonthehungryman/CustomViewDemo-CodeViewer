package com.wilson.customviewdemo_codeviewer;

// https://www.youtube.com/watch?v=ktbYUrlN_Ws
// https://www.youtube.com/watch?v=-8M5nDABiqg
// https://plus.google.com/u/0/+ArpitMathur/posts/cT1EuBbxEgN
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
    private int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor, mCommentColor;
    private int mLanguage, mTabLength;
    private boolean mWrapLines;
    private String rawSourceCode, mTab;
    private Paint mDefaultPaint, mKeyWordPaint, mCommentPaint, mLiteralPaint;
    private StringBuilder mStringBuilder;
    private Pattern mBlockBeginSign, mBlockEndSign, mKeyWords, mComment, mNumeric;
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
            mPrimitiveColor = a.getColor(R.styleable.CodeViewer_primitiveColor, mKeywordColor);
            mLiteralColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GREEN);
            mCommentColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GRAY);

            rawSourceCode = "";

            mTab = new String(new char[mTabLength]).replace("\0", " ");
            mStringBuilder = new StringBuilder();

            mNumeric = Pattern.compile("\\d+");

            // Java
            mBlockBeginSign = Pattern.compile("\\{");
            mBlockEndSign = Pattern.compile("\\}");
            mKeyWords = Pattern.compile("\\bboolean\\b|\\bdo\\b|\\bif\\b|\\bprivate\\b|\\bthis\\b|\\bbreak\\b|\\bdouble\\b|\\bimplements\\b|\\bprotected\\b|\\bthrow\\b|\\bbyte\\b|\\belse\\b|\\bimport\\b|\\bpublic\\b|\\bthrows\\b|\\bcase\\b|\\benum\\b|\\binstanceof\\b|\\breturn\\b|\\btransient\\b|\\bcatch\\b|\\bextends\\b|\\bint\\b|\\bshort\\b|\\btry\\b|\\bchar\\b|\\bfinal\\b|\\binterface\\b|\\bstatic\\b|\\bvoid\\b|\\bclass\\b|\\bfinally\\b|\\blong\\b|\\bstrictfp\\b|\\bvolatile\\b|\\bconst\\b|\\bfloat\\b|\\bnative\\b|\\bsuper\\b|\\bwhile\\b");
            mComment = Pattern.compile("//[\\s\\S]*");

            // Colouring

            mDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDefaultPaint.setColor(Color.BLACK);
            mDefaultPaint.setTextSize(40);
            mDefaultPaint.setTextAlign(Paint.Align.LEFT);

            mKeyWordPaint = new Paint(mDefaultPaint);
            mKeyWordPaint.setColor(mKeywordColor);

            mCommentPaint = new Paint(mDefaultPaint);
            mCommentPaint.setColor(mCommentColor);

            mLiteralPaint = new Paint(mDefaultPaint);
            mLiteralPaint.setColor(mLiteralColor);

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
            String tab = mStringBuilder.toString();
            String[] words = line.trim().split(" +");
            float wordPointer = mPaddingLeft;
            mStringBuilder.append(line.trim());
            boolean inComment = false;

            canvas.drawText(tab,
                    wordPointer,
                    linePointer,
                    mDefaultPaint );

            wordPointer += mDefaultPaint.measureText(tab);

            matcher = mBlockBeginSign.matcher(mStringBuilder.toString());
            if(matcher.find()){
                level++;
            }

            matcher = mBlockEndSign.matcher(mStringBuilder.toString());
            if(matcher.find()){
                level--;
                mStringBuilder.delete(0, mTabLength);
            }

            if(match(mComment, line)){
                inComment = true;
            }

            for(String word : words) {
                if(inComment) {
                    canvas.drawText(word + " ",
                            wordPointer,
                            linePointer,
                            mCommentPaint);
                    wordPointer += mCommentPaint.measureText(word + " ");
                } else if (match(mKeyWords, word)) {
                    canvas.drawText(word + " ",
                            wordPointer,
                            linePointer,
                            mKeyWordPaint);
                    wordPointer += mKeyWordPaint.measureText(word + " ");
                }else if (match(mNumeric, word)){
                    canvas.drawText(word + " ",
                            wordPointer,
                            linePointer,
                            mLiteralPaint);
                    wordPointer += mLiteralPaint.measureText(word + " ");
                }else{
                    canvas.drawText(word + " ",
                            wordPointer,
                            linePointer,
                            mDefaultPaint);
                    wordPointer += mDefaultPaint.measureText(word + " ");
                }
            }

            linePointer += mDefaultPaint.getTextSize() + 5;

//            // Draw the text.
//            canvas.drawText(mStringBuilder.toString(),
//                    mPaddingLeft,
//                    linePointer,
//                    mDefaultPaint);
//            linePointer += mDefaultPaint.getTextSize() + 5;
        }

//        // Draw the text.
//        canvas.drawText(rawSourceCode,
//                mPaddingLeft,
//                mPaddingTop + mDefaultPaint.getTextSize(),
//                mDefaultPaint);
//        paddingLeft + (contentWidth - mTextWidth) / 2,
//        paddingTop + (contentHeight + mTextHeight) / 2,
    }

    private boolean match(Pattern pattern, String word){
        Matcher m = pattern.matcher(word);
        return m.find();
    }

}
