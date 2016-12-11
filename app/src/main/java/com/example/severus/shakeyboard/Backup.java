package com.example.severus.shakeyboard;

import android.gesture.Gesture;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by severus on 24/9/16.
 */
//replace Backup with SHAIME
public class Backup extends InputMethodService implements KeyboardView.OnKeyboardActionListener,GestureDetector.OnGestureListener {
    private KeyboardView kv;
    private Keyboard keyboard;
    private boolean capson=false;
    private String swipe;
    String sFileName="swipe_data.csv";
    File grfile;
    FileWriter writer;
    //private GestureDetectorCompat gDetect;
    private GestureDetector gDetect;
    SqlLite dBase=new SqlLite(this);
    @Override
    public View onCreateInputView() {
        kv=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        keyboard=new Keyboard(this,R.xml.qwerty);
        kv.setKeyboard(keyboard);
        Backup gd=new Backup();
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }
        gDetect=new GestureDetector(this,gd);
        try {
            grfile = new File(root, sFileName);
            writer = new FileWriter(grfile);
            writer.append("Time, Length, Pressure");
        }
        catch (IOException e){
            e.printStackTrace();
        }


        //gDetect.setOnDoubleTapListener(gd);
        kv.setOnTouchListener(new View.OnTouchListener(){
                                  public boolean onTouch(View v, MotionEvent event){
                                      System.out.println("Fired");
                                      if(gDetect.onTouchEvent(event))
                                      // System.out.println("touchdetected "+valueof(action));
                                      {return true;}
                                      else{
                                          return false;
                                      }

                                  }
                              }
        );
        kv.setOnKeyboardActionListener(this);

        swipe="";
        return kv;
    }

    @Override
    public boolean onDown(MotionEvent e) {

        printSamples(e);
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        double vel=Math.sqrt(velocityX*velocityX+velocityY*velocityY);
        printSamples(e1,e2);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        printSamples(e);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        printSamples(e1,e2);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        printSamples(e);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        printSamples(e);
        return false;
    }



   /* private void playClick(int keyCode){
        AudioManager am = (AudioManager)getSystemService(AUDIO_SERVICE);
        switch(keyCode){
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }*/

    //sample

    void printSamples(MotionEvent ev) {

        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        // setContentView(R.layout.activity_main);
        //TextView display = (TextView) findViewById(R.id.showMotion);
        double time=0,xcord=0,ycord=0,pathlength=0,xs=0,xe=0,ys=0,ye=0,pressure=0;
        String show = "";
        pressure=ev.getPressure();
        for (int h = 0; h < historySize; h++) {

            // System.out.printf("At time %d:", ev.getHistoricalEventTime(h));
            if(h!=0) {
                time +=(ev.getHistoricalEventTime(h)-ev.getHistoricalEventTime(h-1));
            }

            for (int p = 0; p < pointerCount; p++) {
                xcord+=ev.getHistoricalX(p, h);
                ycord+=ev.getHistoricalY(p, h);
                //   show += String.format("  pointer %d: (%f,%f)",
                //         ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
            }
            xcord/=pointerCount;
            ycord/=pointerCount;
            if (h==0)
            {
                xs=xcord;
                ys=ycord;
            }else{
                xe=xcord;
                ye=ycord;
            }

        }
        pathlength=Math.sqrt((xs-xe)*(xs-xe)+(ys-ye)*(ys-ye));
        time/=historySize;
        //show+=String.format("At time %d:", ev.getEventTime());
        show += String.format("Show %f,%f",time,pathlength);
        for (int p = 0; p < pointerCount; p++) {
            //show += String.format("  pointer %d: (%f,%f)",ev.getPointerId(p); ev.getX(p); ev.getY(p);
        }

        System.out.println(show);
    }

    void printSamples(MotionEvent ev, MotionEvent ev2) {

        final int historySize = ev.getHistorySize();
        final int pointerCount = ev.getPointerCount();
        // setContentView(R.layout.activity_main);
        //     TextView display = (TextView) findViewById(R.id.showMotion);
        String show = "";
        for (int h = 0; h < historySize; h++) {
            //System.out.printf("At time %d:", ev.getHistoricalEventTime(h));

            for (int p = 0; p < pointerCount; p++) {
                show += String.format("  pointer %d: (%f,%f)", ev.getPointerId(p), ev.getHistoricalX(p, h), ev.getHistoricalY(p, h));
            }
        }
        show += String.format("At time %d:", ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            show += String.format("  pointer %d: (%f,%f)",
                    ev.getPointerId(p), ev.getX(p), ev.getY(p));
        }
        show += "\n";
        for (int h = 0; h < historySize; h++) {
            System.out.printf("At time %d:", ev.getHistoricalEventTime(h));

            for (int p = 0; p < pointerCount; p++) {
                show += String.format("  pointer %d: (%f,%f)",
                        ev2.getPointerId(p), ev2.getHistoricalX(p, h), ev2.getHistoricalY(p, h));
            }
        }
        show += String.format("At time %d:", ev.getEventTime());
        for (int p = 0; p < pointerCount; p++) {
            show += String.format("  pointer %d: (%f,%f)",
                    ev2.getPointerId(p), ev2.getX(p), ev2.getY(p));
        }
        System.out.println(show);
//        display.setText(show);
    }

    //on keyboard listener
    @Override
    public void swipeUp() {
        Log.d(TAG,"UP");
    }

    @Override
    public void swipeRight() {
        Log.d(TAG,"Right");
    }

    @Override
    public void swipeLeft() {
        Log.d(TAG,"Left");
    }

    @Override
    public void swipeDown() {
        Log.d(TAG,"Down");
    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void onRelease(int primaryCode) {
        System.out.println(swipe);
        swipe="";
    }
    @Override
    public void onPress(int primaryCode) {

        swipe=(char)primaryCode+swipe;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String s=""+event.getUnicodeChar();
        Log.d("like",s);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection ic = getCurrentInputConnection();
        // playClick(primaryCode);
        char c=(char)primaryCode;
        String s="";
        s+=c;
        Log.d(TAG,s);
        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                capson = !capson;
                keyboard.setShifted(capson);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char)primaryCode;
                if(Character.isLetter(code) && capson){
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code),1);
        }
    }




}
