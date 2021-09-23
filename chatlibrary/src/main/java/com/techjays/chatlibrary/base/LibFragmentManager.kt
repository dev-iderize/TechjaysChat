package com.techjays.chatlibrary.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.transition.Slide
import com.techjays.chatlibrary.R

class LibFragmentManager
/**
 * Constructor to Initiate fragment manager
 *
 * @param aContext FragmentActivity
 */
    (private val mContext: FragmentActivity) {


    val backstackCount: Int
        get() {
            val aFragmentManager = mContext.supportFragmentManager
            return aFragmentManager.backStackEntryCount
        }


    /**
     * Update the Current Fragment by passing the below parameters
     *
     * @param aFragment Fragment
     * @param tag       String
     * @param aBundle   Bundle
     */
    fun addContent(aFragment: Fragment, tag: String, aBundle: Bundle?) {
        try {
            addContent(aFragment, tag, aBundle, null, null)
        } catch (e: StackOverflowError) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Update the Current Fragment by passing the below parameters with animation
     *
     * @param aFragment Fragment
     * @param tag       String
     * @param aBundle   Bundle
     * @param entry Enter Transition
     * @param exit Exit Transition
     */
    fun addContent(aFragment: Fragment, tag: String, aBundle: Bundle?, entry: Int?, exit: Int?) {
        try {

            Log.e("TAG Screen name", tag)

            // Initialise Fragment Manager
            val aFragmentManager = mContext.supportFragmentManager

            // Initialise Fragment Transaction
            val aTransaction = aFragmentManager.beginTransaction()

            if (aBundle != null) {
                aFragment.arguments = aBundle
            }

            if (entry != null) {
                aFragment.enterTransition = Slide(entry).apply { duration = 300 }
                aFragment.exitTransition = Slide(exit!!).apply { duration = 300 }
            }
            // Add the selected fragment

            // add the tag to the backstack
            aTransaction.addToBackStack(tag)

            // Commit the Fragment transaction
            aTransaction.commit()

            // aTransaction.commitAllowingStateLoss();

            myLastTag = tag

        } catch (e: StackOverflowError) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }



    @SuppressLint("CommitTransaction")
    fun refreshContent(aFragment: Fragment) {
        try {
            // Initialise Fragment Manager
            val aFragmentManager = mContext.supportFragmentManager

            // Initialise Fragment Transaction
            val aTransaction = aFragmentManager.beginTransaction()

            aTransaction.detach(aFragment).attach(aFragment)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Clear All Fragments
     */
    fun clearAllFragments() {
        try {
            val aFragmentManager = mContext.supportFragmentManager
            for (i in 0 until aFragmentManager.backStackEntryCount) {
                aFragmentManager.popBackStack()
            }
        } catch (e: StackOverflowError) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun oneStepBack() {
        val fts = mContext.supportFragmentManager.beginTransaction()
        val fragmentManager = mContext.supportFragmentManager
        if (fragmentManager.backStackEntryCount >= 2) {
            fragmentManager.popBackStackImmediate()
            fts.commit()
        }
    }


    fun removeFragment(aCount: Int) {
        val aFragmentManager = mContext.supportFragmentManager
        for (i in 0 until aCount) {
            aFragmentManager.popBackStack()
        }

    }


    companion object {

        /**
         * Last fragment tag
         */
        private var myLastTag = ""
    }
}
