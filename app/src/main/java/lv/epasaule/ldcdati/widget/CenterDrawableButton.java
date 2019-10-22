package lv.epasaule.ldcdati.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import lv.epasaule.ldcdati.R;

public class CenterDrawableButton extends AppCompatButton {

    private Drawable mDrawableCenter;

    public CenterDrawableButton(Context context) {
        super(context);
        init(context, null);
    }

    public CenterDrawableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CenterDrawableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs){
        //if (isInEditMode()) return;
        if(attrs!=null){
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs, R.styleable.CenterDrawableButton, 0, 0);

            try {
                setCenterDrawable(a.getDrawable(R.styleable.CenterDrawableButton_drawableCenter));

            } finally {
                a.recycle();
            }

        }
    }

    public void setCenterDrawable(int center) {
        if(center==0){
            setCenterDrawable(null);
        }else
            setCenterDrawable(getContext().getResources().getDrawable(center));
    }
    public void setCenterDrawable(@Nullable Drawable center) {
        int[] state;
        state = getDrawableState();
        if (center != null) {
            center.setState(state);
            center.setBounds(0, 0, center.getIntrinsicWidth(), center.getIntrinsicHeight());
            center.setCallback(this);
        }
        mDrawableCenter = center;
        invalidate();
        requestLayout();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mDrawableCenter!=null) {
            setMeasuredDimension(Math.max(getMeasuredWidth(), mDrawableCenter.getIntrinsicWidth()),
                    Math.max(getMeasuredHeight(), mDrawableCenter.getIntrinsicHeight()));
        }
    }
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mDrawableCenter != null) {
            int[] state = getDrawableState();
            mDrawableCenter.setState(state);
            mDrawableCenter.setBounds(0, 0, mDrawableCenter.getIntrinsicWidth(),
                    mDrawableCenter.getIntrinsicHeight());
        }
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        if (mDrawableCenter != null) {
            Rect rect = mDrawableCenter.getBounds();
            canvas.save();
            canvas.translate(getWidth() / 2 - rect.right / 2, getHeight() / 2 - rect.bottom / 2);
            mDrawableCenter.draw(canvas);
            canvas.restore();
        }
    }
}
