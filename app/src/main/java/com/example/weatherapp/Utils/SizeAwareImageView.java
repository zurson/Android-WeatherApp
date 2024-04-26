package com.example.weatherapp.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class SizeAwareImageView extends AppCompatImageView {

    private int lastWidth = 0;
    private int lastHeight = 0;
    private TypedArray viewRefs = null;
    private List<SizeAwareImageView> views = new ArrayList<>();
    private OnSizeChangedListener onSizeChangedListener = new OnSizeChangedListener() {
        @Override
        public void onSizeChanged(SizeAwareImageView view, int width, int height) {
            resolveViews();
            for (SizeAwareImageView it : views) {
                if (!view.equals(it)) {
                    it.setImageDrawable(view.getDrawable());
                }
            }
        }
    };

    public SizeAwareImageView(Context context) {
        super(context);
    }

    public SizeAwareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SizeAwareImageView);
        int groupResId = a.getResourceId(R.styleable.SizeAwareImageView_img_group, 0);
        if (groupResId > 0) {
            viewRefs = getResources().obtainTypedArray(groupResId);
        }
        a.recycle();
    }

    public SizeAwareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SizeAwareImageView);
        int groupResId = a.getResourceId(R.styleable.SizeAwareImageView_img_group, 0);
        if (groupResId > 0) {
            viewRefs = getResources().obtainTypedArray(groupResId);
        }
        a.recycle();
    }

    public void resolveViews() {
        if (viewRefs != null) {
            for (int i = 0; i < viewRefs.length(); i++) {
                int resId = viewRefs.getResourceId(i, 0);
                SizeAwareImageView v = getRootView().findViewById(resId);
                if (v != null) {
                    views.add(v);
                } else {
                    Log.w(TAG, "Resource: " + resId + " not found at idx: " + i);
                }
            }
            viewRefs.recycle();
            viewRefs = null;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != lastWidth || h != lastHeight) {
            lastWidth = w;
            lastHeight = h;
            onSizeChangedListener.onSizeChanged(this, w, h);
        }
    }

    public interface OnSizeChangedListener {
        void onSizeChanged(SizeAwareImageView view, int width, int height);
    }

    private static final String TAG = SizeAwareImageView.class.getSimpleName();
}

