package com.example.severus.shakeyboard;

import android.gesture.Gesture;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Environment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;


public class SHAIME extends InputMethodService implements KeyboardView.OnKeyboardActionListener{
    private KeyboardView kv;
    private Keyboard keyboard;
    private boolean capson=false;
    private String swipe="",test="",swipe2="";
    private List<Keyboard.Key> keyList;
    private double pressure,duration,velocity,start,end;
    private VelocityTracker mvel=null;
    private float old_x=0,old_y=0,olddir_x=0,olddir_y=0;
    private Save_feature sf;
    public static DataDBHelper featuredb;
    private GetSwipeWord swipeWord;
    double x_vel=0,y_vel=0;
    int n_event=1,np_event=1;
    char frequent_char=0;
    public void retrieveKeys() {
        keyList = kv.getKeyboard().getKeys();
    }
    //given motion event find which key character is at that particular coordinate
    public char getkeylabel(MotionEvent event){
        char c=0;
        String s="";

        // For each key in the key list
        for (Keyboard.Key k : keyList) {
            // If the coordinates from the Motion event are inside of the key
            if (k.isInside((int) event.getX(), (int) event.getY())) {
                // k is the key pressed
                Log.d("Debugging",
                        "Key pressed: key=" + k.label+ " X=" + k.x + " - Y=" + k.y);
                int centreX, centreY;
                centreX = (k.width/2) + k.x;
                centreY = (k.width/2) + k.x;
                s=k.label.toString();
                if(s.equals("SPACE")||s.equals("CAPS")||s.equals("DEL")) {
                    Log.d("Debugging","hereeeeee");
                    //swipe="";
                    c = 0;
                }
                else
                c=k.label.charAt(0);
                // These values are relative to the Keyboard View
                // Log.d("Debugging",
                //       "Centre of the key pressed: X="+centreX+" - Y="+centreY);
            }
        }

        return c;
    }
//funtion to retrieve key if direction has changed
    public void check_change(MotionEvent event){

        float new_x=event.getX(),new_y=event.getY();
       // if(((new_x-old_x)>0 && olddir_x<0)||((new_x-old_x)<0 && olddir_x>0)){
            swipe+=getkeylabel(event);
        //}

     //   if(((new_y-old_y)>0 && olddir_y<0)||((new_y-old_y)<0 && olddir_y>0)){
          //  swipe+=getkeylabel(event);
        //}
    }
    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        retrieveKeys();
    }

    //get unique characters in string

    public String get_final_string(String s){
        String ans="",s2="";
        int freq[]=new int[256];
        int count=1,count1=1,count2=1,count3=1,len=s.length(),avg=0,iter=0;
        char c1=0,c2=0,c3=0;
      for(int i=0;i<s.length();i++){
          if(s.charAt(i)>=97&&s.charAt(i)<=122)
              s2+=s.charAt(i);
      }
      s=s2;
        len=s.length();
        if(len==0)
        {
            frequent_char=0;
            return null;
        }
      /*  if(s.charAt(0)>=65 && s.charAt(0)<=91)
        {   if(len==1)
            {
            return null;
            }
            s=s.substring(1);
            len--;
        }
*/
        if(len==1) {
            frequent_char=0;
            return s;
        }
        ans+=s.charAt(0);
//not working
        /*        c1=s.charAt(0);
        int iter=1;
        while(s.charAt(iter)==s.charAt(iter-1))
        {iter+=1;
        count1+=1;
        }
        c2=s.charAt(iter);
        for(iter+=1;iter<len;iter++){
            if(s.charAt(iter)==s.charAt(iter-1)){
                count2+=1;
            }
        }
        c3=s.charAt(iter);
        for(iter+=1;iter<len;iter++){
            if(s.charAt(iter)==s.charAt(iter-1)){
                count3+=1;
            }
        }
        if(count1>=count2){
            if(count2>=count3){

            }else{
                int temp=count2;
                char t=c2;
                count2=count3;
                c2=c3;
                count3=temp;
                c3=t;

            }
        }else{
            int temp=count2;
            char t=c2;
            count2=count1;
            c2=c1;
            count1=temp;
            c1=t;
            if(count2>=count3){

            }else{
                if()
            }
        }*/
        //working method
        count1=1;count2=1;
        c1=s.charAt(0);
        for(int i=1;i<len;i++){
            if(s.charAt(i-1)==s.charAt(i)){
                count1++;
                continue;
            }
            else{
                if(count1>count2){
                    count2=count1;
                    c1=s.charAt(i-1);

                }
                count1=1;
               count+=1;
            }
        }
        avg=len/count;
        frequent_char=c1;
        int current=1;

        for(int i=1;i<len;i++){
            if(s.charAt(i-1)==s.charAt(i)){
                current+=1;
                continue;
            }
            else{
                if(current>avg & ans.charAt(ans.length()-1)!=s.charAt(i-1))
                    ans+=s.charAt(i-1);
                current=1;
            }
        }

        return ans;
    }
    @Override
    public View onCreateInputView() {
        kv=(KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        keyboard=new Keyboard(this,R.xml.qwerty);
        kv.setKeyboard(keyboard);
        kv.setOnKeyboardActionListener(this);
        featuredb=new DataDBHelper(this);
        SHAIME gd=new SHAIME();
        sf=new Save_feature();
        swipeWord=new GetSwipeWord(this);
        //gDetect.setOnDoubleTapListener(gd);
        kv.setOnTouchListener(new View.OnTouchListener(){
           @Override
           public boolean onTouch(View v, MotionEvent event){
               int index = event.getActionIndex();
               int action = event.getActionMasked();
               int pointerId = event.getPointerId(index);



               //check for actions of motion event
               if (event.getAction()==MotionEvent.ACTION_DOWN){
                   //retrieve key at current
                   char ch=getkeylabel(event);
                   if(ch>0)
                   swipe+=ch;
                 // test+=swipe.charAt(0);
                   //set up start timer for measuring duration
                 //  start=System.currentTimeMillis();
                   //setup velocity tracker
                   if(mvel == null) {
                       // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                       mvel = VelocityTracker.obtain();
                   }
                   else {
                       // Reset the velocity tracker back to its initial state.
                       mvel.clear();
                   }
               }

               if(event.getAction()==MotionEvent.ACTION_MOVE){
                   mvel.addMovement(event);
                   mvel.computeCurrentVelocity(1000);
                   // Log velocity of pixels per second
                   // Best practice to use VelocityTrackerCompat where possible.
                   x_vel+= abs(VelocityTrackerCompat.getXVelocity(mvel,pointerId));
                   y_vel+= abs(VelocityTrackerCompat.getYVelocity(mvel,pointerId));
                   n_event+=1;
                 //  Log.d("", "X velocity: " +  x_vel);
                 //  Log.d("", "Y velocity: " +  y_vel);

               }
               if(event.getAction()== MotionEvent.ACTION_UP){
                   //record time when finger lifted up
        //           end=System.currentTimeMillis();
                   //calculate duration
          //         duration=(end-start)/100;
                   //calculate velocity pixels per sec
                 //  Log.d("", "X velocity: " +  x_vel);
                  // Log.d("", "Y velocity: " +  y_vel);

                   velocity=Math.sqrt(x_vel*x_vel+y_vel*y_vel);
                   //obtain pressure
                   pressure+=event.getPressure();
                   np_event+=1;
                   //sf.set_duration(duration);
                   //sf.set_pressure(pressure);
                   //sf.set_speed(velocity);
                   //featuredb.add_feature(sf);
                   //featuredb.databasetoString();
                   //get final string
                   swipe2=get_final_string(swipe);
                   swipe="";
                   if(swipe2==null)
                       swipe2="";
                   //print generated string
                   System.out.println(swipe+"\n 2nd "+ swipe2);
               }

                if(((int)old_x)==0 &((int) old_y)==0){
                    old_x=event.getX();
                    old_y=event.getY();
                    swipe+=getkeylabel(event);
                }
                else if(((int)olddir_x)==0 &((int)olddir_y)==0){
                    olddir_x=event.getX()-old_x;
                    olddir_y=event.getY()-old_y;
                    old_x=event.getX();
                    old_y=event.getY();
                }
               else{
                    check_change(event);
                }

               // Return false to avoid consuming the touch event
               return false;
           }
        });

       // swipe="";
        return kv;
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
        Log.d(TAG,"Up");
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
        if(!(primaryCode==Keyboard.KEYCODE_DELETE||primaryCode==Keyboard.KEYCODE_SHIFT||(char)primaryCode==' ')) {//record time when finger lifted up
            end = System.currentTimeMillis();
            //calculate duration
            duration = (end - start) / 1000;
            //pressure is pressure/nt
            pressure = pressure / np_event;
            Log.d("ans", "X velocity: " + x_vel / n_event);
            Log.d("ans", "Y velocity: " + y_vel / n_event);
            //save into database
            sf.set_duration(duration);
            sf.set_pressure(pressure);
            sf.set_speed(velocity);
            featuredb.add_feature(sf, this);
            // featuredb.databasetoString();
        }

        //end saving
        start=0;
        end=0;
        pressure=0;np_event=1;
        x_vel=0;y_vel=0;n_event=1;
        swipe="";
        test="";
    }
    @Override
    public void onPress(int primaryCode) {
        String first=""+(char)primaryCode;
         test+=(char)primaryCode;
        Log.d("Like",first);
        //swipe=(char)primaryCode+swipe;
        //set up start timer for measuring duration
        start=System.currentTimeMillis();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //String s=""+event.getUnicodeChar();

        return super.onKeyDown(keyCode, event);
    }

    public String strings_matched(String regex){
        System.out.println("Yaha");
        ArrayList<String> suggest = swipeWord.get_suggestion(regex);
        String result="";
        int count=0;
        for (String s : suggest) {
            if (s.length() >= swipe2.length() - 2 && s.length() <= swipe2.length() + 2) {
                System.out.println("Dictionary are " + s);
                if(count==0)
                    result=s;
                count++;
                if (count == 3)
                    break;
            }
        }

        return result;

    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        InputConnection ic = getCurrentInputConnection();
       // playClick(primaryCode);
        char c=(char)primaryCode;
        String s="";
        //s+=c;
        //Log.d("coming",s);


        switch(primaryCode){
            case Keyboard.KEYCODE_DELETE :
                ic.deleteSurroundingText(1, 0);
                swipe="";
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
                if (swipe2.length()!=0 && code!=swipe2.charAt(swipe2.length()-1)){
                    swipe2+="+"+code;
                }
                if(frequent_char==0||frequent_char==code||frequent_char==test.charAt(0))
                test+=".*"+code;
                else
                test+=".*"+frequent_char+".*"+code;
                Log.d("Pattern",test);
                if(Character.isLetter(code) && capson){
                    code = Character.toUpperCase(code);
                }
                if(swipe2.length()<3) {
                    ic.commitText(String.valueOf(code), 1);
                    swipe="";
                    swipe2="";
                }
                else{
                    s=strings_matched(test);
                    s+=" ";
                    if(s.length()<=1){
                        ic.commitText(String.valueOf(code), 1);
                    }
                    else
                    ic.commitText(s,s.length());
                }
        }
    }

    public void pickDefaultCandidate() {
        pickSuggestionManually(0);
    }

    public void pickSuggestionManually(int index) {

    }



}
