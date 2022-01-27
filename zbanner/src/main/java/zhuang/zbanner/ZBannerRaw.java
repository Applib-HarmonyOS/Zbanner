package zhuang.zbanner;

import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.app.Context;
import ohos.data.rdb.DataObserver;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.multimodalinput.event.TouchEvent;
import zhuang.zbanner.util.LogUtil;

import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by zhuang on 2017/11/23.
 */

class ZBannerRaw extends ComponentContainer implements ComponentContainer.ArrangeListener,
        Component.EstimateSizeListener, Component.TouchEventListener, Component.ScrolledListener {
    private static final int TYPE_ONE_PAGE = 1;//只有一页的情况
    private static final int TYPE_TWO_PAGE = 2;//只有两页的情况
    private static final int TYPE_OTHER_PAGE = 3;//有三页以上的情况

    private ViewDragHelper mDragger;
    private ViewDragHelperCallback mCallback;
    private final ArrayList<ItemInfo> mItems = new ArrayList<>();
    private ZBannerAdapter mAdapter;
    private int mCurPosition;//当前页位置
    private boolean mFirstLayout = true;
    private int mType;
    private int N;
    private Indicator mIndicator;
    private PagerObserver mObserver;

    private int mPageGap;//页面之间的间隔
    private float mWidthFactor;//页面宽度倍数
    private int mOffscreenPageLimit;//缓存页面

    private int mWidth;
    private int mPageWidth;

    int mCurPageLeft;//静止状态时，当前页的left位置
    int mNextPageLft;//静止状态时，右侧页面的left位置
    int mPrePageLeft;//静止状态时，左侧页面的left位置

    private ZBanner.ZBannerPageTransformer mPageTransformer;
    private static final int DRAW_ORDER_DEFAULT = 0;
    private static final int DRAW_ORDER_FORWARD = 1;
    private static final int DRAW_ORDER_REVERSE = 2;
    private int mDrawingOrder;
    private static final ViewPositionComparator sPositionComparator = new ViewPositionComparator();
    private ArrayList<Component> mDrawingOrderedChildren;

    private Timer mTimer;
    private long mRecentTouchTime;
    private boolean mAutoPlay;
    private int mAnimalDuration = 2000;//ms
    private int mDisplayDuration = 2000;//ms
    private int mDuration = mAnimalDuration + mDisplayDuration;//ms

    private ZBanner.OnPageChangeLister mOnPageChangeLister;

     ZBannerRaw(Context context, Builder builder) {
        super(context);
        mPageGap = builder.pageGap;
        mWidthFactor = builder.widthFactor;
        mOffscreenPageLimit = builder.offscreenPageLimit;
        initView();
    }

     ZBannerRaw(Context context) {
        super(context);
        initView();
    }

     ZBannerRaw(Context context, AttrSet attrs) {
        super(context, attrs);
        initView();
    }

     ZBannerRaw(Context context, AttrSet attrs, int defStyleAttr) {
        super(context, attrs, String.valueOf(defStyleAttr));
        initView();
    }

    private void initView() {
        mCallback = new ViewDragHelperCallback();
        mDragger = ViewDragHelper.create(this, 1.0f, mCallback);
        setArrangeListener(this);
        setEstimateSizeListener(this);
        setTouchEventListener(this);
        setScrolledListener(this);
    }

    public void setIndicator(Indicator mIndicator) {
        this.mIndicator = mIndicator;
        this.mIndicator.setCount(N);
    }

    public void setAdapter(ZBannerAdapter adapter) {
        mAdapter = adapter;
        if (mAdapter != null) {
            if (mObserver == null) {
                mObserver = new PagerObserver();
            }
            mAdapter.setViewPagerObserver(mObserver);
        }

        /**
         *         try {
         *             mAdapter.notifyDataSetChanged();
         *         }
         *         catch (Exception e)
         *         {
         *             LogUtil.error('error',e+"");
         *         }
         */
    }

    private void dataSetChanged() {
        for (int i = 0; i < mItems.size(); i++) {
            mAdapter.destroyItem(mItems.get(i).fragment);
        }
        mAdapter.finishUpdate();
        mItems.clear();
        mCurPosition = 0;
        mFirstLayout = true;
        N = mAdapter.getCount();
        if (mIndicator != null) {
            mIndicator.setCount(N);
        }

        switch (N) {
            case 1:
                mType = TYPE_ONE_PAGE;
                mWidthFactor = 1f;//只有一个页面时，mWidthFactor无效，恒为1f
                break;
            case 2:
                mType = TYPE_TWO_PAGE;
                mWidthFactor = 1f;//只有两个页面时，mWidthFactor无效,恒为1f
                break;
            default:
                mType = TYPE_OTHER_PAGE;
                break;
        }
        postLayout();
    }

    @Override
    public boolean onEstimateSize (int widthMeasureSpec, int heightMeasureSpec) {

        mWidth = getClientWidth();
        mPageWidth = (int) (mWidth * mWidthFactor);

        if (mFirstLayout) {
            mFirstLayout = false;
            mCurPageLeft = (int) (mWidth * (1 - mWidthFactor) / 2);
            mPrePageLeft = mCurPageLeft - mPageWidth - mPageGap;
            mNextPageLft = mCurPageLeft + mPageWidth + mPageGap;
            switch (mType) {
                case TYPE_ONE_PAGE:
                case TYPE_TWO_PAGE:
                    addItem();
                    break;
                case TYPE_OTHER_PAGE:
                    popuateItem();
                    break;
                default:
                    break;
            }

            if (mOnPageChangeLister != null) {
                mOnPageChangeLister.change(mCurPosition);
            }
        }

        int childWidthSize = mPageWidth;
        int childHeightSize = getHeight() - getPaddingTop() - getPaddingBottom();
        int size = getChildCount();

        for (int i = 0; i < size; ++i) {
            final Component child = getComponentAt(i);
            final int widthSpec = EstimateSpec.getSizeWithMode( childWidthSize, EstimateSpec.PRECISE);
            final int heightSpec = EstimateSpec.getSizeWithMode( childHeightSize, EstimateSpec.PRECISE);
            child.estimateSize(widthSpec, heightSpec);
        }

        return  true;
    }

    private int getClientWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    void popuateItem() {
        popuateItem(mCurPageLeft);
    }

    void popuateItem(int curViewLeft) {
        if (N <= 0) return;
        int curIndex = -1;
        ItemInfo curItem = null;
        for (curIndex = 0; curIndex < mItems.size(); curIndex++) {
            final ItemInfo ii = mItems.get(curIndex);
            if (ii.position == mCurPosition) {
                curItem = ii;
                break;
            }
        }

        //新增当前页
        if (curItem == null) {
            curItem = new ItemInfo();
            curItem.position = mCurPosition;
            curItem.prePosition = previPosi(curItem);
            curItem.nextPosition = nextPosi(curItem);
            curItem.fragment = Optional.of(mAdapter.instantiateItem(this, mCurPosition).get());
            curItem.left = curViewLeft;
            mItems.add(curIndex, curItem);
        } else {
            curItem.left = curViewLeft;
        }

        if (curItem != null) {
            int rightCount;//左侧总共有多少个页面
            int leftCount;//右侧总共有多少个页面
            //页面总数为双数
            if (N % 2 == 0) {
                rightCount = N / 2;
                leftCount = rightCount - 1;
            }
            //页面总数为单数
            else {
                rightCount = leftCount = (N - 1) / 2;
            }

            rightCount = Math.min(rightCount, mOffscreenPageLimit);
            leftCount = Math.min(leftCount, mOffscreenPageLimit);
            int leftNum = Math.max(leftCount, curIndex);
            int rightNum = Math.max(rightCount, mItems.size() - curIndex - 1);

            requesLayout(leftNum,rightNum,curIndex,curItem,rightCount,leftCount);
        }
        mAdapter.finishUpdate();
        sortChildDrawingOrder();
    }

    public  void requesLayout(int leftNum,int rightNum,int curIndex,ItemInfo curItem,int rightCount,int leftCount){
        boolean needRequestLayoutLeft;
        boolean needRequestLayoutRight;
        if (curIndex == 0) {
            needRequestLayoutRight = populateRight(curItem, curIndex, rightCount, rightNum);
            needRequestLayoutLeft = populateLeft(curItem, curIndex, leftCount, leftNum);
        } else {
            needRequestLayoutLeft = populateLeft(curItem, curIndex, leftCount, leftNum);
            needRequestLayoutRight = populateRight(curItem, curIndex, rightCount, rightNum);
        }
        if (needRequestLayoutLeft || needRequestLayoutRight) {
            postLayout();
        }

    }

    public int previPosi (ItemInfo curItem){
        return ( curItem.position == 0 ? N - 1 : curItem.position - 1 );
    }

    public int nextPosi(ItemInfo curItem){
        return (curItem.position == N - 1 ? 0 : curItem.position + 1 );
    }

    int itemIndex;
    ItemInfo ii;
    boolean needRequestLayout = false;
    int offset;

    private boolean populateRight(ItemInfo curItem, int curIndex, int rightCount, int rightNum) {

        itemIndex = curIndex + 1;
        ii = itemIndex < mItems.size() ? mItems.get(itemIndex) : null;

        for (int i = 1; i <= rightNum; i++) {
             offset = i * (mPageWidth + mPageGap);
            if (ii != null) {
                cognifunc(rightCount,curItem);
            // till
            }
            else {
                ItemInfo rightii = new ItemInfo();
                int position = mCurPosition + i;
                if (position >= N) {
                    position -= N;
                }
                rightii.position = position;
                rightii.prePosition =  previPosiryt(rightii);
                rightii.nextPosition = nextPosiryt(rightii);
                rightii.left = curItem.left + offset;
                rightii.fragment = mAdapter.instantiateItem(this, rightii.position);
                mItems.add(rightii);

            }
            rightCount--;
        }
        return needRequestLayout;
    }

    public void cognifunc(int rightCount,ItemInfo curItem ){
        if (rightCount <= 0) {
            mItems.remove(ii);
            if (N > 2 * mOffscreenPageLimit + 1) {
                mAdapter.destroyItem(ii.fragment);
            } else {
                needRequestLayout = true;
            }
        } else {
            itemIndex++;
            ii.left = curItem.left + offset;
        }
        ii = gettingSize(mItems,itemIndex);

    }

    public  ItemInfo gettingSize(ArrayList<ItemInfo> mItems, int itemIndex){
        return ( itemIndex < mItems.size() ? mItems.get(itemIndex) : null) ;
    }

    public int previPosiryt (ItemInfo rightii){
        return (rightii.position == 0 ? N - 1 : rightii.position - 1) ;
    }

    public int nextPosiryt(ItemInfo rightii){
        return (rightii.position == N - 1 ? 0 : rightii.position + 1);
    }

    private boolean populateLeft(ItemInfo curItem, int curIndex, int leftCount, int leftNum) {
        boolean needRequestLayout = false;
        int itemIndex = curIndex - 1;
        ItemInfo ii = itemIndex >= 0 ? mItems.get(itemIndex) : null;
        for (int i = 1; i <= leftNum; i++) {
            int offset = i * (mPageWidth + mPageGap);
            if (ii != null) {
                cognifunc2(leftCount,curItem);
                // till
            } else {
                ItemInfo leftii = new ItemInfo();
                int position = mCurPosition - i;
                if (position < 0) {
                    position += N;
                }
                leftii.position = position;
                leftii.prePosition = previPosiryt(leftii);
                leftii.nextPosition = nextPosilft(leftii);
                leftii.left = curItem.left - offset;
                leftii.fragment = mAdapter.instantiateItem(this, leftii.position);
                mItems.add(0, leftii);
            }
            leftCount--;
        }
        return needRequestLayout;
    }

    public void cognifunc2(int rightCount,ItemInfo curItem ){
        if (rightCount <= 0) {
            mItems.remove(ii);
            if (N > 2 * mOffscreenPageLimit + 1) {
                mAdapter.destroyItem(ii.fragment);
            } else {
                needRequestLayout = true;
            }
        } else {
            ii.left = curItem.left - offset;
        }
        itemIndex--;
        ii = gettingii(mItems,itemIndex);

    }

    public  ItemInfo gettingii(ArrayList<ItemInfo> mItems, int itemIndex){
        return ( itemIndex >= 0 ? mItems.get(itemIndex) : null) ;
    }

    public int previPosilft (ItemInfo leftii){
        return (leftii.position == 0 ? N - 1 : leftii.position - 1) ;
    }

    public int nextPosilft(ItemInfo leftii){
        return (leftii.position == N - 1 ? 0 : leftii.position + 1);
    }

    private void addItem() {
        int size = mItems.size();
        if (size == 0) {
            for (int i = 0; i < N; i++) {
                ItemInfo ii = new ItemInfo();
                ii.position = i;
                ii.prePosition = Math.abs(i - 1);
                ii.nextPosition = Math.abs(i - 1);
                ii.fragment = mAdapter.instantiateItem(this, ii.position);
                ii.left = i * (mWidth + mPageGap);
                mItems.add(ii);
            }
            mAdapter.finishUpdate();
            sortChildDrawingOrder();
        }
    }

    ItemInfo infoForChild(Component child) {
        for (int i = 0; i < mItems.size(); i++) {
            ItemInfo ii = mItems.get(i);
            if (mAdapter.isViewFromObject(child, ii.fragment.get())) {
                return ii;
            }
        }
        return null;
    }

    @Override
    public boolean onArrange(int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            Component childView = getComponentAt(i);
            ItemInfo ii = infoForChild(childView);
            childView.arrange(ii.left, 0,
                    ii.left + childView.getWidth(), childView.getHeight());
        }
        return true;
    }

    public boolean onInterceptTouchEvent(TouchEvent event) {
        return mDragger.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(Component component, TouchEvent touchEvent) {
        mDragger.processTouchEvent(touchEvent);
        final int action = touchEvent.getAction();
        switch (action) {
            case TouchEvent.PRIMARY_POINT_DOWN:
                mAutoPlay = false;
                mDragger.setAutoPlaying(false);
                break;
            case TouchEvent.POINT_MOVE:
                //防止与父组件滑动事件冲突
                ComponentParent parent = getComponentParent();
                if (parent != null) {
                    //TODO : API unavailbale
                    //parent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            case TouchEvent.PRIMARY_POINT_UP:
                mAutoPlay = true;
                mDragger.setAutoPlaying(true);
                mRecentTouchTime = System.currentTimeMillis();
                break;
            default:
                break;
        }
        return onInterceptTouchEvent(touchEvent);
    }


    private class ViewDragHelperCallback extends ViewDragHelper.Callback {
        Component anotherView;//只有两个页面时需要用到
        int anotherViewOldLeft;//只有两个页面时需要用到

        ItemInfo firstItem;
        Component firstView;
        ItemInfo lastItem;
        Component lastView;

        @Override
        public boolean tryCaptureView(Component child, int pointerId) {
            switch (mType) {
                case TYPE_ONE_PAGE:
                    return false;
                case TYPE_TWO_PAGE:
                    for (int i = 0; i < getChildCount(); i++) {
                        Component view = getComponentAt(i);
                        if (view != child) {
                            anotherView = view;
                            anotherViewOldLeft = view.getLeft();
                        }
                    }
                    return true;
                case TYPE_OTHER_PAGE:
                    setFirstAndLast();
                    return true;
                default:
                    return true;
            }
        }

        @Override
        public int getViewHorizontalDragRange(Component child) {
            return 1;
        }

        @Override
        public int clampViewPositionHorizontal(Component child, int left, int dx) {
            return left;
        }

// Check dis (layout at line 483 n 485)

        @Override
        public void onViewPositionChanged(Component changedView, int left, int top, int dx, int dy) {
            switch (mType) {
                case TYPE_TWO_PAGE:
                    if (left < 0) {
                        anotherView.arrange(changedView.getRight() + mPageGap, 0,changedView.getRight() + anotherView.getWidth() + mPageGap, anotherView.getBottom() + dy); //setLayoutConfig
                    } else {
                        anotherView.arrange(left - anotherView.getWidth() - mPageGap, 0,left - mPageGap, anotherView.getBottom());
                    }
//setComponentPosition
                    if (anotherViewOldLeft * anotherView.getLeft() < 0) {
                        anotherViewOldLeft = anotherView.getLeft();
                        ItemInfo ii = mItems.get(0);
                        mItems.remove(0);
                        mItems.add(ii);
                        sortChildDrawingOrder();
                    }

                    transform();;
                    break;
                case TYPE_OTHER_PAGE:
                    triggerPositionChange();
                    for (int i = 0; i < getChildCount(); i++) {
                        Component view = getComponentAt(i);
                        if (view != changedView) {
//                          TODO API unavailable
//                            ViewCompat.offsetLeftAndRight(view, dx);
                            view.setMarginLeft(dx);
                            view.setMarginRight(dx);
                        }
                        if (mPageTransformer != null) {
                            float position = (float) (view.getLeft() - mCurPageLeft) / mPageWidth;
                            mPageTransformer.transformPage(view, position);
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        public  void transform(){
            for (int i = 0; i < getChildCount(); i++) {
                Component view = getComponentAt(i);
                if (mPageTransformer != null) {
                    float position = (float) view.getLeft() / mPageWidth;
                    mPageTransformer.transformPage(view, position);
                }
            }

        }

        private void triggerPositionChange() {
            if (firstView.getRight() > mWidth / 2) {
                mCurPosition = firstItem.position;
                popuateItem(firstView.getLeft());
                setFirstAndLast();
            } else if (lastView.getLeft() < mWidth / 2) {
                mCurPosition = lastItem.position;
                popuateItem(lastView.getLeft());
                setFirstAndLast();
            }
        }

        private void setFirstAndLast() {
            firstItem = mItems.get(0);
            firstView = firstItem.fragment.get().getComponent();
            lastItem = mItems.get(mItems.size() - 1);
            lastView = lastItem.fragment.get().getComponent();
        }

        @Override
        public void onViewReleased(Component releasedChild, float xvel, float yvel) {
            ItemInfo ii = infoForChild(releasedChild);
            if (ii == null) return;
            int left = releasedChild.getLeft();
            if (left < mCurPageLeft - mPageWidth / 2) {
                smoothMove1(xvel);
            } else if (left > mCurPageLeft - mPageWidth / 2 && left < mCurPageLeft) {
                smoothMove2(xvel);

            } else if (left > mWidth / 2) {
                smoothMove3(xvel);
            } else if (left > mCurPageLeft && left < mWidth / 2) {
                if (xvel <= 0) {
                    smoothMoveBack(ii);
                } else {
                    smoothMoveRight(ii);
                }
            } else {
                smoothMoveBack(ii);
            }
            if (mIndicator != null) {
                mIndicator.setSelectPosition(mCurPosition);
            }
            if (mOnPageChangeLister != null) {
                mOnPageChangeLister.change(mCurPosition);
            }
            invalidate();
        }

        public  void smoothMove1(float xvel){
            if (xvel <= 0) {
                smoothMoveLeft(ii);
            } else {
                smoothMoveBack(ii);
            }
        }

        public  void smoothMove2(float xvel){
            if (xvel >= 0) {
                smoothMoveBack(ii);
            } else if (xvel < 0) {
                smoothMoveLeft(ii);
            }
        }

        public void smoothMove3(float xvel){
            if (xvel >= 0) {
                smoothMoveRight(ii);
            } else if (xvel < 0) {
                smoothMoveBack(ii);
            }
        }

        //页面向左移动一页
        void smoothMoveLeft(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mPrePageLeft, 0);
            mCurPosition = ii.nextPosition;
        }

        //页面向右移动一页
        void smoothMoveRight(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mNextPageLft, 0);
            mCurPosition = ii.prePosition;
        }

        //页面恢复到原来的位置
        void smoothMoveBack(ItemInfo ii) {
            mDragger.settleCapturedViewAt(mCurPageLeft, 0);
            mCurPosition = ii.position;
        }
    }

    //跳转到指定页面
    public void setCurrentItem(int position) {
        mCallback.setFirstAndLast();
        mCurPosition = position;
        Component view = getViewFromPosition(position);
        mDragger.smoothSlideViewTo(view, mCurPageLeft, 0);
//        ViewCompat.postInvalidateOnAnimation(ZBannerRaw.this);
        invalidate();
        if (mIndicator != null) {
            mIndicator.setSelectPosition(mCurPosition);
        }
        if (mOnPageChangeLister != null) {
            mOnPageChangeLister.change(position);
        }
    }

    /**
     * 获取指定位置的view
     *
     * @param position
     * @return
     */
    private Component getViewFromPosition(int position) {
        final int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).position == position) {
                return mItems.get(i).fragment.get().getComponent();
            }
        }
        return null;
    }

    @Override
    public void onContentScrolled(Component component, int i, int i1, int i2, int i3) {
        if (mDragger.continueSettling(true)) {
            invalidate();
//            ViewCompat.postInvalidateOnAnimation(ZBannerRaw.this);
        }
    }

    @Override
    public void scrolledStageUpdate(Component component, int newStage) {

        /**
         *  scrolledStageUpdate .
         */
    }


    public void setPageTransformer(boolean reverseDrawingOrder, ZBanner.ZBannerPageTransformer pageTransformer) {
        mDrawingOrder = reverseDrawingOrder ? DRAW_ORDER_REVERSE : DRAW_ORDER_FORWARD;

        //TODO: setChildrenDrawingOrderEnabled API unavailable
        // setChildrenDrawingOrderEnabled(true);

        this.mPageTransformer = pageTransformer;
    }



    @Override
    public void addComponent(Component child) {
        super.addComponent(child);
        //TODO: setLayer API unavaialble
        /*final int layerType = mPageTransformer == null ? Component.LAYER_TYPE_NONE : Component.LAYER_TYPE_HARDWARE;
        child.setLayerType(layerType, null);*/
    }

    /**
     * 得到view在mItems中的位置
     */
    private int viewPosition(Component child) {
        final int size = mItems.size();
        for (int i = 0; i < size; i++) {
            if (mItems.get(i).fragment.get().getComponent() == child) {
                return i;
            }
        }
        return 0;
    }

    private void sortChildDrawingOrder() {
        if (mDrawingOrder != DRAW_ORDER_DEFAULT) {
            if (mDrawingOrderedChildren == null) {
                mDrawingOrderedChildren = new ArrayList();
            } else {
                mDrawingOrderedChildren.clear();
            }

            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final Component child = getComponentAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutConfig();
                lp.childIndex = i;
                lp.position = viewPosition(child);
                mDrawingOrderedChildren.add(child);
            }
            Collections.sort(mDrawingOrderedChildren, sPositionComparator);
        }
    }
    //TODO: unavailable API
    /*@Override
    protected int getChildDrawingOrder(int childCount, int i) {
        final int index = mDrawingOrder == DRAW_ORDER_REVERSE ? childCount - 1 - i : i;
        final int result = ((LayoutParams) mDrawingOrderedChildren.get(index).getLayoutConfig()).childIndex;
        return result;
    }*/

    /*@Override
    protected ComponentContainer.LayoutConfig generateDefaultLayoutParams() {
        return new LayoutParams();
    }*/

    /*@Override
    public LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return generateDefaultLayoutParams();
    }*/

    /*@Override
    protected ComponentContainer.LayoutConfig createLayoutConfig(ComponentContainer.LayoutConfig p,AttrSet attrs) {
        return generateDefaultLayoutParams();
    }*/

    @Override
    public LayoutConfig createLayoutConfig(Context context, AttrSet attrSet) {
        return  new LayoutParams(getContext(), attrSet);
    }

    @Override
    public LayoutConfig verifyLayoutConfig(ComponentContainer.LayoutConfig p) {
        return super.verifyLayoutConfig(p);
    }

    /*@Override
    public ComponentContainer.LayoutConfig generateLayoutParams(AttrSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }*/

    public class PagerObserver implements DataObserver {
        PagerObserver() {
        }

        @Override
        public void onChange() {
            dataSetChanged();
        }
    }

    public static class Builder {
        int pageGap;
        float widthFactor;
        int offscreenPageLimit;

        public ZBannerRaw build(Context context) {
            ZBannerRaw zBannerRaw = new ZBannerRaw(context, this);
            return zBannerRaw;
        }

        public Builder pageGap(int pageGap) {
            this.pageGap = pageGap;
            return this;
        }

        public Builder widthFactor(float widthFactor) {
            this.widthFactor = widthFactor;
            return this;
        }

        public Builder offscreenPageLimit(int offscreenPageLimit) {
            this.offscreenPageLimit = offscreenPageLimit;
            return this;
        }
    }

    public static class LayoutParams extends ComponentContainer.LayoutConfig {
        public int gravity;
        int position;
        int childIndex;

        public LayoutParams() {
            super(MATCH_PARENT, MATCH_PARENT);
        }

        public LayoutParams(Context context, AttrSet attrs) {
            super(context, attrs);
            //final Theme a = context.obtainStyledAttributes(attrs, LAYOUT_ATTRS); //getTypedAttributes
            gravity = LayoutAlignment.TOP;
            //a.recycle();
        }
    }

    static class ViewPositionComparator implements Comparator<Component> {
        @Override
        public int compare(Component lhs, Component rhs) {
            final LayoutParams llp = (LayoutParams) lhs.getLayoutConfig();
            final LayoutParams rlp = (LayoutParams) rhs.getLayoutConfig();
            return llp.position - rlp.position;
        }
    }

    void star() {
        if (mTimer == null) {
            mDuration = mAnimalDuration + mDisplayDuration;
            mDragger.setAutoPlaying(true);
            mDragger.setDuration(mAnimalDuration);
            mAutoPlay = true;
            mTimer = new Timer();
            mTimer.schedule(new ScrollerTask(), mDisplayDuration, mDuration);
        }
    }

    void stop() {
        if (mTimer != null) {
            mDragger.setAutoPlaying(false);
            mTimer.cancel();
            mTimer = null;
        }
    }

    public void setAnimalDuration(int duration) {
        this.mAnimalDuration = duration;
    }

    public void setDisplayDuration(int duration) {
        this.mDisplayDuration = duration;
    }

    EventHandler handler = new EventHandler(EventRunner.getMainEventRunner()) {
        @Override
        protected void processEvent(InnerEvent event) {
            mCurPosition++;
            if (mCurPosition == N) {
                mCurPosition = 0;
            }
            setCurrentItem(mCurPosition);
        }
    };

    private class ScrollerTask extends TimerTask {

        public ScrollerTask() {
            /**
             *  ScrollerTask .
             */
        }

        public void run() {
            if (mAutoPlay && (System.currentTimeMillis() - mRecentTouchTime >= mDisplayDuration)) {
                handler.sendEvent(0);
            }
        }
    }

    public void setOnPageChangeLister(ZBanner.OnPageChangeLister mOnPageChangeLister) {
        this.mOnPageChangeLister = mOnPageChangeLister;
    }

}
