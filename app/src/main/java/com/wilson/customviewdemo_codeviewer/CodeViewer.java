package com.wilson.customviewdemo_codeviewer;

// https://www.youtube.com/watch?v=ktbYUrlN_Ws
// https://www.youtube.com/watch?v=-8M5nDABiqg
// https://plus.google.com/u/0/+ArpitMathur/posts/cT1EuBbxEgN
// Measure starts first (after create)

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
 * CodeViewer extends the View class.
 * It is a custom view the uses syntax highlighting for a better view
 * of source code.
 *
 * <pre>
 * Revision Log
 * Who       When       Reason
 * --------- ---------- ----------------------------------
 * Wilson    03\20\17   Created - support for Java
 * </pre>
 *
 * @author Wilson
 * @version 1.0
 */
public class CodeViewer extends View {
    // Common attributes
    int mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom, mContentWidth, mContentHeight;

    // For colours, storing as int oppossed to Color works better
    int mKeywordColor, mVariableColor, mPrimitiveColor, mLiteralColor, mCommentColor;

    // Options
    private int mLanguage, mTabLength;
    private boolean mWrapLines;

    // more settings
    private String rawSourceCode, mTab;

    // These attributes are for syntax highlighting
    private Paint mDefaultPaint, mKeyWordPaint, mCommentPaint, mLiteralPaint, mVariablePaint, mPrimitivePaint;
    private StringBuilder mStringBuilder, mVariableRegex;
    private Pattern mBlockBeginSign, mBlockEndSign, mLiteralBegin, mLiteralEnd, mSingleQuoteLiteral, mKeyWords, mComment, mNumeric, mPrimitives;
    private Matcher matcher;

    /**
     * Constructor for CodeViewer
     * @param context
     */
    public CodeViewer(Context context) {
        super(context);
        init(null, 0);
    }

    /**
     * Constructor for CodeViewer
     * @param context
     * @param attrs
     */
    public CodeViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    /**
     * Constructor for CodeViewer
     * @param context
     * @param attrs
     * @param defStyle
     */
    public CodeViewer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Performs the construction logic. Initializes members.
     * Comes before onMeasure.
     * One of the main purposes of this method is to initialize data that will be used later,
     * in onMeasure and onDraw. This includes things like StringBuilders and Paints.
     * @param attrs The AttributeSet to use
     * @param defStyle The defStyle to use
     */
    private void init(AttributeSet attrs, int defStyle) {
        // Get a typed array. This is used to access the individual attributes (from attrs/AttributeSet)
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CodeViewer);

        try{
            // Grab all the attributes into private members
            mWrapLines      = a.getBoolean(R.styleable.CodeViewer_wrapLines, false);
            mLanguage       = a.getInteger(R.styleable.CodeViewer_language, 0);
            mTabLength      = a.getInteger(R.styleable.CodeViewer_tabLength, 4);
            mKeywordColor   = a.getColor(R.styleable.CodeViewer_keywordColor, Color.RED);
            mVariableColor  = a.getColor(R.styleable.CodeViewer_variableColor, Color.BLUE);
            mPrimitiveColor = a.getColor(R.styleable.CodeViewer_primitiveColor, mKeywordColor);
            mLiteralColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GREEN);
            mCommentColor   = a.getColor(R.styleable.CodeViewer_literalColor, Color.GRAY);

            // Initialize the rawSourceCode if null
            // Raw source code must be set after creation (this method)
            if(rawSourceCode == null)
                rawSourceCode = "";

            // Create the tab
            mTab = new String(new char[mTabLength]).replace("\0", " ");

            // Initialize StringBuilders
            mStringBuilder = new StringBuilder();
            mVariableRegex = new StringBuilder();

            // Used in multiple languages, matches numbers
            mNumeric = Pattern.compile("\\d+");

            // Java
            // Set patterns to match certain lexical categories
            mBlockBeginSign = Pattern.compile("\\{");
            mBlockEndSign = Pattern.compile("\\}");
            mSingleQuoteLiteral = Pattern.compile("^'");
            mLiteralBegin = Pattern.compile("^\\\"");
            mLiteralEnd = Pattern.compile("[^\\\\\\\\]\\\\\\\\\\\\\\\"$");
            mKeyWords = Pattern.compile("\\b@\\w+\\b|\\bdo\\b|\\bif\\b|\\bprivate\\b|\\bthis\\b|\\bbreak\\b|\\bimplements\\b|\\bprotected\\b|\\bthrow\\b|\\belse\\b|\\bimport\\b|\\bpublic\\b|\\bthrows\\b|\\bcase\\b|\\benum\\b|\\binstanceof\\b|\\breturn\\b|\\btransient\\b|\\bcatch\\b|\\bextends\\b|\\btry\\b|\\bfinal\\b|\\binterface\\b|\\bstatic\\b|\\bvoid\\b|\\bclass\\b|\\bfinally\\b|\\bstrictfp\\b|\\bvolatile\\b|\\bconst\\b|\\bnative\\b|\\bsuper\\b|\\bwhile\\b");
            mPrimitives = Pattern.compile("\\bint\\b|\\bboolean\\b|\\bbyte\\b|\\bshort\\b|\\blong\\b|\\bfloat\\b|\\bdouble\\b|\\bchar\\b");
            mComment = Pattern.compile("//[\\s\\S]*");

            // Colouring
            // The default color/paint
            mDefaultPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDefaultPaint.setColor(Color.BLACK);
            mDefaultPaint.setTextSize(40);
            mDefaultPaint.setTextAlign(Paint.Align.LEFT);

            // Use the default paint as a base, then set the color
            // for each different syntax color

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
            // From what I understand this caches the typedarray for quicker use later
            // Based on official docs
            a.recycle();
        }
    }

    /**
     * Set the source code to display
     * @param code the code to display in String format
     */
    public void setSourceCode(String code){
        rawSourceCode = code;
    }


    /**
     * onDraw is responsible for creating the gui that is displayed to the user.
     * It's where the "magic" happens.
     * I am only drawing text but you can draw lines, shapes, and really anything you want.
     * @param canvas The canvas to draw onto
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Grab the standard attributes
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
        mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;

        // Grab the lines from the raw source code
        String[] lines = rawSourceCode.split("\\n");

        // Level is the indentation level (based on braces and such)
        int level = 0;

        // Points to the current line (vertically)
        float linePointer =  mPaddingTop + mDefaultPaint.getTextSize();

        // Loop one line at a time
        for(String line : lines){
            // use the string builder to create the tab
            int count = level;
            mStringBuilder.setLength(0);
            while(count > 0){ mStringBuilder.append(mTab); count--; }
            String tab;
            String[] words = line.trim().split(" +");

            // Word pointer points to where the next word starts (horizontal)
            float wordPointer = mPaddingLeft;

            // Used for highlighting that lasts longer than a word
            boolean inComment = false, inLiteral = false;

            // The paint to use when drawing the text
            Paint paint = mDefaultPaint;

            // Determine the level
            matcher = mBlockBeginSign.matcher(line);
            if(matcher.find()){
                level++;
            }

            matcher = mBlockEndSign.matcher(line);
            if(matcher.find()){
                level--;
                mStringBuilder.delete(0, mTabLength);
            }

            // Then create the tab
            tab = mStringBuilder.toString();

            // Draw the tab
            canvas.drawText(tab,
                    wordPointer,
                    linePointer,
                    paint);

            // move the horizontal pointer
            wordPointer += paint.measureText(tab);

            // check if this is part of a comment
            if(match(mComment, line)){
                inComment = true;
            }

            // loop through the line, one word at a time
            for(int j = 0; j < words.length; j++) {
                // Capture the word that is the bird
                String word = words[j];

                // Set the paint object and any flags depending on the match
                // Default is DefaultPaint
                if(inComment) {
                    paint = mCommentPaint;
                }else if(inLiteral) {
                    // If it is the end of the literal, change the flag.
                    if(match(mLiteralEnd, word)){
                        inLiteral = false;
                    }
                    // No need to set paint, still set from before
                }else if(match(mLiteralBegin, word)) {
                    inLiteral = true;
                    paint = mLiteralPaint;
                }else if(match(mSingleQuoteLiteral, word)){
                    paint = mLiteralPaint;
                }else if (match(mPrimitives, word)) {
                    // If it is a primitive, there should be a variable or variables next
                    // Loop through the rest of the line
                    for(int k = j +1; k < words.length; k++){
                        // Grab the next word and remove commas and semicolon
                        String possibleVar = words[k].replace(",", "").replace(";", "");
                        // Variables should only have words and numbers
                        if(!Pattern.compile("\\W").matcher(possibleVar).find()){
                            // Add the new var to the variableregex
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

                // Draw the word onto the canvas
                canvas.drawText(word + " ",
                        wordPointer,
                        linePointer,
                        paint);

                // Move the word pointer
                // Paints can have different sizes,
                // so use the paint to measure the length of the word that was just painted
                wordPointer += paint.measureText(word + " ");
            }

            // Move the line pointer
            linePointer += mDefaultPaint.getTextSize() + 5;
        }
    }

    /**
     * Match is used to confirm whether a word matches (actually find) a pattern
     * @param pattern The pattern to use
     * @param word The word to check
     * @return True if the word contains the pattern
     */
    private boolean match(Pattern pattern, String word){
        Matcher m = pattern.matcher(word);
        // find returns true if the word contains the pattern
        // matches would return true only if the entire string matches
        return m.find();
    }

}
