package com.zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import com.zhuang.zbanner.ZBanner;
/**
 * Created by dkzwm on 2017/3/2.
 *
 * @author dkzwm
 */

public class DrawerTransformer implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(Component page, float position) {
        if ((position >= -1 && position <= 0) || (position > 1 && position <= 2)) {
            page.setTranslationX(0);
        } else if (position > 0 && position <= 1) {
            page.setTranslationX(-page.getWidth() / 2 * position);
        }
    }
}
