package com.zhuang.zbanner.PageTransformer;


import com.zhuang.zbanner.ZBanner;
import ohos.agp.components.Component;

public class AccordionTransformer implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(Component view, float position) {
        final float width = view.getWidth();
        if (position >= 0 && position <= 1) {
            view.setTranslationX(-width * position);
            view.setPivotX(width);
            view.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            view.setTranslationX(0);
            view.setPivotX(0);
            view.setScaleX(1f);
        }
    }
}
