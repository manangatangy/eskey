package com.wolfie.eskey.view.component.Settings;

import android.util.Log;

import java.util.HashSet;

/**
 * Class to manage a collection of ItemLayouts.
 * The GroupSetting knows which ItemLayout is currently showing (if any) and it knows
 * how to request the current one to close.
 */

public class GroupSetting {

    // Only one item may be open at a time.  Opening another will close the current open item.
    private ItemLayout mCurrentlyOpen = null;        // Null if all closed.

    /**
     * Returns true if the item is allowed to show.
     * This may cause the currently showing item to close (f it agrees).
     */
    public boolean requestToHideCurrent() {
        Log.d("eskey", "GroupSetting.requestToHideCurrent()");
        // Can only return true if the currently open item agrees to close.
        if (mCurrentlyOpen == null) {
            Log.d("eskey", "GroupSetting.requestToHideCurrent() nothing currently showing ==> YES");
            return true;
        }
        Log.d("eskey", "GroupSetting.requestToHideCurrent() requesting current '" + mCurrentlyOpen.getHeadingText() + "' to hide");
        if (!mCurrentlyOpen.onHide()) {
            Log.d("eskey", "GroupSetting.requestToHideCurrent() current '" + mCurrentlyOpen.getHeadingText() + "' said NO");
            return false;
        }
        Log.d("eskey", "GroupSetting.requestToHideCurrent() current '" + mCurrentlyOpen.getHeadingText() + "' said YES, now hiding ...");
        mCurrentlyOpen.hide();      // Will call back to notifyCurrentShowingItemIsHiding()
        return true;
    }

    public void notifyItemIsHiding() {
        Log.d("eskey", "GroupSetting.notifyItemIsHiding() '" + mCurrentlyOpen.getHeadingText() + "' notifies now hidden");
        mCurrentlyOpen = null;
    }

    public void notifyItemIsShowing(ItemLayout itemLayout) {
        mCurrentlyOpen = itemLayout;
        Log.d("eskey", "GroupSetting.notifyItemIsShowing() '" + mCurrentlyOpen.getHeadingText() + "' notifies now showing");
    }

}
