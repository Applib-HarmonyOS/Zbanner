package com.zhuang.zbanner.PageTransformer;


import com.zhuang.zbanner.ZBanner;
import ohos.agp.components.Component;

public class AccordionTransformer implements ZBanner.ZBannerPageTransformer {


    /**
     * @param page     在切换的页面
     * @param position 正在切换的页面相对于当前显示在正中间的页面的位置
     *                 0表示当前页，1表示右侧一页，-1表示左侧一页。
     *                 注意，这几个都是临界值，position在页面切换过程中会一直改变
     *                 例如左移的话，当前页的position会从0减少到-1，切换完成后就变成左侧一页了。
     */

    @Override
    public void transformPage(Component component, float position) {
        final float width = component.getWidth();
        if (position >= 0 && position <= 1) {
            component.setTranslationX(-width * position);
            component.setPivotX(width);
            component.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            component.setTranslationX(0);
            component.setPivotX(0);
            component.setScaleX(1f);
        }
    }
}
