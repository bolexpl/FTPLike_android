package com.example.bolek.ftplclient.lib

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.GestureDetector

class RecyclerItemClickListener(context: Context,
                                recyclerView: RecyclerView,
                                listener: OnItemClickListener)
    : RecyclerView.OnItemTouchListener {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
    }

    private var mListener: OnItemClickListener = listener
    private var mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            val childView = recyclerView.findChildViewUnder(e.x, e.y)

            mListener.onItemLongClick(childView!!, recyclerView.getChildLayoutPosition(childView))
        }
    })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
    }

    var flag = false
    var f2 = false

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)

        if (childView != null) {

            if (mGestureDetector.onTouchEvent(e)) {
                Log.d("DEBUG", "click")
                mListener.onItemClick(
                        childView,
                        rv.getChildLayoutPosition(childView)
                )
            }
        }
        return false
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}