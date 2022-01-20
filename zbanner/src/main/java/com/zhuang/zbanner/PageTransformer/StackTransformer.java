package com.zhuang.zbanner.PageTransformer;



import com.zhuang.zbanner.ZBanner;
import ohos.agp.components.Component;

/**
 * Created by zhuang on 2017/12/20.
 */

public class StackTransformer implements ZBanner.ZBannerPageTransformer {
    @Override
    public void transformPage(Component view, float position) {
        int pageWidth = view.getWidth();
        if (position < 1 && position > 0) {
            view.setTranslationX(pageWidth * -position);
        } else if (position <= 0 && position >= -1) {
            view.setTranslationX(0);
        }
    }
}
