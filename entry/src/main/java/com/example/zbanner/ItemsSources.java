package com.example.zbanner;


import java.util.ArrayList;
import java.util.List;

import  com.example.zbanner.*;
import com.zhuang.zbanner.PageTransformer.*;
import com.zhuang.zbanner.ZBanner;

/**
 * Created by zhuang on 2018/1/5.
 */

public class ItemsSources {

    public final static String TYPE_CLASSIC = "classic";
    public final static String TYPE_WIDTH_FACTOR = "widthFactor";
    public final static String TYPE_INDICATOR = "indicator";

    public static List<ExampleItem> getItems() {
        List<ExampleItem> list = new ArrayList();
        list.add(new ExampleItem("Classic", null, ResourceTable.Layout_ability_banner_classic));
        list.add(new ExampleItem("WidthFactor", null, ResourceTable.Layout_ability_banner_widthfactor));
        list.add(new ExampleItem("Indicator", null, ResourceTable.Layout_ability_banner_indicator));
        list.add(new ExampleItem("AccordionTransformer", (ZBanner.ZBannerPageTransformer) new AccordionTransformer()));
        list.add(new ExampleItem("AccordionTransformer1", (ZBanner.ZBannerPageTransformer) new AccordionTransformer1()));
        list.add(new ExampleItem("DepthPageTransformer", (ZBanner.ZBannerPageTransformer) new DepthPageTransformer()));
        list.add(new ExampleItem("DrawerTransformer", (ZBanner.ZBannerPageTransformer) new DrawerTransformer()));
        list.add(new ExampleItem("Flip3DTransformer", (ZBanner.ZBannerPageTransformer) new Flip3DTransformer()));
        list.add(new ExampleItem("FlipHorizontalTransformer", (ZBanner.ZBannerPageTransformer) new FlipHorizontalTransformer()));
        list.add(new ExampleItem("RotateDownTransformer", (ZBanner.ZBannerPageTransformer) new RotateDownTransformer()));
        list.add(new ExampleItem("StackTransformer", (ZBanner.ZBannerPageTransformer) new StackTransformer()));
        list.add(new ExampleItem("ZoomOutTransformer", (ZBanner.ZBannerPageTransformer) new ZoomOutTransformer()));
        list.add(new ExampleItem("Customer", null,ResourceTable.Layout_ability_banner_customer));
        return list;
    }

    static class ExampleItem {
        String title;
        ZBanner.ZBannerPageTransformer transformer;
        int layoutId;

        public ExampleItem(String title, ZBanner.ZBannerPageTransformer transformer, int layoutId) {
            this.title = title;
            this.transformer = transformer;
            this.layoutId = layoutId;
        }

        public ExampleItem(String title, ZBanner.ZBannerPageTransformer transformer) {
            this.title = title;
            this.transformer = transformer;
            this.layoutId = ResourceTable.Layout_ability_banner_transformer;
        }
    }

}
