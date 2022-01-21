# ZBanner
ZBanner是一个真正的轮播控件，并非ViewPager的简单改造。ZBanner使用Adapter+Fragment的方式来展示页面，每一页就是一个Fragment,因此你可以随意设计自己的Fragment,例如形状、加载图片的方式等等。同时提供了转换动画、自定义布局等等让你实现各种炫丽效果。

## 预览效果
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/1.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/2.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/3.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/4.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/5.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/6.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/7.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/8.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/9.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/10.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/11.gif)
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/12.gif)

## 例子下载
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/13.png)

## 简单使用

ResourceTable.temp.xml
  ``` 
<?xml version="1.0" encoding="utf-8"?>
<com.zhuang.zbanner.ZBanner
    xmlns:ohos="http://schemas.huawei.com/res/ohos"
    ohos:id="$+id:zBanner"
    ohos:width="match_parent"
    ohos:height="200fp" />
  ```
java
  ```
package com.example.zbanner;

import com.example.zbanner.slice.BannerFraction;
import com.zhuang.zbanner.ZBanner;

import com.zhuang.zbanner.ZBannerAdapter;
import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.ability.fraction.FractionManager;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.Color;
import ohos.utils.PacMap;

public class temp extends FractionAbility {

    ZBanner zBanner;


    @Override
    protected void onStart(Intent savedInstanceState) {
        super.onStart(savedInstanceState);
        super.setUIContent(ResourceTable.Layout_temp);
        zBanner = (ZBanner) findComponentById(ResourceTable.Id_zBanner);
        zBanner.setAdapter(new MyBannerAdapter(getFractionManager()));
    }

    @Override
    protected void onActive() {
        super.onActive();
        //每个页面展示时间为1000ms  页面切换持续时间为2000ms
        zBanner.star(1000,2000);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        //停止自动切换
        zBanner.stop();
    }

    private class MyBannerAdapter extends ZBannerAdapter {

        public MyBannerAdapter(FractionManager fm) {
            super(fm);
        }

        @Override
        public Fraction getItem(int position) {
            return BannerFraction.newInstance(position);
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    public class BannerFraction extends Fraction {

        private int resId;
        private int position;

        public BannerFraction() {
        }

        public static com.example.zbanner.slice.BannerFraction newInstance(int resId, int position) {
            com.example.zbanner.slice.BannerFraction fragment = new com.example.zbanner.slice.BannerFraction();
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
                    Util.createToast(com.example.zbanner.slice.BannerFraction.this,
                            "click:position=" + position).show();
                }
            });
            return rootView;
        }


    }

}

  ```
  
  
## 转换动画
```
zBanner.setPageTransformer(new Flip3DTransformer());
```
ZBanner提供了多种转换动画效果：
- AccordionTransformer
- AccordionTransformer1
- DepthPageTransformer 
- DrawerTransformer 
- Flip3DTransformer 
- FlipHorizontalTransformer 
- RotateDownTransformer 
- StackTransformer 
- ZoomOutTransformer  

ZBanner允许用户自定义Transformer，只需实现接口ZBannerPageTransformer，例如AccordionTransformer的实现如下：




```



import com.zhuang.zbanner.ZBanner;
import ohos.agp.components.Component;

public class AccordionTransformer implements ZBanner.ZBannerPageTransformer {


    /**
     * @param page     在切换的页面
     * @param position 正在切换的页面相对于当前显示在正中间的页面的位置
     *                 0表示当前页，1表示右侧一页，-1表示左侧一页。
     *                 注意，这几个都是临界值，position在页面切换过程中会一直改变
     *                 例如左移的话，当前页的position会从0减少到-1，切换完成后就变成左侧一页了。
     */
    
    @Override
    public void transformPage(Component component, float position) {
        final float width = component.getWidth();
        if (position >= 0 && position <= 1) {
            component.setTranslationX(-width * position);
            component.setPivotX(width);
            component.setScaleX(1f - position);
        } else if (position < 0 && position >= -1) {
            component.setTranslationX(0);
            component.setPivotX(0);
            component.setScaleX(1f);
        }
    }
}

```
用一张图来说明transformPage的position参数

![](https://github.com/likeadog/Zbanner/blob/master/screenshot/page_transformer1.png)

如有不清楚的可以查阅ViewPager的PageTransformer,因为ZBanner提供的PageTransformer接口与ViewPager的方式是一致的。

## 可以在Xml文件中设置的属性
| 属性        | 描述           | 示例  |
| ------------- |:-------------:| -----:|
| widthFactor      | 设置ZBanner中页面宽度倍数，例如设置为0.8，则每个页面都占用ZBanner的0.8倍 |zbanner:widthFactor=".8"|
| pageGap      | 页面间隔 |zbanner:pageGap="10dp" |
| offscreenPageLimit | 左右两侧分别可缓存的页面数 | zbanner:offscreenPageLimit="2"|
| indicatorSelectIcon    |可自定义指示器被选中时的图标|zbanner:indicatorSelectIcon="@drawable/ic_indicator_line_select"|
| indicatorUnSelectIcon    |可自定义指示器未被选中时的图标|zbanner:indicatorUnSelectIcon="@drawable/ic_indicator_line_unselect"|
| indicatorGravity    |指示器的位置|zbanner:indicatorGravity="bottomRight"|
| indicatorIconSize    |指示器的大小|zbanner:indicatorIconSize="10dp"|
| showIndicator    |是否显示指示器|zbanner:showIndicator="false"|
|indicatorMargin|指示器的margin|zbanner:indicatorMargin="10dp"|
|indicatorGap|指示器中各个图标的间隔|zbanner:indicatorGap="3dp"|

## 自定义布局
如果你对ZBanner默认的布局不满意，例如indicator的位置不满意，ZBanner的样式不满意，可以自定义布局。例如下面的效果  
![](https://github.com/likeadog/Zbanner/blob/master/screenshot/12.gif)  
该界面把Zbanner与indicator分开，首先需要设置默认的indicator为false，让ZBanner不显示自身的indicator,再把你自己的indicator按你想要的位置来布局到界面中。
```<?xml version="1.0" encoding="utf-8"?>
<DependentLayout
    xmlns:custom="http://schemas.huawei.com/res/custom"
    xmlns:ohos="http://schemas.huawei.com/res/ohos"
    ohos:width="match_parent"
    ohos:height="200fp">

    <com.zhuang.zbanner.ZBanner
        ohos:id="$+id:zBanner"
        ohos:width="match_parent"
        ohos:height="match_parent"
        custom:indicatorIconSize="8dp"
        custom:indicatorMargin="2dp"
        custom:showIndicator="false"
        custom:layout_constraintLeft_toLeftOf="parent"
        custom:layout_constraintRight_toRightOf="parent"
        custom:layout_constraintTop_toTopOf="parent"
        custom:offscreenPageLimit="2" />

    <Text
        ohos:id="$+id:title"
        ohos:width="match_parent"
        ohos:height="match_content"
        ohos:background_element="#66000000"
        ohos:padding="10fp"
        ohos:text_color="#ffffff"
        custom:layout_constraintBottom_toBottomOf="@+id/zBanner" />

    <com.zhuang.zbanner.Indicator
        ohos:right_margin="10fp"
        ohos:id="$+id:indicator"
        ohos:width="match_content"
        ohos:height="match_content"
        custom:layout_constraintBottom_toBottomOf="@+id/title"
        custom:layout_constraintRight_toRightOf="@id/title"
        custom:layout_constraintTop_toTopOf="@+id/title" />

</DependentLayout>
```
然后在java代码中设置
```
            Indicator indicator = findComponentById(ResourceTable.id_indicator);
            zBanner.setIndicator(indicator);
            final Text title = findComponentById(ResourceTable.id_title);
            zBanner.setOnPageChangeLister(new ZBanner.OnPageChangeLister() {
                @Override
                public void change(int position) {
                    title.setText(position + "" );
                }
            });
```
更多详细请看项目中的例子源码

## 点击事件

从上面的介绍可以看到ZBanner的每个页面其实就是一个Fragment，如需为页面设置点击事件，只需在对应的Fragment中设置点击事件即可
```
 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_banner1, container, false);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        return rootView;
    }
```

## License
```
Copyright 2018 likeadog

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
