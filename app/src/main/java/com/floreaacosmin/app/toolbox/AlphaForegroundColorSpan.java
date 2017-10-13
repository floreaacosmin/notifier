package com.floreaacosmin.app.toolbox;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;

@SuppressLint("ParcelCreator")
public class AlphaForegroundColorSpan extends ForegroundColorSpan {

    private float mAlpha;

    public AlphaForegroundColorSpan(int color) {
        super(color);
	}

    @SuppressWarnings("unused")
    public AlphaForegroundColorSpan(Parcel src) {
        super(src);
        mAlpha = src.readFloat();
    }

    @Override
	public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeFloat(mAlpha);
    }

	@Override
	public void updateDrawState(TextPaint ds) {
		ds.setColor(getAlphaColor());
	}

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    @SuppressWarnings("unused")
    public float getAlpha() {
        return mAlpha;
    }

    private int getAlphaColor() {
        int foregroundColor = getForegroundColor();
        return Color.argb((int) (mAlpha * 255), Color.red(foregroundColor), Color.green(foregroundColor), Color.blue(foregroundColor));
    }
}