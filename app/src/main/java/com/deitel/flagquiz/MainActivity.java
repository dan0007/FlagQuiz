// MainActivity.java
// Hosts the MainActivityFragment on a phone and both the
// MainActivityFragment and SettingsActivityFragment on a tablet
package com.deitel.flagquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
   // keys for reading data from SharedPreferences
   public static final String CHOICES = "pref_numberOfChoices";
   public static final String REGIONS = "pref_regionsToInclude";
   public static final String CURRENTQUESTION = "pref_currentQuestion";
   public static final String num_guesses = "pref_numberOfGuesses";

   private boolean phoneMode = true; // used to force portrait mode
   private boolean preferencesChanged = true; // did preferences change?
   private boolean formatPreferenceChanged = false;
   private FlagAndButtonFragment flagAndButtonFragment;

   // configure the MainActivity
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      flagAndButtonFragment = new FlagAndButtonFragment();
      FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
      transaction.replace(R.id.main_fragment_container, flagAndButtonFragment);
      transaction.commit();
      // set default values in the app's SharedPreferences
      PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

      // register listener for SharedPreferences changes
      PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).
         registerOnSharedPreferenceChangeListener(
            preferencesChangeListener);

      // determine screen size
      int screenSize = getResources().getConfiguration().screenLayout &
         Configuration.SCREENLAYOUT_SIZE_MASK;

      // if device is a tablet, set phoneMode to false
      if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
         screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
         phoneMode = false; // not a phone-sized device

      // if running on phone-sized device, allow only portrait orientation
      if (phoneMode)
         setRequestedOrientation(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
   }


   // called after onCreate completes execution
   @Override
   protected void onStart() {
      super.onStart();

      if (preferencesChanged) {
         // now that the default preferences have been set,
         // initialize MainActivityFragment and start the quiz
         flagAndButtonFragment = (FlagAndButtonFragment)
            getSupportFragmentManager().findFragmentById(
               R.id.main_fragment_container);

         flagAndButtonFragment.updateGuessRows(
            PreferenceManager.getDefaultSharedPreferences(this));
         flagAndButtonFragment.updateRegions(
            PreferenceManager.getDefaultSharedPreferences(this));

         if (Objects.equals(PreferenceManager.getDefaultSharedPreferences(this).getString(CURRENTQUESTION, null), "") || formatPreferenceChanged) {
            formatPreferenceChanged = false;
            flagAndButtonFragment.resetQuiz();
         } else {
            flagAndButtonFragment.currentQuestionFromPreferences(PreferenceManager.getDefaultSharedPreferences(this));
         }
         preferencesChanged = false;
      }
   }

   // show menu if app is running on a phone or a portrait-oriented tablet
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      // get the device's current orientation
      int orientation = getResources().getConfiguration().orientation;

      // display the app's menu only in portrait orientation
      if (orientation == Configuration.ORIENTATION_PORTRAIT) {
         // inflate the menu
         getMenuInflater().inflate(R.menu.menu_main, menu);
         return true;
      }
      else
         return false;
   }

   // displays the SettingsActivity when running on a phone
   @Override
   public boolean onOptionsItemSelected(MenuItem item) {
      Intent preferencesIntent = new Intent(this, SettingsActivity.class);
      startActivity(preferencesIntent);
      return super.onOptionsItemSelected(item);
   }

   @Override
   public void onBackPressed() {
      flagAndButtonFragment.signalComingBack();
      super.onBackPressed();
   }

   // listener for changes to the app's SharedPreferences
   private OnSharedPreferenceChangeListener preferencesChangeListener =
      new OnSharedPreferenceChangeListener() {
         // called when the user changes the app's preferences
         @Override
         public void onSharedPreferenceChanged(
            SharedPreferences sharedPreferences, String key) {
            // user changed app setting
            formatPreferenceChanged = !key.equals(CURRENTQUESTION);

            FlagAndButtonFragment quizFragment = (FlagAndButtonFragment)
               getSupportFragmentManager().findFragmentById(
                  R.id.main_fragment_container);

            if (key.equals(CHOICES)) { // # of choices to display changed
               quizFragment.updateGuessRows(sharedPreferences);
               quizFragment.resetQuiz();
            }
            else if (key.equals(REGIONS)) { // regions to include changed
               Set<String> regions =
                  sharedPreferences.getStringSet(REGIONS, null);

               if (regions != null && regions.size() > 0) {
                  quizFragment.updateRegions(sharedPreferences);
                  quizFragment.resetQuiz();
               }
               else {
                  // must select one region--set North America as default
                  SharedPreferences.Editor editor =
                     sharedPreferences.edit();
                  regions.add(getString(R.string.default_region));
                  editor.putStringSet(REGIONS, regions);
                  editor.apply();

                  Toast.makeText(MainActivity.this,
                     R.string.default_region_message,
                     Toast.LENGTH_SHORT).show();
               }
            }

            if (Integer.parseInt(sharedPreferences.getString(num_guesses, null)) > Integer.parseInt(sharedPreferences.getString(CHOICES, null))) {
               Toast.makeText(MainActivity.this, "The number of guesses must be less than or equal to the number of choices.\n Setting the number of guess to the number of choices", Toast.LENGTH_SHORT).show();
               SharedPreferences.Editor editor =
                       sharedPreferences.edit();
               editor.putString(num_guesses, sharedPreferences.getString(CHOICES, null));
               editor.apply();
            } else if (!key.equals(CURRENTQUESTION)) {
               Toast.makeText(MainActivity.this,
                       R.string.restarting_quiz,
                       Toast.LENGTH_SHORT).show();
            }
         }
      };
}


/*************************************************************************
 * (C) Copyright 1992-2016 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/
