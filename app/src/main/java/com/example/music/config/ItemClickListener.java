package com.example.music.config;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

/**
 * 点击事件
 * Created by DevWiki on 2016/7/16.
 */

public class ItemClickListener extends RecyclerView.SimpleOnItemTouchListener {


    private GestureDetectorCompat gestureDetector;
    private View childView;
    private RecyclerView touchView;

    public interface OnItemClickListener {

        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public ItemClickListener(Context context,
                             OnItemClickListener listener) {
        gestureDetector = new GestureDetectorCompat(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {

                        if (childView != null && listener != null) {
                            listener.onItemClick(childView, touchView.getChildPosition(childView));
                        }
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {

                        if (childView != null && listener != null) {
                            listener.onItemLongClick(childView,
                                    touchView.getChildPosition(childView));
                        }
                    }
                });
    }

    @Override
    public boolean onInterceptTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        childView = rv.findChildViewUnder(e.getX(), e.getY());
        touchView = rv;
        return false;
    }

    @Override
    public void onTouchEvent(@NotNull RecyclerView rv, @NotNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}