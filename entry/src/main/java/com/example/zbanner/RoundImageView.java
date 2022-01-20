package com.example.zbanner;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Image;
import ohos.agp.components.element.Element;
import ohos.agp.render.Canvas;
import ohos.agp.render.Paint;
import ohos.agp.utils.Matrix;
import ohos.app.Context;
import ohos.media.image.PixelMap;

/**
 * Created by zhuang on 2018/1/5.
 */
public  class RoundImageView{

//public class RoundImageView extends Image {
    /**
     * 圆形模式
     */
//    private static final int MODE_CIRCLE = 1;
//    /**
//     * 普通模式
//     */
//    private static final int MODE_NONE = 0;
//    /**
//     * 圆角模式
//     */
//    private static final int MODE_ROUND = 2;
//    private Paint mPaint;
//    private int currMode = 0;
//    /**
//     * 圆角半径
//     */
//    private int currRound = dp2px(10);
//
//    public RoundImageView(Context context) {
//        super(context);
//        initViews();
//    }
//
//    public RoundImageView(Context context, AttrSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public RoundImageView(Context context, AttrSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initViews();
//    }
//
//    private void initViews() {
//        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        Element mDrawable = getDrawable();
//        Element mDrawMatrix = getImageElement();
//        if (mDrawable == null) {
//            return; // couldn't resolve the URI
//        }
//
//        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
//            return;     // nothing to draw (empty bounds)
//        }
//
//        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
//            mDrawable.draw(canvas);
//        } else {
//            final int saveCount = canvas.getSaveCount();
//            canvas.save();
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                if (getCropToPadding()) {
//                    final int scrollX = getScrollX();
//                    final int scrollY = getScrollY();
//                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
//                            scrollX + getRight() - getLeft() - getPaddingRight(),
//                            scrollY + getBottom() - getTop() - getPaddingBottom());
//                }
//            }
//            canvas.translate(getPaddingLeft(), getPaddingTop());
//            PixelMap bitmap = drawable2Bitmap(mDrawable);
//            mPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
//            canvas.drawRoundRect(new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom()),
//                    currRound, currRound, mPaint);
//            canvas.restoreToCount(saveCount);
//        }
//    }
//
//    /**
//     * drawable转换成bitmap
//     */
//    private PixelMap drawable2Bitmap(Element drawable) {
//        if (drawable == null) {
//            return null;
//        }
//        PixelMap bitmap = PixelMap.createBitmap(getWidth(), getHeight(), PixelMap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        //根据传递的scaletype获取matrix对象，设置给bitmap
//        Matrix matrix = getImageMatrix();
//        if (matrix != null) {
//            canvas.concat(matrix);
//        }
//        drawable.draw(canvas);
//        return bitmap;
//    }
//
//    private int dp2px(float value) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
//    }
}
