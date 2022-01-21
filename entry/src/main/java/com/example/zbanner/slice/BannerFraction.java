package com.example.zbanner.slice;

import com.example.zbanner.ResourceTable;
import com.example.zbanner.Util;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.agp.components.Image;
import ohos.agp.components.LayoutScatter;

public class BannerFraction extends Fraction {

    private int resId;
    private int position;

    public BannerFraction() {
    }

    public static BannerFraction newInstance(int resId, int position) {
        BannerFraction fragment = new BannerFraction();
        fragment.setArguments(resId, position);
        return fragment;
    }

    private void setArguments(int resId, int position) {
        this.resId = resId;
        this.position = position;
    }

    @Override
    protected Component onComponentAttached(LayoutScatter scatter, ComponentContainer container, Intent intent) {
        Component rootView = scatter.parse(ResourceTable.Layout_fraction_banner, container, false);
        Image imageView = (Image) rootView.findComponentById(ResourceTable.Id_imageView);

        imageView.setPixelMap(resId);
        rootView.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Util.createToast(BannerFraction.this, "click:position=" + position).show();
            }
        });
        return rootView;
    }


}
