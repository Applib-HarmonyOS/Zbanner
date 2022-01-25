package com.example.zbanner;

import com.zhuang.zbanner.PageTransformer.AccordionTransformer;
import com.zhuang.zbanner.PageTransformer.AccordionTransformer1;
import com.zhuang.zbanner.PageTransformer.DepthPageTransformer;
import com.zhuang.zbanner.PageTransformer.DrawerTransformer;
import com.zhuang.zbanner.PageTransformer.Flip3dTransformer;
import com.zhuang.zbanner.PageTransformer.FlipHorizontalTransformer;
import com.zhuang.zbanner.PageTransformer.RotateDownTransformer;
import com.zhuang.zbanner.PageTransformer.StackTransformer;
import com.zhuang.zbanner.PageTransformer.ZoomOutTransformer;
import com.zhuang.zbanner.ZBanner;
import java.util.ArrayList;
import java.util.List;



/**
 * Created by zhuang on 2018/1/5.
 */

public final class ItemsSources {
    public static final List<ExampleItem> list = new ArrayList<>();

    private ItemsSources() {}

    public  static final String TYPE_CLASSIC = "classic";
    public static final String TYPE_WIDTH_FACTOR = "widthFactor";
    public static final String TYPE_INDICATOR = "indicator";

    /**
     *  getting the list .
     *
     *  @return  .
     *
     */
    public static List<ExampleItem> getItems() {

        list.add(new ExampleItem("Classic", null, ResourceTable.Layout_ability_banner_classic));
        list.add(new ExampleItem("WidthFactor", null, ResourceTable.Layout_ability_banner_widthfactor));
        list.add(new ExampleItem("Indicator", null, ResourceTable.Layout_ability_banner_indicator));
        list.add(new ExampleItem("AccordionTransformer", (ZBanner.ZBannerPageTransformer) new AccordionTransformer()));
        list.add(new ExampleItem(
                "AccordionTransformer1",
                (ZBanner.ZBannerPageTransformer) new AccordionTransformer1()
        ));
        list.add(new ExampleItem("DepthPageTransformer", (ZBanner.ZBannerPageTransformer) new DepthPageTransformer()));
        list.add(new ExampleItem("DrawerTransformer", (ZBanner.ZBannerPageTransformer) new DrawerTransformer()));
        list.add(new ExampleItem("Flip3DTransformer", (ZBanner.ZBannerPageTransformer) new Flip3dTransformer()));
        list.add(new ExampleItem(
                "FlipHorizontalTransformer",
                (ZBanner.ZBannerPageTransformer) new FlipHorizontalTransformer()
        ));
        list.add(new ExampleItem(
                "RotateDownTransformer",
                (ZBanner.ZBannerPageTransformer) new RotateDownTransformer()
        ));
        list.add(new ExampleItem("StackTransformer", (ZBanner.ZBannerPageTransformer) new StackTransformer()));
        list.add(new ExampleItem("ZoomOutTransformer", (ZBanner.ZBannerPageTransformer) new ZoomOutTransformer()));
        list.add(new ExampleItem("Customer", null, ResourceTable.Layout_ability_banner_customer));
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
