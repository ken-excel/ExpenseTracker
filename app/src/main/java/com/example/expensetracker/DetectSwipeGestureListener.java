package com.example.expensetracker;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;

    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;

    // Source activity that display message in text view.
    private HistoryActivity activity = null;

    public HistoryActivity getActivity() {
        return activity;
    }

    public void setActivity(HistoryActivity activity) {
        this.activity = activity;
    }

    /* This method is invoked when a swipe gesture happened. */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        // Get swipe delta value in x axis.
        float deltaX = e1.getX() - e2.getX();

        // Get swipe delta value in y axis.
        float deltaY = e1.getY() - e2.getY();

        // Get absolute value.
        float deltaXAbs = Math.abs(deltaX);
        float deltaYAbs = Math.abs(deltaY);

        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
        if((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X))
        {
            if(deltaX < 0)
            {
                Toast.makeText(this.activity, "RIGHT SWIPE", Toast.LENGTH_SHORT).show();
                if(this.activity.month == 1){
                    this.activity.year -= 1;
                    this.activity.month = 12;
                    this.activity.update();
                }
                else{
                    this.activity.month -= 1;
                    this.activity.update();
                }
            }else
            {
                Toast.makeText(this.activity, "LEFT SWIPE", Toast.LENGTH_SHORT).show();
                if(this.activity.month != this.activity.this_month || this.activity.year != this.activity.this_year){
                    if(this.activity.month == 12){
                        this.activity.year += 1;
                        this.activity.month = 1;
                        this.activity.update();
                    }
                    else{
                        this.activity.month += 1;
                        this.activity.update();
                    }
                }
            }
        }

        if((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y))
        {
            if(deltaY > 0)
            {
                //this.activity.displayMessage("Swipe to up");
            }else
            {
                //this.activity.displayMessage("Swipe to down");
            }
        }


        return true;
    }
}