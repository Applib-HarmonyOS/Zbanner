package com.example.zbanner;

import com.example.zbanner.slice.BannerAbilitySlice;
import com.example.zbanner.slice.BannerFragment;
import com.example.zbanner.slice.BannerFragmentRound;
import com.zhuang.zbanner.Indicator;
import com.zhuang.zbanner.ZBanner;
import com.zhuang.zbanner.ZBannerAdapter;
import com.zhuang.zbanner.util.LogUtil;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.ability.fraction.FractionManager;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;

import java.util.List;

public class BannerAbility extends FractionAbility {


    int[] imgResId = {
            ResourceTable.Media_jackson,
            ResourceTable.Media_jordan,
            ResourceTable.Media_kobe,
            ResourceTable.Media_stephen,
            ResourceTable.Media_android
    };
    String[] name = {
            "jackson",
            "jordan",
            "kobe",
            "stephen",
            "android",
    };
    ZBanner zBanner;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);

        int position = intent.getIntParam("position", 0);
        List<ItemsSources.ExampleItem> list = ItemsSources.getItems();
        int layoutId = list.get(position).layoutId;

        LogUtil.info("check2",""+position);

        super.setUIContent(layoutId);

        zBanner = (ZBanner) findComponentById(ResourceTable.Id_zBanner);
        if (layoutId == ResourceTable.Layout_ability_banner_widthfactor) {
            zBanner.setAdapter(new MyBannerAdapter1(getFractionManager()));
        } else {
            zBanner.setAdapter(new MyBannerAdapter(getFractionManager()));
        }
        ZBanner.ZBannerPageTransformer transformer = list.get(position).transformer;

        if (transformer != null) {
            zBanner.setPageTransformer(transformer);
        }

        if (findComponentById(ResourceTable.Id_indicator) != null) {
            Indicator indicator = (Indicator) findComponentById(ResourceTable.Id_indicator);
            zBanner.setIndicator(indicator);
        }

        if (findComponentById(ResourceTable.Id_title) != null) {
            final Text title = (Text) findComponentById(ResourceTable.Id_title);
            zBanner.setOnPageChangeLister(new ZBanner.OnPageChangeLister() {
                @Override
                public void change(int position) {
                    title.setText(position + ".  " + name[position]);
                }
            });
        }

    }

    @Override
    protected void onForeground(Intent intent) {
        super.onForeground(intent);
        zBanner.star();
    }

    @Override
    protected void onBackground() {
        super.onBackground();
        zBanner.stop();
    }

    private class MyBannerAdapter extends ZBannerAdapter {

        public MyBannerAdapter(FractionManager fm) {
            super(fm);
        }

        @Override
        public Fraction getItem(int position) {
            return BannerFragment.newInstance(imgResId[position], position);
        }

        @Override
        public int getCount() {
            return imgResId.length;
        }
    }

    private class MyBannerAdapter1 extends ZBannerAdapter {

        public MyBannerAdapter1(FractionManager fm) {
            super(fm);
        }

        @Override
        public Fraction getItem(int position) {
            return BannerFragmentRound.newInstance(imgResId[position], position);
        }

        @Override
        public int getCount() {
            return imgResId.length;
        }
    }
}
