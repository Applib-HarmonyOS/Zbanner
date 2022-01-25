package com.zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import com.zhuang.zbanner.ZBanner;


/**
 * AccordionTransformer style ZBanner .
 */
public class AccordionTransformer1 implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(Component view, float position) {
        final float width = view.getWidth();
        if (position >= 0 && position <= 1) {
            view.setTranslationX(-width * position);
            view.setPivotX(width);
            view.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            view.setTranslationX(-width * position);
            view.setPivotX(0);
            view.setScaleX(1f + position);
        }
    }
}
