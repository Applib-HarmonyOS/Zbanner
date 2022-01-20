package com.zhuang.zbanner;

import com.example.zbanner.ResourceTable;
import com.example.zbanner.util.ResUtil;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.AttrSet;
import ohos.agp.components.Component;
import ohos.agp.components.StackLayout;
import ohos.agp.components.element.Element;
import ohos.agp.components.element.VectorElement;
import ohos.agp.render.render3d.Engine;
import ohos.agp.utils.LayoutAlignment;
import ohos.app.Context;

import java.util.concurrent.atomic.AtomicInteger;

import static ohos.agp.components.AttrHelper.fp2px;

/**
 * Created by zhuang on 2017/12/13.
 */

public class ZBanner extends StackLayout {
    private final static int INDICATOR_GRAVITY_BOTTOM_LEFT = 0;
    private final static int INDICATOR_GRAVITY_BOTTOM_CENTER = 1;
    private final static int INDICATOR_GRAVITY_BOTTOM_RIGHT = 2;
    private static final int DURATION_ANIMAL = 1000; // ms
    private static final int DURATION_DISPLAY = 2000; // ms

    private ZBannerRaw zBannerRaw;
    private Indicator indicator;
    private Indicator.Builder indicatorBuilder;

    /**
     * xml中可配置的变量
     */
    private int mPageGap;//页面之间的间隔
    private float mWidthFactor = 1f;//页面宽度倍数
    private int mOffscreenPageLimit = 2;//缓存页面
    private Element indicatorSelectIcon;//指示器被选中时的图标
    private Element indicatorUnSelectIcon;//指示器未被选中时的图标
    private int indicatorGravity = INDICATOR_GRAVITY_BOTTOM_CENTER;//指示器的位置
    private int indicatorIconSize = 12;//指示器的图标大小
    private boolean showIndicator = true;//是否显示指示器
    private int indicatorMargin = 5;//dp
    private int mIndicatorGap = 5;//指示器图标之间的间隔
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);
    private OnPageChangeLister mOnPageChangeLister;

    public ZBanner(Context context) {
        super(context);
        initView(null, 0, context);
    }

    public ZBanner( Context context,  AttrSet attrs) {
        super(context, attrs);
        initView(attrs, 0, context);
    }

    public ZBanner( Context context,AttrSet attrs, int defStyleAttr) {
        super(context, attrs, String.valueOf(defStyleAttr));
        initView(attrs, defStyleAttr, context);
    }

    void initView(AttrSet attrs, int defStyle, Context context) {

        mPageGap = attrs.getAttr("ZBanner_pageGap").isPresent() ? attrs.getAttr("pageGap").get().getIntegerValue(): mPageGap;  // getDimensionPixelSize(R.styleable.ZBanner_pageGap, mPageGap);
        mWidthFactor = attrs.getAttr("ZBanner_widthFactor").isPresent() ? attrs.getAttr("ZBanner_widthFactor").get().getFloatValue() : mWidthFactor; //getFloat(R.styleable.ZBanner_widthFactor, mWidthFactor);
        mOffscreenPageLimit = attrs.getAttr("ZBanner_offscreenPageLimit").isPresent() ? attrs.getAttr("ZBanner_offscreenPageLimit").get().getIntegerValue() : mOffscreenPageLimit; // getInt(R.styleable.ZBanner_offscreenPageLimit, mOffscreenPageLimit);
        indicatorSelectIcon = attrs.getAttr("ZBanner_indicatorSelectIcon").isPresent() ? attrs.getAttr("ZBanner_indicatorSelectIcon").get().getElement() : null; // getDrawable(R.styleable.ZBanner_indicatorSelectIcon);
        indicatorUnSelectIcon = attrs.getAttr("ZBanner_indicatorUnSelectIcon").isPresent() ? attrs.getAttr("ZBanner_indicatorUnSelectIcon").get().getElement() : null; // .getDrawable(R.styleable.ZBanner_indicatorUnSelectIcon);
        indicatorGravity =attrs.getAttr("ZBanner_indicatorGravity").isPresent() ?  attrs.getAttr("ZBanner_indicatorGravity").get().getIntegerValue() : indicatorGravity;// a.getInt(R.styleable., indicatorGravity);
        indicatorIconSize = attrs.getAttr("ZBanner_indicatorIconSize").isPresent() ? attrs.getAttr("ZBanner_indicatorIconSize").get().getDimensionValue() : AttrHelper.vp2px(indicatorIconSize, context) ; // a.getDimensionPixelSize(R.styleable.ZBanner_indicatorIconSize, dpToPx(indicatorIconSize));
        showIndicator = attrs.getAttr("ZBanner_showIndicator").isPresent() ? attrs.getAttr("ZBanner_showIndicator").get().getBoolValue() : showIndicator; //a.getBoolean(R.styleable.ZBanner_showIndicator, showIndicator);
        indicatorMargin = attrs.getAttr("ZBanner_indicatorMargin").isPresent() ? attrs.getAttr("ZBanner_indicatorMargin").get().getDimensionValue() : AttrHelper.vp2px(indicatorMargin,context); //dpToPx(indicatorMargin); // a.getDimensionPixelSize(R.styleable.ZBanner_indicatorMargin, dpToPx(indicatorMargin));
        mIndicatorGap =attrs.getAttr("ZBanner_indicatorGap").isPresent() ? attrs.getAttr("ZBanner_indicatorGap").get().getDimensionValue() : AttrHelper.vp2px(mIndicatorGap, context) ;//dpToPx(mIndicatorGap) ;// a.getDimensionPixelSize(R.styleable.ZBanner_indicatorGap, dpToPx(mIndicatorGap));

        if (mWidthFactor < 0.5f || mWidthFactor > 1f) {
            throw new RuntimeException("mWidthFactor的区间只能是[0.5f,1f]");
        }
        if (mOffscreenPageLimit < 1) {
            throw new RuntimeException("mOffscreenPageLimit必须>=1");
        }
        if (indicatorSelectIcon == null) {
            //indicatorSelectIcon = ResUtil.getPixelMapDrawable(context  , ResourceTable.Layout_ic_indicator_select);
            indicatorSelectIcon = new VectorElement(context  , ResourceTable.Graphic_ic_indicator_select);
        }
        if (indicatorUnSelectIcon == null) {
            //indicatorUnSelectIcon = ResUtil.getPixelMapDrawable(context, ResourceTable.Graphic_ic_indicator_unselect);
            indicatorUnSelectIcon = new VectorElement(context  , ResourceTable.Graphic_ic_indicator_unselect);
        }

        initBanner();
        initIndicator(context, attrs);
    }

    void initIndicator(Context context, AttrSet attrSet) {
        indicatorBuilder = new Indicator.Builder()
                .indicatorSelectIcon(indicatorSelectIcon)
                .indicatorUnSelectIcon(indicatorUnSelectIcon)
                .indicatorIconSize(indicatorIconSize)
                .indicatorGap(mIndicatorGap);
        indicator = indicatorBuilder.build(getContext());

        if (!showIndicator) return;

        ZBannerRaw.LayoutParams params = new ZBannerRaw.LayoutParams();
        switch (indicatorGravity) {
            case (INDICATOR_GRAVITY_BOTTOM_LEFT):
                params.gravity = LayoutAlignment.BOTTOM;
                break;
            case (INDICATOR_GRAVITY_BOTTOM_CENTER):
                params.gravity = LayoutAlignment.BOTTOM | LayoutAlignment.HORIZONTAL_CENTER;
                break;
            case (INDICATOR_GRAVITY_BOTTOM_RIGHT):
                params.gravity = LayoutAlignment.BOTTOM | LayoutAlignment.RIGHT;
                break;
        }
        params.setMargins(indicatorMargin, indicatorMargin, indicatorMargin, indicatorMargin);
        indicator.setLayoutConfig(params);
        addComponent(indicator);
        zBannerRaw.setIndicator(indicator);
    }

    public void setIndicator(Indicator indicator) {
        zBannerRaw.setIndicator(indicator);
        indicator.setBuilder(indicatorBuilder);
    }

    void initBanner() {
        zBannerRaw = new ZBannerRaw.Builder()
                .pageGap(mPageGap)
                .widthFactor(mWidthFactor)
                .offscreenPageLimit(mOffscreenPageLimit)
                .build(getContext());
        zBannerRaw.setId(generateBannerViewId());
        addComponent(zBannerRaw);
    }

    private int generateBannerViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }

    public void setAdapter(ZBannerAdapter adapter) {
        zBannerRaw.setAdapter(adapter);
    }

    public void setPageTransformer(ZBannerPageTransformer pageTransformer) {
        zBannerRaw.setPageTransformer(true, pageTransformer);
    }

    /**
     * @param displayDuration 页面展示的时间 ms
     * @param animalDuration  页面滑动的时间 ms
     */
    public void star(int displayDuration, int animalDuration) {
        zBannerRaw.setDisplayDuration(displayDuration);
        zBannerRaw.setAnimalDuration(animalDuration);
        zBannerRaw.star();
    }

    public void star() {
        zBannerRaw.setDisplayDuration(DURATION_DISPLAY);
        zBannerRaw.setAnimalDuration(DURATION_ANIMAL);
        zBannerRaw.star();
    }

    public void stop() {
        zBannerRaw.stop();
    }

    public void setOnPageChangeLister(OnPageChangeLister mOnPageChangeLister) {
        zBannerRaw.setOnPageChangeLister(mOnPageChangeLister);
    }

    public void setCurrentItem(int position){
        zBannerRaw.setCurrentItem(position);
    }

    /**
     * 提供一个页面切换时的接口，可以自定义转换动画
     */
    public interface ZBannerPageTransformer {
        /**
         * @param page     在切换的页面
         * @param position 正在切换的页面相对于当前显示在正中间的页面的位置
         *                 0表示当前页，1表示右侧一页，-1表示左侧一页。
         *                 注意，这几个都是临界值，position在页面切换过程中会一直改变
         *                 例如左移的话，当前页的position会从0减少到-1，切换完成后就变成左侧一页了。
         */
        void transformPage(Component page, float position);
    }

    public interface OnPageChangeLister {
        void change(int position);
    }

}
