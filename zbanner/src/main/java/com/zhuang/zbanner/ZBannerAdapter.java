package com.zhuang.zbanner;


import ohos.aafwk.ability.fraction.Fraction;
import ohos.aafwk.ability.fraction.FractionManager;
import ohos.aafwk.ability.fraction.FractionScheduler;
import ohos.agp.components.Component;
import ohos.agp.components.ComponentContainer;
import ohos.data.rdb.DataObservable;
import ohos.data.rdb.DataObserver;

import java.util.Optional;

/**
 * Created by zhuang on 2017/11/23.
 */

public abstract class ZBannerAdapter {

    private final FractionManager mFractionManager;
    private FractionScheduler mCurTransaction = null;
    private DataObserver mDataObserver;
    private final DataObservable mObservable = new DataObservable();

    public ZBannerAdapter(FractionManager fm) {
        mFractionManager = fm;
    }

    public abstract Fraction getItem(int position);

    public abstract int getCount();

    public Optional<Fraction> instantiateItem(ComponentContainer container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFractionManager.startFractionScheduler();
        }

        final long itemId = getItemId(position);
        // Do we already have this fraction?
        String name = makeFractionName(container.getId(), itemId);
        Optional<Fraction> fraction = mFractionManager.getFractionByTag(name);
        if (fraction.isPresent()) {
            mCurTransaction.show(fraction.get());  // check once
        } else {
            fraction = Optional.ofNullable(getItem(position));
            mCurTransaction.add(container.getId(), fraction.get(),
                    makeFractionName(container.getId(), itemId));
        }
        return fraction;
    }

    public boolean isViewFromObject(Component view, Fraction fraction) {
        return fraction.getComponent() == view;
    }

    public void destroyItem(Optional<Fraction> fraction) {
        if (mCurTransaction == null) {
            mCurTransaction = mFractionManager.startFractionScheduler();
        }
        mCurTransaction.remove(fraction.get());
    }

    public long getItemId(int position) {
        return position;
    }

    private String makeFractionName(int ComponentId, long id) {
        return "harmony:switcher:" + ComponentId + ":" + id;
    }

    public void finishUpdate() {
        if (mCurTransaction != null) {
            mCurTransaction.submit();
            mCurTransaction = null;
        }
    }

    public void notifyDataSetChanged() {
        synchronized (this) {
            if (mDataObserver != null) {
                mDataObserver.onChange();
            }
        }
        mObservable.notifyObservers();
    }

    void setViewPagerObserver(DataObserver observer) {
        synchronized (this) {
            mDataObserver = observer ;
        }
    }

}
