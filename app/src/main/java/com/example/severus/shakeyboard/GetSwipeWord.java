package com.example.severus.shakeyboard;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * Created by abhishekcs10 on 11/11/16.
 */

public class GetSwipeWord {
  private List<String> dict=new ArrayList<String>();

    private void readRawTextFile(Context ctx)
    {
        InputStream inputStream = ctx.getResources().openRawResource(R.raw.words2);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                dict.add(line);
            }
        } catch (IOException e) {
            //return null;
        }
        //return text.toString();
    }

    public GetSwipeWord(Context ctx){
        readRawTextFile(ctx);
    }
    private  void add_word(String s){
        dict.add(0,s);
    }

    public ArrayList<String> get_suggestion(String regex){
        System.out.println("Came for words");
        ArrayList<String> array_list = new ArrayList<String>();
        System.out.println(" Dict size "+ dict.size() + " Searching for "+ regex);

        try {
            Pattern p = Pattern.compile(regex);
            Matcher m;

            for (String string : dict) {
                m = p.matcher(string);
                if (m.matches()) {
                   // System.out.println("Matched "+ string );
                    array_list.add(string);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  array_list;
    }

}
