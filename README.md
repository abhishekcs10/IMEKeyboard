# IMEKeyboard

This code is implementation of swipe feature in the custom keyboard.

For enabling the swipe feature following is the algorithm used...

1- Enable keyboard listener and override the touch event to get track of the touch events occuring.

2- Now track the sequence of keys on which touch event has occured and store them. (this tracks the letters irrespective of the frequency i.e. a letter will occur as many times as you hold the keyboard button.)
To know how you detect key label, read following stack overflow link->

http://stackoverflow.com/questions/30787066/retrieve-x-and-y-coordinates-of-a-key-in-android

3- Make a regular expression from two most frequent letters where 1st and the last letter are always the same as recorded during the touch event listener. 

4- Add a dictionary file in your resource folder and then search for the regular expression in your dictionary.

5- Display the top three results that match with the regular expression and whose length is between 2 less or more than the number of unique characters in the recorded string of letter in step 2.

Request->
---------

Please suggest any better technique for making predictions of words if any. Your thoughts are welcomed...:)

Thank you

