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
    int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom, mContentWidth, mContentHeight;
    int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor, mCommentColor;
    private int mLanguage, mTabLength;
    private boolean mWrapLines;
    private String rawSourceCode, mTab;
    private Paint mDefaultPaint, mKeyWordPaint, mCommentPaint, mLiteralPaint, mVariablePaint, mPrimitivePaint;
    private StringBuilder mStringBuilder, mVariableRegex;
    private Pattern mBlockBeginSign, mBlockEndSign, mLiteralBegin, mLiteralEnd, mSingleQuoteLiteral, mKeyWords, mComment, mNumeric, mPrimitives;
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
            mWrapLines      = a.getBoolean(R.styleable.CodeViewer_wrapLines, false);
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
            mVariableRegex = new StringBuilder();

            mNumeric = Pattern.compile("\\d+");

            // Java
            mBlockBeginSign = Pattern.compile("\\{");
            mBlockEndSign = Pattern.compile("\\}");
            mSingleQuoteLiteral = Pattern.compile("^'");
            mLiteralBegin = Pattern.compile("^\\\"");
            mLiteralEnd = Pattern.compile("[^\\\\\\\\]\\\\\\\\\\\\\\\"$");
            mKeyWords = Pattern.compile("\\b@\\w+\\b|\\bdo\\b|\\bif\\b|\\bprivate\\b|\\bthis\\b|\\bbreak\\b|\\bimplements\\b|\\bprotected\\b|\\bthrow\\b|\\belse\\b|\\bimport\\b|\\bpublic\\b|\\bthrows\\b|\\bcase\\b|\\benum\\b|\\binstanceof\\b|\\breturn\\b|\\btransient\\b|\\bcatch\\b|\\bextends\\b|\\btry\\b|\\bfinal\\b|\\binterface\\b|\\bstatic\\b|\\bvoid\\b|\\bclass\\b|\\bfinally\\b|\\bstrictfp\\b|\\bvolatile\\b|\\bconst\\b|\\bnative\\b|\\bsuper\\b|\\bwhile\\b");
            mPrimitives = Pattern.compile("\\bint\\b|\\bboolean\\b|\\bbyte\\b|\\bshort\\b|\\blong\\b|\\bfloat\\b|\\bdouble\\b|\\bchar\\b");
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

            mPrimitivePaint = new Paint(mDefaultPaint);
            mPrimitivePaint.setColor(mPrimitiveColor);

            mVariablePaint = new Paint(mDefaultPaint);
            mVariablePaint.setColor(mVariableColor);

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
            //String line = lines[i];
            int count = level;
            mStringBuilder.setLength(0);
            while(count > 0){ mStringBuilder.append(mTab); count--; }
            String tab;
            String[] words = line.trim().split(" +");
            float wordPointer = mPaddingLeft;
            boolean inComment = false, inLiteral = false;
            Paint paint = mDefaultPaint;

            matcher = mBlockBeginSign.matcher(line);
            if(matcher.find()){
                level++;
            }

            matcher = mBlockEndSign.matcher(line);
            if(matcher.find()){
                level--;
                mStringBuilder.delete(0, mTabLength);
            }

            tab = mStringBuilder.toString();
            canvas.drawText(tab,
                    wordPointer,
                    linePointer,
                    paint);

            wordPointer += paint.measureText(tab);


            if(match(mComment, line)){
                inComment = true;
            }

            for(int j = 0; j < words.length; j++) {
                String word = words[j];
                if(inComment) {
                    paint = mCommentPaint;
                }else if(inLiteral) {
                    if(match(mLiteralEnd, word)){
                        inLiteral = false;
                    }
                }else if(match(mLiteralBegin, word)) {
                    inLiteral = true;
                    paint = mLiteralPaint;
                }else if(match(mSingleQuoteLiteral, word)){
                    paint = mLiteralPaint;
                }else if (match(mPrimitives, word)) {
                    for(int k = j +1; k < words.length; k++){
                        String possibleVar = words[k].replace(",", "").replace(";", "");
                        if(!Pattern.compile("\\W").matcher(possibleVar).find()){
                            mVariableRegex.append("\\b" + possibleVar + "\\b|");
                        }
                    }
                    paint = mPrimitivePaint;
                }else if(mVariableRegex.length() > 0 && match(Pattern.compile(mVariableRegex.toString().substring(0, mVariableRegex.length() - 1)), word)){
                    paint = mVariablePaint;
                } else if (match(mKeyWords, word)) {
                    paint = mKeyWordPaint;
                }else if (match(mNumeric, word)){
                    paint = mLiteralPaint;
                }else{
                    paint = mDefaultPaint;
                }

                canvas.drawText(word + " ",
                        wordPointer,
                        linePointer,
                        paint);
                wordPointer += paint.measureText(word + " ");
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
