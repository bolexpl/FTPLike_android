package com.example.bolek.ftplclient.lib

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.GestureDetector
import android.view.ViewGroup
import com.example.bolek.ftplclient.R

class RecyclerItemClickListener(context: Context,
                                recyclerView: RecyclerView,
                                listener: OnItemClickListener)
    : RecyclerView.OnItemTouchListener {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
        fun onItemLongClick(view: View, position: Int)
        fun onSpecialButtonClick(view: View, position: Int)
    }

    private var mListener: OnItemClickListener = listener
    private var mGestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean = true

        override fun onDoubleTap(e: MotionEvent?): Boolean = true

        override fun onLongPress(e: MotionEvent) {
            val childView = recyclerView.findChildViewUnder(e.x, e.y)

            mListener.onItemLongClick(childView!!, recyclerView.getChildLayoutPosition(childView))
        }
    })

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (mGestureDetector.onTouchEvent(e)) {

            val view = rv.findChildViewUnder(e.x, e.y)

            view?.let {
                val listPosition = rv.getChildAdapterPosition(view)
                val specialChildView: View? = findExactChild(
                        view,
                        e.rawX,
                        e.rawY,
                        (5 * rv.resources.displayMetrics.density).toInt()
                )

                if (listPosition != RecyclerView.NO_POSITION) {
                    if (specialChildView != null) {
                        mListener.onSpecialButtonClick(specialChildView, listPosition)
                    } else {
                        mListener.onItemClick(view, rv.getChildLayoutPosition(view))
                    }
                    return true
                }
            }
        }

        return false
    }

    private fun findExactChild(view: View?, x: Float, y: Float,
                               specialViewClickPadding: Int): View? {

        if (view == null || view !is ViewGroup) {
            return view
        }

        val specialView: View? = view.findViewById(R.id.popupButton)
        specialView?.let {
            val viewBounds = Rect()
            specialView.getGlobalVisibleRect(viewBounds)
            if (x >= viewBounds.left - specialViewClickPadding &&
                    x <= viewBounds.right + specialViewClickPadding &&
                    y >= viewBounds.top - specialViewClickPadding &&
                    y <= viewBounds.bottom + specialViewClickPadding) {
                return specialView
            }
        }
        return null
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
    }
}