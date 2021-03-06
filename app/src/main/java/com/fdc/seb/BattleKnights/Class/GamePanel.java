package com.fdc.seb.BattleKnights.Class;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.fdc.seb.BattleKnights.Entities.Background;
import com.fdc.seb.BattleKnights.Entities.Player;
import com.fdc.seb.BattleKnights.R;

/**
 * Created by Sebos on 30/09/2015.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    public static final int MOVESPEED = 0;
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    private MainThread thread;
    private String TAG = "GamePanel/";
    private Background bg;
    private Player player;

    private boolean playing;

    public GamePanel(Context context) {
        super(context);

        //add the callback to the surface holder to intercept events
        getHolder().addCallback(this);

        //Implement touch listener
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        //Make gamepanel focusable to handle evnts
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        SCREEN_WIDTH = getWidth();
        SCREEN_HEIGHT = getHeight();

        //Instanciate game objects;
        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.grassbg1));
        //Create player bitmap w/ frames, size, separator etc.
        player = new Player(Player.TChar.ELISA);

        //Start the game loop

        playing = true;
        Log.i(TAG, "Surface Created");
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        Log.i(TAG, "Surface destroyed");
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
                retry = false;
                Log.i(TAG, "Thread stopped");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private float x, y;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float tx = event.getX(),
                ty = event.getY();
        int pointerIndex = event.getActionIndex();

        int pointerId = event.getPointerId(pointerIndex);

        int action = (event.getAction() & MotionEvent.ACTION_MASK);

        int pointCnt = event.getPointerCount();

        for (int i = 0; i < pointCnt; i++) {
            int id = event.getPointerId(i);
//                    x_last[id] = x[id];
//                    y_last[id] = y[id];
//                    isTouch_last[id] = isTouch[id];
//                    x[id] = event.getX(i);
//                    y[id] = event.getY(i);
        }

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                Log.i(TAG, "TOUCH_START");
                x = tx;
                y = ty;
                player.setSide(true);

                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "TOUCH_END");
                player.setRight(false);
                player.setLeft(false);
                player.setUp(false);
                player.setDown(false);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                if (tx > x && tx - x > 10) {
                    Log.i(TAG, "TOUCH_RIGHT");
                    player.setRight(true);
                } else if (tx - x < -10) {
                    Log.i(TAG, "TOUCH_LEFT");
                    player.setLeft(true);
                }

                if (ty > y && (ty - y > 10)) {
                    Log.i(TAG, "TOUCH_DOWN");
                    player.setDown(true);
                } else if (ty - y < -10) {
                    Log.i(TAG, "TOUCH_UP");
                    player.setUp(true);
                    //player.setUp(true);
                }
                break;
        }

//        else if (action == MotionEvent.ACTION_LEFT)
//        return super.onTouchEvent(event);
        return true;
    }

    public void update() {
        if (playing) {
            bg.update();
            player.update();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        final float scaleX = getWidth() / bg.WIDTH;
        final float scaleY = getHeight() / bg.HEIGHT;

        if (canvas != null) {
            final int savedState = canvas.save();
            canvas.scale(scaleX, scaleY);
            bg.draw(canvas);
            player.draw(canvas);

            canvas.drawCircle(x, y , 5, new Paint());
            drawFPS(canvas);

            canvas.restoreToCount(savedState);
        }
    }

    private void drawFPS(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(11F);
        canvas.drawText("FPS : " + thread.getAvgFPS(), 20, 20, paint);
    }
}
