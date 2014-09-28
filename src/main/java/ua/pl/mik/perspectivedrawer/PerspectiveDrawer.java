package ua.pl.mik.perspectivedrawer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import com.nineoldandroids.view.ViewHelper;

public class PerspectiveDrawer extends FrameLayout {
    private static final float MIN_VELOCITY = 140f;
    private static final float MINIMUM_SCALE_X = 0.55f;
    private static final float MINIMUM_SCALE_Y = 0.6f;
    private static final boolean HONEY_CAPABLE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

    private final int mVelocityUnits;
    private final int mTouchSlop;
    private final int mLongPressTimeout;

    private PageHolder mMenuHolder;
    private PageHolder mPageHolder;
    private View mTouchTarget = null;

    private boolean mOpened = false;
    private boolean mAnimating = false;
    private boolean mOpening = false;
    private boolean mTracking = false;
    private MotionEvent mDownEvent;

    private float mCurDegree = 0;
    private int mPageShift;
    private int mMenuShift;
    private boolean mDimmingEnabled = true;

    private static final int CLOSED_ANGLE = 0;
    private int mOpenedAngle = -20;
    private int mAnimationDuration = 300;
    private VelocityTracker mVelocityTracker;

    private int mLeftSwipeArea = 50; //dp
    private Rect mPagePaddings = new Rect();
    private float mRightLimit;
    private float mScaledWidth;
    private float mTranslateDistance;

    private DrawerListener mListener;

    public PerspectiveDrawer(Context context) {
        this(context, null);
    }

    public PerspectiveDrawer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PerspectiveDrawer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final float density = getResources().getDisplayMetrics().density;
        final int velocityUnits = 1000;
        mVelocityUnits = (int) (velocityUnits * density);
        mLeftSwipeArea *= density;

        mTouchSlop = ViewConfiguration.get(context).getScaledPagingTouchSlop();
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        if (!isInEditMode()) {
            mPageShift = getResources().getDimensionPixelSize(R.dimen.animated_drawer_page_shift);
            mMenuShift = getResources().getDimensionPixelSize(R.dimen.animated_drawer_menu_shift);
            mAnimationDuration = getResources().getInteger(R.integer.animated_drawer_animation_duration);
            mOpenedAngle = -Math.abs(getResources().getInteger(R.integer.animated_drawer_angle));
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildCount() != 2) {
            throw new InflateException("AnimatedDrawer should have 2 childs");
        }

        try {
            mMenuHolder = (PageHolder) getChildAt(0);
            mPageHolder = (PageHolder) getChildAt(1);
        } catch (ClassCastException e) {
            throw new InflateException("Childs should be PageHolder");
        }

        if (mPageHolder.getBackground() != null) {
            mPageHolder.getBackground().getPadding(mPagePaddings);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec) + mPagePaddings.left
                        + mPagePaddings.right,
                MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) + mPagePaddings.top
                        + mPagePaddings.bottom,
                MeasureSpec.EXACTLY);

        mPageHolder.measure(widthSpec, heightSpec);
    }

    private void calculateScaledSizes() {
        Matrix matrix = new Matrix();
        matrix.reset();
        Matrix matrix3d = new Matrix();
        Camera camera = new Camera();

        if (HONEY_CAPABLE) {
            matrix.preScale(MINIMUM_SCALE_X, MINIMUM_SCALE_Y, mPageHolder.getMeasuredWidth() / 2,
                    mPageHolder.getMeasuredHeight() / 2);
            camera.rotateY(mOpenedAngle);
            camera.getMatrix(matrix3d);
        }
        matrix3d.preTranslate(- mPageHolder.getMeasuredWidth() / 2, - mPageHolder.getMeasuredHeight() / 2);
        matrix3d.postTranslate(mPageHolder.getMeasuredWidth() / 2,
                mPageHolder.getMeasuredHeight() / 2);
        if (HONEY_CAPABLE) {
            matrix.postConcat(matrix3d);
        }

        float[] pst = new float[]{0, 0};
        matrix.mapPoints(pst);
        final float left = pst[0];
        mScaledWidth = (mPageHolder.getMeasuredWidth() / 2 - left) * 2;

        mTranslateDistance = getMeasuredWidth() - (getMeasuredWidth() - mScaledWidth) / 2 - mPageShift;
        mRightLimit = getMeasuredWidth() - mPageShift;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mMenuHolder.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mPageHolder.layout(-mPagePaddings.left, -mPagePaddings.top, getMeasuredWidth() + mPagePaddings.right,
                getMeasuredHeight() + mPagePaddings.bottom);

        setOpenDegree(mCurDegree);

        calculateScaledSizes();
    }

    /**
     * Change drawer state: open it if it is closed and vise versa
     */
    public void toggle() {
        if (mOpened) {
            close();
        } else {
            open();
        }
    }

    /**
     * Open drawer. Slide page to right.
     */
    public void open() {
        SimpleAnimator a = new SimpleAnimator(this, mOpenedAngle);
        a.setDuration(mAnimationDuration);
        startAnimation(a);
    }

    /**
     * Close drawer. Slide page to front.
     */
    public void close() {
        SimpleAnimator a = new SimpleAnimator(this, CLOSED_ANGLE);
        a.setDuration(mAnimationDuration);
        startAnimation(a);
    }

    protected void fling(float velocity) {
        SimpleAnimator a = new SimpleAnimator(this, velocity > 0 ? mOpenedAngle : CLOSED_ANGLE);
        final float rel;
        if (velocity > 0) {
            rel = (mOpenedAngle - mCurDegree) / mOpenedAngle;
        } else {
            rel = mCurDegree / mOpenedAngle;
        }
        int duration = (int) (rel * mAnimationDuration);
        a.setDuration(duration);
        a.setInterpolator(Interpolators.Quad.EASE_OUT);
        startAnimation(a);
    }

    /**
     * Returns current open degree.
     *
     * @return float between [{@link #CLOSED_ANGLE}, {@link #mOpenedAngle}]
     */
    public float getOpenDegree() {
        return mCurDegree;
    }

    /**
     * Normalize input degree and change current state to it.
     *
     * @param degree degree to set.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setOpenDegree(float degree) {
        // Limit degree to [CLOSED_ANGLE, mOpenedAngle]
        if (degree < mOpenedAngle) {
            degree = mOpenedAngle;
        } else if (degree > CLOSED_ANGLE) {
            degree = CLOSED_ANGLE;
        }

        final Interpolator interpolator;
        if (HONEY_CAPABLE) {
            interpolator = Interpolators.Sine.EASE_IN_OUT;
        } else {
            interpolator = Interpolators.Linear.EASE_NONE;
        }

        float rel = interpolator.getInterpolation(degree / mOpenedAngle);

        if (mListener != null) {
            mListener.onDrawerSlide(this, rel);
        }

        final float shift = rel * mTranslateDistance;
        mOpened = degree != 0;
        mMenuHolder.setVisibility(mOpened ? VISIBLE : INVISIBLE);
        ViewHelper.setTranslationX(mPageHolder, shift);
        if (HONEY_CAPABLE) {
            boolean enableAcceleration = (mCurDegree == CLOSED_ANGLE) && (degree != CLOSED_ANGLE);
            boolean disableAcceleration = (mCurDegree != CLOSED_ANGLE) && (degree == CLOSED_ANGLE);
            final float degreeInternal = interpolator.getInterpolation(degree / mOpenedAngle) * mOpenedAngle;
            if (enableAcceleration) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mPageHolder.setLayerType(LAYER_TYPE_HARDWARE, null);
                        mMenuHolder.setLayerType(LAYER_TYPE_HARDWARE, null);
                    }
                });
                if (mListener != null) {
                    mListener.onDrawerOpened(this);
                }
            } else if (disableAcceleration) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        mPageHolder.setLayerType(LAYER_TYPE_NONE, null);
                        mMenuHolder.setLayerType(LAYER_TYPE_NONE, null);
                    }
                });
                if (mListener != null) {
                    mListener.onDrawerClosed(this);
                }
            }
            mPageHolder.setRotationY(degreeInternal);
        }

        float menuShift = (1 - rel) * mMenuShift;
        ViewHelper.setX(mMenuHolder, -menuShift);
        ViewHelper.setAlpha(mMenuHolder, rel);

        if (mDimmingEnabled) {
            final int dim = (int) (rel * 160);
            mPageHolder.setForegroundColor(mOpened ? (dim & 0xff) << 24 : 0);
        }

        if (HONEY_CAPABLE) {
            final float scaleX = MINIMUM_SCALE_X + (1f - MINIMUM_SCALE_X) * (1f - rel);
            mPageHolder.setScaleX(scaleX);
            final float scaleY = MINIMUM_SCALE_Y + (1f - MINIMUM_SCALE_Y) * (1f - rel);
            mPageHolder.setScaleY(scaleY);
        }

        mCurDegree = degree;
    }

    private void dispatchTransformedTouchEvent(View target, MotionEvent event) {
        event.offsetLocation(-target.getLeft(), -target.getTop());
        target.dispatchTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            return true;
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mOpened) {
                    mTouchTarget = mMenuHolder;
                } else {
                    mTouchTarget = mPageHolder;
                }
                // We storing copy of MotionEvent because views in hierarchy may change it
                mDownEvent = MotionEvent.obtain(event);
                dispatchTransformedTouchEvent(mTouchTarget, event);
                mTracking = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mAnimating) {
                    // User moved finger far enough by Y axis: no need to catch swipe gesture anymore
                    if (Math.abs(event.getY() - mDownEvent.getY()) > mTouchSlop * 3) {
                        mTracking = false;
                    }
                    // We searching for open event and user moved finger far enough by X axis
                    if (mTracking && Math.abs(event.getX() - mDownEvent.getX()) > mTouchSlop) {
                        if (!mOpened && mDownEvent.getX() < mLeftSwipeArea) {
                            // Panel is closed and caught left edge
                            mOpening = true;
                        } else if (mOpened && mDownEvent.getX() > mRightLimit) {
                            // Panel is opened and caught right edge
                            mOpening = false;
                        } else {
                            // Nothing was caught: just pass event to view hierarchy
                            dispatchTransformedTouchEvent(mTouchTarget, event);
                            break;
                        }

                        // If something was caught we passing CANCEL event to view hierarchy
                        mAnimating = true;
                        event.setAction(MotionEvent.ACTION_CANCEL);
                    }
                    dispatchTransformedTouchEvent(mTouchTarget, event);
                } else {
                    /**
                     * Calculate new angle:
                     * - relative to 0 in case we are opening page
                     * - relative to right limit if we are closing page
                     */
                    float pos;
                    if (mOpening) {
                        pos = (event.getX() - mDownEvent.getX() - mTouchSlop) / mRightLimit * mOpenedAngle;
                    } else {
                        pos = (mRightLimit - mDownEvent.getX() + event.getX() + mTouchSlop) / mRightLimit * mOpenedAngle;
                    }
                    setOpenDegree(pos);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.computeCurrentVelocity(mVelocityUnits);
                float vel = mVelocityTracker.getXVelocity();
                if (mAnimating) {
                    // We need to make decision: open or close page
                    mAnimating = false;
                    if (mCurDegree != mOpenedAngle && mCurDegree != CLOSED_ANGLE) {
                        if (vel > MIN_VELOCITY) {
                            fling(1);
                        } else if (vel < -MIN_VELOCITY) {
                            fling(-1);
                        } else if (mCurDegree < mOpenedAngle / 2) {
                            fling(1);
                        } else if (mCurDegree >= mOpenedAngle / 2) {
                            fling(-1);
                        }
                    }
                } else if (Math.abs(mDownEvent.getX() - event.getX()) < mTouchSlop
                        && Math.abs(mDownEvent.getY() - event.getY()) < mTouchSlop
                        && event.getDownTime() - event.getEventTime() < mLongPressTimeout
                        && mOpened && event.getX() > mRightLimit) {
                    // Page is opened and user tapped on it
                    close();
                } else {
                    // No action: pass event to view hierarchy
                    dispatchTransformedTouchEvent(mTouchTarget, event);
                }
                // Clean up
                mTouchTarget = null;
                mDownEvent.recycle();
                mDownEvent = null;
                break;
            default:
                if (mTouchTarget != null) {
                    dispatchTransformedTouchEvent(mTouchTarget, event);
                }
                break;
        }

        // We always consume all touch events
        return true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mVelocityTracker = VelocityTracker.obtain();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Enable or disable dimming while opening page. Enabled by default.
     *
     * @param enabled false to disable dimming
     */
    public void setDimmingEnabled(boolean enabled) {
        mDimmingEnabled = enabled;
        if (enabled) {
            setOpenDegree(mCurDegree);
        } else {
            mPageHolder.setForegroundColor(0);
        }
    }

    /**
     * Returns current state of dimming.
     *
     * @return true if dimming is enabled.
     */
    public boolean isDimmingEnabled() {
        return mDimmingEnabled;
    }

    /**
     * Set the layout resource id as current page.
     *
     * @param resId layout to set.
     */
    public void setPage(int resId) {
        setPage(inflate(getContext(), resId, null));
    }

    /**
     * Set the layout resource id as menu page.
     *
     * @param resId layout to set.
     */
    public void setMenu(int resId) {
        setMenu(inflate(getContext(), resId, null));
    }

    /**
     * Set the view as current page.
     *
     * @param page view to set.
     */
    public void setPage(View page) {
        setViewInHolder(page, mPageHolder);
    }

    /**
     * Set the view as menu page.
     *
     * @param menu view to set.
     */
    public void setMenu(View menu) {
        setViewInHolder(menu, mMenuHolder);
    }

    private void setViewInHolder(View view, PageHolder holder) {
        holder.removeAllViews();
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holder.addView(view, lp);
    }

    /**
     * Get holder view for menu page.
     *
     * @return {@link PageHolder} for menu page.
     */
    @SuppressWarnings("unused")
    public PageHolder getMenuHolder() {
        return mMenuHolder;
    }

    /**
     * Get holder view for main page.
     *
     * @return {@link PageHolder} for main page.
     */
    @SuppressWarnings("unused")
    public PageHolder getPageHolder() {
        return mPageHolder;
    }

    /**
     * Get open/close animation duration.
     *
     * @return animation duration.
     */
    @SuppressWarnings("unused")
    public int getAnimationDuration() {
        return mAnimationDuration;
    }

    /**
     * Set open/close animation duration.
     *
     * @param animationDuration new animation duration.
     */
    @SuppressWarnings("unused")
    public void setAnimationDuration(int animationDuration) {
        this.mAnimationDuration = animationDuration;
    }

    /**
     * Get page angle in opened state.
     *
     * @return page angle.
     */
    @SuppressWarnings("unused")
    public int getOpenedAngle() {
        return mOpenedAngle;
    }

    /**
     * Set angle in opened state.
     *
     * @param openedAngle new opened angle.
     */
    @SuppressWarnings("unused")
    public void setOpenedAngle(int openedAngle) {
        this.mOpenedAngle = openedAngle;
    }

    /**
     * Set {@link PerspectiveDrawer.DrawerListener} to receive callbacks about drawer
     * change state.
     *
     * @param listener The  listener object.
     */
    @SuppressWarnings("unused")
    public void setListener(DrawerListener listener) {
        this.mListener = listener;
    }

    /**
     * Get current state.
     *
     * @return true if page currently opened.
     */
    @SuppressWarnings("unused")
    public boolean isOpened() {
        return mOpened;
    }

    /**
     * Get parallax value for menu shifting.
     *
     * @return menu shift size.
     */
    @SuppressWarnings("unused")
    public int getMenuShift() {
        return mMenuShift;
    }

    /**
     * Set parallax value for menu shifting.
     *
     * @param menuShift new menu shifting value.
     */
    @SuppressWarnings("unused")
    public void setMenuShift(int menuShift) {
        this.mMenuShift = menuShift;
    }

    private static class SimpleAnimator extends Animation {
        private PerspectiveDrawer drawer;
        private float to;
        private float diff;

        SimpleAnimator(PerspectiveDrawer drawer, float to) {
            this.drawer = drawer;
            this.to = to;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            diff = to - drawer.getOpenDegree();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float curr = to - diff * (1f - interpolatedTime);
            drawer.setOpenDegree(curr);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.currentDegree = mCurDegree;
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            SavedState ss = (SavedState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            mCurDegree = ss.currentDegree;
            requestLayout();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private static class SavedState extends BaseSavedState {
        public float currentDegree;

        public SavedState(Parcel source) {
            super(source);
            currentDegree = source.readFloat();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeFloat(currentDegree);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * Listener for monitoring events about drawers.
     */
    public interface DrawerListener {
        /**
         * Called when a drawer's position changes.
         * @param drawerView The child view that was moved
         * @param slideOffset The new offset of this drawer within its range, from 0-1
         */
        public void onDrawerSlide(View drawerView, float slideOffset);

        /**
         * Called when a drawer has settled in a completely open state.
         * The drawer is interactive at this point.
         *
         * @param drawerView Drawer view that is now open
         */
        public void onDrawerOpened(View drawerView);

        /**
         * Called when a drawer has settled in a completely closed state.
         *
         * @param drawerView Drawer view that is now closed
         */
        public void onDrawerClosed(View drawerView);
    }
}
