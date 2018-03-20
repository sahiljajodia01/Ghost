/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    FastDictionary fastDictionary;
    TextView text;
    TextView label;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();

        try {
            fastDictionary = new FastDictionary(assetManager.open("words.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        String currentWordFragemnt = text.getText().toString();

        if(currentWordFragemnt.length() >= 4 && fastDictionary.isWord(currentWordFragemnt)) {

            Toast.makeText(this, "Computer Won", Toast.LENGTH_SHORT).show();
            Log.i("Computer", "win");

        }
        else {
            if(fastDictionary.getGoodWordStartingWith(currentWordFragemnt) == null) {
                Toast.makeText(this, "Computer Wins", Toast.LENGTH_SHORT).show();
                Log.i("Computer", "wins");
            }
            else {
                Log.i("Return Word", fastDictionary.getGoodWordStartingWith(currentWordFragemnt));
                String validWord = fastDictionary.getGoodWordStartingWith(currentWordFragemnt);
                char nextChar = validWord.charAt(currentWordFragemnt.length());
                addTextToGame(nextChar);
            }
        }

        userTurn = true;
        label.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        char c = (char) event.getUnicodeChar();
        if(Character.isDigit(c)) {
            return super.onKeyUp(keyCode, event);
        }
        else {
            addTextToGame(c);
            label.setText(COMPUTER_TURN);
            userTurn = false;
            computerTurn();
            return super.onKeyUp(keyCode, event);
        }

        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/

    }

    public void addTextToGame(char c) {
        text.setText(text.getText().toString() + c);
    }

    public void onChallenge(View view) {
        String currentWord = text.getText().toString();
        if(currentWord.length() >= 4 && fastDictionary.isWord(currentWord)){
            Toast.makeText(this, "User Won", Toast.LENGTH_SHORT).show();
        }
        else if(fastDictionary.getGoodWordStartingWith(currentWord) != null) {
            Toast.makeText(this, "Computer Won!", Toast.LENGTH_SHORT).show();
            text.setText(fastDictionary.getGoodWordStartingWith(currentWord));
        }
        else {
            Toast.makeText(this, "User Won", Toast.LENGTH_SHORT).show();
        }
    }
}
