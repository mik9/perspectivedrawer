package ua.pl.mik.perspectivedrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * A compatibility layer for {@link ua.pl.mik.perspectivedrawer.PerspectiveDrawer} that allow to easily replace
 * Google's DrawerLayout.
 */
public class DrawerLayout extends PerspectiveDrawer {

    /**
     * Stub/no-op implementations of all methods of {@link DrawerListener}.
     * Override this if you only care about a few of the available callback methods.
     */
    public abstract static class SimpleDrawerListener implements DrawerListener {
        @Override
        public void onDrawerStateChanged(int newState) {
        }

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
        }

        @Override
        public void onDrawerOpened(View drawerView) {
        }

        @Override
        public void onDrawerClosed(View drawerView) {
        }
    }

    public DrawerLayout(Context context) {
        super(context);
    }

    public DrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Check if the given drawer view is currently in an open state.
     * To be considered "open" the drawer must have settled into its fully
     * visible state. To check for partial visibility use
     * {@link #isDrawerVisible(android.view.View)}.
     *
     * @param drawer Drawer view to check
     * @return true if the given drawer view is in an open state
     * @see #isDrawerVisible(android.view.View)
     */
    public boolean isDrawerOpen(View drawer) {
        return getOpenDegree() == getOpenedAngle() ;
    }

    /**
     * Check if the given drawer view is currently in an open state.
     * To be considered "open" the drawer must have settled into its fully
     * visible state. If there is no drawer with the given gravity this method
     * will return false.
     *
     * @param drawerGravity Gravity of the drawer to check
     * @return true if the given drawer view is in an open state
     */
    public boolean isDrawerOpen(int drawerGravity) {
        return getOpenDegree() == getOpenedAngle();
    }

    /**
     * Check if a given drawer view is currently visible on-screen. The drawer
     * may be only peeking onto the screen, fully extended, or anywhere inbetween.
     *
     * @param drawer Drawer view to check
     * @return true if the given drawer is visible on-screen
     * @see #isDrawerOpen(android.view.View)
     */
    public boolean isDrawerVisible(View drawer) {
        return isOpened();
    }

    /**
     * Check if a given drawer view is currently visible on-screen. The drawer
     * may be only peeking onto the screen, fully extended, or anywhere in between.
     * If there is no drawer with the given gravity this method will return false.
     *
     * @param drawerGravity Gravity of the drawer to check
     * @return true if the given drawer is visible on-screen
     */
    public boolean isDrawerVisible(int drawerGravity) {
        return isOpened();
    }

    /**
     * Close the specified drawer view by animating it into view.
     *
     * @param drawerView Drawer view to close
     */
    public void closeDrawer(View drawerView) {
        close();
    }

    /**
     * Close the specified drawer by animating it out of view.
     *
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     */
    public void closeDrawer(int gravity) {
        close();
    }

    /**
     * Open the specified drawer view by animating it into view.
     *
     * @param drawerView Drawer view to open
     */
    public void openDrawer(View drawerView) {
        open();
    }

    /**
     * Open the specified drawer by animating it out of view.
     *
     * @param gravity Gravity.LEFT to move the left drawer or Gravity.RIGHT for the right.
     *                GravityCompat.START or GravityCompat.END may also be used.
     */
    public void openDrawer(int gravity) {
        open();
    }

    /**
     * Set a listener to be notified of drawer events.
     *
     * @param listener Listener to notify when drawer events occur
     * @see DrawerListener
     */
    public void setDrawerListener(DrawerListener listener) {
        super.setDrawerListener(listener);
    }

    public static interface DrawerListener extends PerspectiveDrawer.DrawerListener{
        public void onDrawerStateChanged(int newState);
    }
}
