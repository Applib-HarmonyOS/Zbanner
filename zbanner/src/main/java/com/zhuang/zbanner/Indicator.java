package com.zhuang.zbanner;

import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.Component.EstimateSizeListener;
import ohos.agp.components.element.Element;
import ohos.agp.render.Canvas;
import ohos.app.Context;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuang on 2017/12/12.
 */

public class Indicator extends Component implements EstimateSizeListener, Component.DrawTask {

    private List<ItemInfo> mItems = new ArrayList<>();
    private int mOldPosition;
    private int mCount;

    private Element mIndicatorSelectIcon; //指示器被选中时的图标
    private Element mIndicatorUnSelectIcon; //指示器未被选中时的图标
    private int mIndicatorIconSize; //指示器的图标大小
    private int mIndicatorGap; //指示器图标之间的间隔

    /**
     *  Indicator .
     *
     * @param context fdfd
     * @param builder .
     */
    public Indicator(Context context, Builder builder) {
        super(context);
        mIndicatorSelectIcon = builder.indicatorSelectIcon;
        mIndicatorUnSelectIcon = builder.indicatorUnSelectIcon;
        mIndicatorIconSize = builder.indicatorIconSize;
        mIndicatorGap = builder.mIndicatorGap;
    }

    public Indicator(Context context) {
        super(context);
    }

    public Indicator(Context context,  AttrSet attrs) {
        super(context, attrs);
    }

    public Indicator(Context context, AttrSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onEstimateSize(int widthMeasureSpec, int heightMeasureSpec) {
        int width = mCount * mIndicatorIconSize + (mCount - 1) * mIndicatorGap;
        int height = mIndicatorIconSize;


        setEstimatedSize(
                ohos.agp.components.Component.EstimateSpec.getSizeWithMode(
                        width, ohos.agp.components.Component.EstimateSpec.NOT_EXCEED
                ),
                ohos.agp.components.Component.EstimateSpec.getSizeWithMode(
                        height, ohos.agp.components.Component.EstimateSpec.NOT_EXCEED
                )
        );

        //        setMeasuredDimension(width, height);
        return  true;
    }

    @Override
    public void onDraw(ohos.agp.components.Component component, Canvas canvas) {
        int left = 0;
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo itemInfo = mItems.get(i);
            if (itemInfo.isSelect) {
                mIndicatorSelectIcon.setBounds(left, 0, left + mIndicatorIconSize, mIndicatorIconSize);
                mIndicatorSelectIcon.drawToCanvas(canvas);
            } else {
                mIndicatorUnSelectIcon.setBounds(left, 0, left + mIndicatorIconSize, mIndicatorIconSize);
                mIndicatorUnSelectIcon.drawToCanvas(canvas);
            }
            left += mIndicatorIconSize + mIndicatorGap;
        }
    }

    public void setCount(int count) {
        this.mCount = count;
        mOldPosition = 0;
        mItems.clear();
        for (int i = 0; i < mCount; i++) {
            ItemInfo itemInfo = new ItemInfo();
            if (i == 0) {
                itemInfo.isSelect = true;
            }
            mItems.add(itemInfo);
        }
        invalidate();
        postLayout();
    }

    /**
     * setSelectPosition .
     *
     * @param position .
     */

    public void setSelectPosition(int position) {
        if (mOldPosition == position) {
            return;
        }

        if (mItems.size() == 0) {
            return;
        }

        mItems.get(position).isSelect = true;
        mItems.get(mOldPosition).isSelect = false;
        mOldPosition = position;
        invalidate();
    }




    private class ItemInfo {
        boolean isSelect;
    }

    /**
     * setBuilder .
     * @param builder .
     */
    public void setBuilder(Builder builder) {
        mIndicatorSelectIcon = builder.indicatorSelectIcon;
        mIndicatorUnSelectIcon = builder.indicatorUnSelectIcon;
        mIndicatorIconSize = builder.indicatorIconSize;
        mIndicatorGap = builder.mIndicatorGap;
        invalidate();
    }

    /**
     * Builder .
     */
    public static class Builder {
        Element indicatorSelectIcon; //指示器被选中时的图标
        Element indicatorUnSelectIcon; //指示器未被选中时的图标
        int indicatorIconSize; //指示器的图标大小
        int mIndicatorGap; //指示器图标之间的间隔

        public Indicator build(Context context) {
            Indicator indicator = new Indicator(context, this);
            return indicator;
        }

        public Builder indicatorGap(int indicatorGap) {
            this.mIndicatorGap = indicatorGap;
            return this;
        }

        public Builder indicatorSelectIcon(Element indicatorSelectIcon) {
            this.indicatorSelectIcon = indicatorSelectIcon;
            return this;
        }

        public Builder indicatorUnSelectIcon(Element indicatorUnSelectIcon) {
            this.indicatorUnSelectIcon = indicatorUnSelectIcon;
            return this;
        }

        public Builder indicatorIconSize(int indicatorIconSize) {
            this.indicatorIconSize = indicatorIconSize;
            return this;
        }

    }
}
