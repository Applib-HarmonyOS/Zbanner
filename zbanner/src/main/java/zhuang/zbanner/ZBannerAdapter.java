package zhuang.zbanner;


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

    private final FractionManager mFragmentManager;
    private FractionScheduler mCurTransaction = null;
    private DataObserver mViewPagerObserver;
    private final DataObservable mObservable = new DataObservable();

    protected ZBannerAdapter(FractionManager fm) {
        mFragmentManager = fm;
    }

    public abstract Fraction getItem(int position);

    public abstract int getCount();

    public Optional<Fraction> instantiateItem(ComponentContainer container, int position) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.startFractionScheduler();
        }

        final long itemId = getItemId(position);
        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), itemId);
        Optional<Fraction> fragment = mFragmentManager.getFractionByTag(name);
        if (fragment.isPresent()) {
            mCurTransaction.show(fragment.get());  // check once
        } else {
            fragment = Optional.ofNullable(getItem(position));
            mCurTransaction.add(container.getId(), fragment.get(),
                    makeFragmentName(container.getId(), itemId));
        }
        return fragment;
    }

    public boolean isViewFromObject(Component view, Fraction fragment) {
        return fragment.getComponent() == view;
    }

    public void destroyItem(Optional<Fraction> fragment) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.startFractionScheduler();
        }
        mCurTransaction.remove(fragment.get());
    }

    public long getItemId(int position) {
        return position;
    }

    private String makeFragmentName(int viewId, long id) {
        return "harmony:switcher:" + viewId + ":" + id;
    }

    public void finishUpdate() {
        if (mCurTransaction != null) {
            mCurTransaction.submit();
            mCurTransaction = null;
        }
    }

    public void notifyDataSetChanged() {
        synchronized (this) {
            if (mViewPagerObserver != null) {
                mViewPagerObserver.onChange();
            }
        }
        mObservable.notifyObservers();
    }

    void setViewPagerObserver(DataObserver observer) {
        synchronized (this) {
            mViewPagerObserver = observer ;
        }
    }

}
