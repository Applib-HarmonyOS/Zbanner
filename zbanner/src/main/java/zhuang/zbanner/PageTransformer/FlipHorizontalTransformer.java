package zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import ohos.agp.render.ThreeDimView;
import zhuang.zbanner.ZBanner;

/**
 * Created by zhuang on 2017/12/20.
 */

public class FlipHorizontalTransformer extends ThreeDimView implements ZBanner.ZBannerPageTransformer {
    @Override
    public void transformPage(Component view, float position) {
        if (position >= -1 && position <= 1) {
            float rotation = 180f * position;
            view.setTranslationX(view.getWidth() * -position);
            view.setAlpha(rotation > 90f || rotation < -90f ? 0 : 1);
            view.setPivotX(view.getWidth() * 0.5f);
            view.setPivotY(view.getHeight() * 0.5f);
            rotateY(rotation);
        }

    }
}
