package com.pgi.convergencemeetings.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by amit1829 on 11/15/2017.
 */

public class ConvergenceWebView extends WebView {

    private OnScrollChangedCallback scrollViewListener = null;

    public interface OnScrollChangedCallback {
        void onScrollChanged(ConvergenceWebView convergenceWebView, int x, int y, int oldx, int oldy);
    }

    public ConvergenceWebView(Context context) {
        super(context);
    }

    public ConvergenceWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ConvergenceWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(OnScrollChangedCallback scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}
