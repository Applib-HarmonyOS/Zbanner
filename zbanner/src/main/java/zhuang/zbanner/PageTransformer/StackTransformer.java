package zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import zhuang.zbanner.ZBanner;
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
