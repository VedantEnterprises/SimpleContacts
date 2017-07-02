package com.cj.simplecontacts.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cj.simplecontacts.R;

/**
 * Created by chenjun on 2017/6/17.
 */

public class IndexSiderBar extends View{
    private Paint paint = new Paint();

    private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
    public static String[] letter = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#" };
    private int choose = -1;// 选中
    private TextView mTextDialog;
    private int textSize = 30;
    private float sigleHeight;
    public IndexSiderBar(Context context) {
        super(context);

    }

    public IndexSiderBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);

    }

    public IndexSiderBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme()
                                        .obtainStyledAttributes(attrs, R.styleable.IndexSiderBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.IndexSiderBar_textsize:
                    textSize = typedArray.getDimensionPixelSize(attr, 30);
                    break;

            }
        }
        typedArray.recycle();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int width = getWidth();
        sigleHeight = ((float)height)/(letter.length);
        for(int i = 0;i < letter.length;i++){
            paint.setColor(Color.rgb(33,65,98));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setAntiAlias(true);
            paint.setTextSize(textSize);
            if(i == choose){
                paint.setColor(Color.parseColor("#3399ff"));
                paint.setFakeBoldText(true);
            }
            float xPos = width/2 - paint.measureText(letter[i])/2;
            float yPos = sigleHeight + i*sigleHeight;
            canvas.drawText(letter[i],xPos,yPos,paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();
        int oldChoose = choose;
        OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
        int index = (int) (y/getHeight()*letter.length);//点击的位置在数据的下标

        switch (action){
            case MotionEvent.ACTION_UP:
                setBackgroundDrawable(new ColorDrawable(0x00000000));
                choose = -1;
                invalidate();
                if(mTextDialog != null){
                    mTextDialog.setVisibility(INVISIBLE);
                }
                break;
            default:
                if(oldChoose != index){
                    if(index >= 0 && index < letter.length){
                        if(listener != null){
                            listener.onTouchingLetterChanged(letter[index]);
                        }
                        if(mTextDialog != null){
                            mTextDialog.setText(letter[index]);
                            mTextDialog.setVisibility(VISIBLE);
                        }
                        choose = index;
                        invalidate();
                    }
                }
                break;

        }

        return true;
    }

    public void setTextDialog(TextView textDialog){
        this.mTextDialog = textDialog;
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
        this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
    }

    public interface OnTouchingLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }
}
