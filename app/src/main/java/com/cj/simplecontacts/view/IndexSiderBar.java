package com.cj.simplecontacts.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenjun on 2017/6/17.
 */

public class IndexSiderBar extends View{
    private Paint paint;
    public IndexSiderBar(Context context) {
        super(context);
        init();
    }

    public IndexSiderBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public IndexSiderBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        
    }
}
