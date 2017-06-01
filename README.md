# Chinese-Remainder-Clock
Android app for a Chinese Remainder Clock

(This code should import directly into AndroidStudio. Just copy the full directory into AndroidStudioProjects and it should be recognized immediately.)

This app tells time, and allows you to manipulate time, using a Chinese remainder clock.

What is that all about? If an ordinary clock would tell time in the form 10:06:47, then a "Chinese Remainder Clock" tells us this same time in the form 12:021:132. These digits are the remainder after dividing hours by 3 and 4; minutes by 3, 4, and 5; and seconds by 3, 4, and 5. A famous result called the Chinese Remainder Theorem promises that if you know these remainders, you can reconstruct the hour, minute, and second uniquely.

The app, which is meant primarily as a pedagogical tool, offers two different representations of a Chinese Remainder Clock (one analog, and one digital), and works with 12- and 24-hour time. Users can experiment with incrementing or decrementing various units to see how the clock works, or can modify the time directly. Just touch the screen to reveal the various options.
