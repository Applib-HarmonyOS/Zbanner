package com.zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import ohos.agp.render.ThreeDimView;
import com.zhuang.zbanner.ZBanner;


/**
 * Flip3DTransformer style .
 */
public class Flip3dTransformer extends ThreeDimView implements ZBanner.ZBannerPageTransformer {

    @Override
    public void transformPage(Component page, float position) {
        final float width = page.getWidth();
        if (position >= 0 && position <= 1) {
            page.setPivotY(0);
            rotateY(0);
            page.setPivotX(0);

            page.setPivotY(page.getHeight() * 0.5f);
            rotateY(90f * position);

        } else if (position < 0 && position >= -1) {
            page.setPivotX(0);
            page.setPivotY(0);
            rotateY(0);

            page.setPivotX(width);
            page.setPivotY(page.getHeight() * 0.5f);
            rotateY(90f * position);
        }

    }
}
