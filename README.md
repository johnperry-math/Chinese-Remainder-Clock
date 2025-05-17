# Chinese-Remainder-Clock
Android app for a Chinese Remainder Clock

This app tells time, and allows you to manipulate time, using a Chinese remainder clock.

What is that all about? If an ordinary clock would tell time in the form 10:06:47, then a "Chinese Remainder Clock" tells us this same time in the form 12:021:132. These digits are the remainder after dividing hours by 3 and 4; minutes by 3, 4, and 5; and seconds by 3, 4, and 5. A famous result called the Chinese Remainder Theorem promises that if you know these remainders, you can reconstruct the hour, minute, and second uniquely.

The app, which is meant primarily as a pedagogical tool, offers two different representations of a Chinese Remainder Clock (one analog, and one digital), and works with 12- and 24-hour time. Users can experiment with incrementing or decrementing various units to see how the clock works, or can modify the time directly. Just touch the screen to reveal the various options.

## Note: This is effectively abandonware

In principle, this code should import directly into AndroidStudio. Just copy the full directory into AndroidStudioProjects and it should be recognized immediately.

In practice, the people who run Google, Gradle, and JetBrains are uninterested or incapable of producing products that don't wreck your build unless you upgrade constantly.  I don't mean this as a complaint -- after all, they provide the software for free -- but that doesn't change the fact that **every few months**, some combination of:
* Gradle,
* the Java compiler,
* the Kotlin plugins,
* the Android APIs, and/or
* other things I've forgotten...

...would upgrade to some inconsistent state, forcing me to waste _multiple hours_ just to get the device to build again.

Then there are Google's ever-changing whims on the hurdles a developer has to leap over in order to keep an app listed on the Play Store, which includes the requirement that you log in every now and then, even if you have nothing to do.

As a hobbyist, I only have so much time and energy, and I wasn't able to keep maintaining it. I greatly regret this, but there's only so much one person can do. I _may_ come back to it from time to time.
