# Chinese-Remainder-Clock
Android app for a Chinese Remainder Clock

This app tells time, and allows you to manipulate time, using a Chinese remainder clock.

What is that all about? If an ordinary clock would tell time in the form 10:06:47, then a "Chinese Remainder Clock" tells us this same time in the form 12:021:132. These digits are the remainder after dividing hours by 3 and 4; minutes by 3, 4, and 5; and seconds by 3, 4, and 5. A famous result called the Chinese Remainder Theorem promises that if you know these remainders, you can reconstruct the hour, minute, and second uniquely.

The app, which is meant primarily as a pedagogical tool, offers two different representations of a Chinese Remainder Clock (one analog, and one digital), and works with 12- and 24-hour time. Users can experiment with incrementing or decrementing various units to see how the clock works, or can modify the time directly. Just touch the screen to reveal the various options.

## Most development on this has stopped

### Why I (mostly) stopped developing this

It became too hard to keep the thing working.

In principle, this code should import directly into AndroidStudio. Just copy the full directory into AndroidStudioProjects and it should be recognized immediately.

In practice, the people who run Google, Gradle, and JetBrains are either uninterested in or incapable of producing an IDE that don't wreck your build unless you upgrade constantly.  I don't mean this as a complaint -- after all, they the software is free -- but that doesn't change the fact that **every few months**, some combination of:
* Gradle,
* the Java compiler,
* the Kotlin plugins,
* the Android APIs, and/or
* other things I've forgotten...

...would "upgrade" to some inconsistent state, forcing me to waste _multiple hours_ just to get the device to build again.

Two examples:
* Antonella recently asked me to send her some pictures of the app in operation. It had been a couple of years since I had touched the code, and I had changed computers at least once. I had to download and start Android Studio. I spent the next 4 hours:
  * upgrading Gradle (twice);
  * modifying gradle build scripts, gradle properties, and project settings to something Android Studio would actually accept;
  * adding `@Deprecated` tags to a few things to silence the compiler;
  * removing a development test module that I wasn't using, but Google changed something so that an empty test module preventing me from building the app ;
  * adjusting code for things that used to be pointers and no longer are (I can't believe I am writing that); and
  * drilling down into an obscure, poorly explained run-time error.

  Essentially, what should have been a 10-minute task turned into a 4 1/2-hour exercise in frustration. Most of the problems were entirely the fault of the IDE developers' deciding that things had to change, and not providing an easy upgrade path. Once upon a time, Android Studio made that much easier to figure out; I can remember when they'd tell you not just that there was a problem with your Gradle setup, but they'd tell you which file had to change, what had to change, _and they'd link to it from the error message!_ At the time, that was kind of magical.

* I decided the next day to fix all the deprecation warnings, which required:
  * updating the `minSdkVersion`, because I couldn't work out how to make some newer things work otherwise;
  * moving to a new color picker gadget, because Google _still_ can't be bothered to provide one of the most common UI tools you'd need, and the new API changed all sorts of types, which means the old `AmbilWarna` one is now unusable;
  * removing the old `android.service.dreams.DreamService`, which seemed to difficult to bring up-to-date, especially since Google never really supported it much and I think they've abandoned it anyway;
  * changing everything `Activity`- and `Fragment`-related to `androidx`, which merits its own sublist:
    * _to Google's credit_,
      * most warnings magically went away the moment you finally found which `androidx` API applied and imported it;
      * some functions changed their types slightly, so that you no longer have to cast the type and pray everything went well;
    * _to Google's discredit_, some pretty fundamental things _did not_ work that way; for example:
      * for a `PreferenceFragment`, you had to do things in `onCreate`; with a `PreferenceFragment`, you do things in `onCreatePreferences`;
      * `PreferenceActivity` is no longer a thing; it's now another `AppCompatActivity`, and the process of showing them is different from what one could do with a `PreferenceActivity`;
      * `Preference` no longer has an `onCreateView` function: you take care of that in the constructor now, and you don't even have to inflate the layout anymore, but merely set the layout resource;
      * an `Activity` no longer has a `getFragmentManager` but rather a `getSupportFragmentManager` which, despite the name change, to all appearances does _exactly the same thing_;
      * the procedure to obtain strings from the app's resources has changed;
      * `Space` formatting was moved out of one library and into another, leading to a confusing compile-time error;
      * ...and as far as I can tell, very little of this was documented anywhere aside from StackOverflow questions, as if it would cost Google a great deal of time or effort to state in the `@Deprecated` annotations _where to find the new, appropriate APIs_.
  
  ...I also just noticed that I forgot to fix one deprecated item. 😭 Better fix that real quick! (the good news, such as it is, is that that went pretty well... again, [thanks to StackOverflow](https://stackoverflow.com/a/74132434/4526030).)

### Why is this app not on the Google Play Store?

It used to be! However, Google imposes a burden that I consider capricious and ever-changing. For example:
* Regularly informing Google on:
  * data collection practices;
  * credit card / bank account usage;
  * ...
* 
* That includes the requirement that you log in every now and then, even if you have nothing to do.)

As a hobbyist, I only have so much time and energy, and I wasn't able to keep maintaining it. I greatly regret this, but there's only so much one person can do. I _may_ come back to it from time to time.
