package zhuang.zbanner.PageTransformer;

import ohos.agp.components.Component;
import zhuang.zbanner.ZBanner;
/**
 * Created by zhuang on 2017/12/19.
 */

public class DepthPageTransformer implements ZBanner.ZBannerPageTransformer {
    private static final float MIN_SCALE = 0.75f;

    /** TransformPage .
     *
     * @param component of transformPage
     * @param position 正在切换的页面相对于当前显示在正中间的页面的位置 .
     *                 0表示当前页，1表示右侧一页，-1表示左侧一页 。
     *                 注意，这几个都是临界值，position在页面切换过程中会一直改变 .
     */
    public void transformPage(Component component, float position) {
        int pageWidth = component.getWidth();

        if (position < -1) { // [-Infinity,-1)
            component.setAlpha(0);
        } else if (position >= -1 && position <= 0) { // [-1,0]
            component.setAlpha(1);
            component.setTranslationX(0);
            component.setScaleX(1);
            component.setScaleY(1);
        } else if (position <= 1) { // (0,1]
            component.setAlpha(1 - position);
            component.setTranslationX(pageWidth * -position);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            component.setScaleX(scaleFactor);
            component.setScaleY(scaleFactor);
        } else { // (1,+Infinity]
            component.setAlpha(0);
        }
    }
}
