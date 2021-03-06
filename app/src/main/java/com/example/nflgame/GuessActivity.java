package com.example.nflgame;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class GuessActivity extends AppCompatActivity implements View.OnClickListener {

    public DatabaseHelper dbHelper = new DatabaseHelper(this);

    static String SEASON;
    static final long COUNTDOWN_IN_MILLISEC = 11000;
    ArrayList<Datensatz> listOfAllGamesFromSelectedSeason = new ArrayList<>();
    Datensatz currentGame = new Datensatz();
    int randomGameId;
    int gamesToPlayCounter, gamesToPlayTotal;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    // Views
    TextView textViewSeason, textViewGameType, textViewWeek, textViewWeekday, textViewAwayTeam, textViewAwayScore, textViewHomeTeam, textViewHomeScore, textViewQuestionCount, textViewCountdown, textViewCorrect, textViewIncorrect;
    ImageView imageViewBackToSeasonList, imageViewCardAway, imageViewCardHome, imageViewSkipButton, imageViewDrawButton, imageViewOverlayCorrect, imageViewOverlayIncorrect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess);

        Intent intent = getIntent();
        SEASON = intent.getStringExtra(SeasonPickerActivity.EXTRA_SEASON);


        textViewCountdown = findViewById(R.id.textViewCountdown);
        textViewSeason = findViewById(R.id.textViewSeason);
        textViewGameType = findViewById(R.id.textViewGameType);
        textViewWeek = findViewById(R.id.textViewWeek);
        textViewWeekday = findViewById(R.id.textViewWeekday);
        textViewAwayTeam = findViewById(R.id.textViewAwayTeam);
        textViewAwayScore = findViewById(R.id.textViewAwayScore);
        textViewAwayTeam = findViewById(R.id.textViewAwayTeam);
        textViewHomeTeam = findViewById(R.id.textViewHomeTeam);
        textViewHomeScore = findViewById(R.id.textViewHomeScore);
        imageViewBackToSeasonList = findViewById(R.id.imageViewBackToSeasonList);
        imageViewCardAway = findViewById(R.id.imageViewCardAway);
        imageViewCardHome = findViewById(R.id.imageViewCardHome);
        textViewQuestionCount = findViewById(R.id.textViewQuestionCount);
        imageViewSkipButton = findViewById(R.id.imageViewSkipButton);
        imageViewDrawButton = findViewById(R.id.imageViewDrawButton);
        textViewCorrect = findViewById(R.id.textViewCorrect);
        textViewIncorrect = findViewById(R.id.textViewIncorrect);
        imageViewOverlayCorrect = findViewById(R.id.imageViewOverlayCorrect);
        imageViewOverlayIncorrect = findViewById(R.id.imageViewOverlayIncorrect);

        imageViewCardAway.setOnClickListener(this);
        imageViewCardHome.setOnClickListener(this);
        imageViewDrawButton.setOnClickListener(this);
        imageViewOverlayCorrect.setOnClickListener(this);
        imageViewOverlayIncorrect.setOnClickListener(this);

        imageViewBackToSeasonList.setOnClickListener(this);
        imageViewSkipButton.setOnClickListener(this);

        // allGamesFromSelectedSeason for savegame
        getOrCreateListOfAllGamesFromSelectedSeason();

        // get a random game from the list of all games from selected Season and set TextViews
        setCurrentGame();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageViewCardAway:
                dbHelper.updateScore(SEASON, timeLeftInMillis);
                // cancel countdowntimer
                countDownTimer.cancel();
                // remove the played game from the list of games to play
                listOfAllGamesFromSelectedSeason.remove(randomGameId);
                // update games to play counter
                dbHelper.updateGamesToPlayCounter(SEASON, dbHelper.getGamesToPlayCounter(SEASON) + 1);
                // check if answer ist correct
                dbHelper.updateCorrectOrIncorrect(SEASON, checkIfCorrectOrIncorrect(currentGame.getAway_score(), currentGame.getHome_score()));
                if (checkIfCorrectOrIncorrect(currentGame.getAway_score(), currentGame.getHome_score())) {
                    imageViewOverlayCorrect.setVisibility(View.VISIBLE);
                } else {
                    imageViewOverlayIncorrect.setVisibility(View.VISIBLE);
                }
//                //check if season is played through
//                if (dbHelper.isPlayedThrough(SEASON)) {
//                    Log.d("Played through: ", "TRUE");
//                    finish();
//                } else {
//                    setCurrentGame();
//                }
                break;

            case R.id.imageViewCardHome:
                dbHelper.updateScore(SEASON, timeLeftInMillis);
                // cancel countdowntimer
                countDownTimer.cancel();
                // remove the played game from the list of games to play
                listOfAllGamesFromSelectedSeason.remove(randomGameId);
                // update games to play counter
                dbHelper.updateGamesToPlayCounter(SEASON, dbHelper.getGamesToPlayCounter(SEASON) + 1);
                // check if answer ist correct
                dbHelper.updateCorrectOrIncorrect(SEASON, checkIfCorrectOrIncorrect(currentGame.getHome_score(), currentGame.getAway_score()));
                if (checkIfCorrectOrIncorrect(currentGame.getHome_score(), currentGame.getAway_score())) {
                    imageViewOverlayCorrect.setVisibility(View.VISIBLE);
                } else {
                    imageViewOverlayIncorrect.setVisibility(View.VISIBLE);
                }
//                //check if season is played through
//                if (dbHelper.isPlayedThrough(SEASON)) {
//                    Log.d("Played through: ", "TRUE");
//                    finish();
//                } else {
//                    setCurrentGame();
//                }
                break;

            case R.id.imageViewDrawButton:
                dbHelper.updateScore(SEASON, timeLeftInMillis);
                // cancel countdowntimer
                countDownTimer.cancel();
                // remove the played game from the list of games to play
                listOfAllGamesFromSelectedSeason.remove(randomGameId);
                // update games to play counter
                dbHelper.updateGamesToPlayCounter(SEASON, dbHelper.getGamesToPlayCounter(SEASON) + 1);
                // check if answer ist correct
                if (currentGame.getAway_score() == currentGame.getHome_score()) {
                    dbHelper.updateCorrectOrIncorrect(SEASON, true);
                    imageViewOverlayCorrect.setVisibility(View.VISIBLE);
                } else {
                    dbHelper.updateCorrectOrIncorrect(SEASON, false);
                    imageViewOverlayIncorrect.setVisibility(View.VISIBLE);
                }
//                //check if season is played through
//                if (dbHelper.isPlayedThrough(SEASON)) {
//                    Log.d("Played through: ", "TRUE");
//                    finish();
//                } else {
//                    setCurrentGame();
//                }
                break;

            case R.id.imageViewBackToSeasonList:
                dbHelper.updateScore(SEASON, timeLeftInMillis);
                // cancel countdowntimer
                countDownTimer.cancel();
                backToSeasonPickerActivity();
                break;

            case R.id.imageViewSkipButton:
                dbHelper.updateScore(SEASON, timeLeftInMillis);
                // cancel countdowntimer
                countDownTimer.cancel();
                setCurrentGame();
                break;

            case R.id.imageViewOverlayCorrect:
            case R.id.imageViewOverlayIncorrect:
                //check if season is played through
                if (dbHelper.isPlayedThrough(SEASON)) {
                    Log.d("Played through: ", "TRUE");
                    finish();
                } else {
                    setCurrentGame();
                    imageViewOverlayCorrect.setVisibility(View.GONE);
                    imageViewOverlayIncorrect.setVisibility(View.GONE);
                }
                break;

        }
        Log.d("Score", dbHelper.getTotalScore());

    }

    private void getOrCreateListOfAllGamesFromSelectedSeason() {
        if (dbHelper.isAllGamesFromSelectedSeasonJSONempty(SEASON)) {
            try {
                // create list of all games from the selected season
                listOfAllGamesFromSelectedSeason = dbHelper.makeAllGamesFromSelectedSeasonIfNotInSaveGame(SEASON);
                // write the list into the database savegameTable as String
                dbHelper.writeListOfAllGamesFromSelectedSeasonToDatabase(fromListToJSONString(listOfAllGamesFromSelectedSeason), SEASON);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                // load List of all games from the selected season
                listOfAllGamesFromSelectedSeason = fromJSONStringToList(dbHelper.loadListOfAllGamesFromSelectedSeasonFromDatabase(SEASON));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

    }

    private void setCurrentGame() {
        //get a random game from the remaining games in the list
        randomGameId = createRandomGameId(listOfAllGamesFromSelectedSeason.size());
        currentGame = listOfAllGamesFromSelectedSeason.get(randomGameId);
        // set all TextViews
        setTextViews();
    }

    public void setTextViews() {

        textViewSeason.setText("Season " + currentGame.getSeason());
        textViewGameType.setText(currentGame.getGame_type());
        textViewWeek.setText("Week " + currentGame.getWeek());
        textViewWeekday.setText(currentGame.getWritten_date());
        textViewAwayTeam.setText(getShortTeamName(currentGame.getAway_team()));
        textViewAwayScore.setText(currentGame.getAway_score());
        textViewHomeTeam.setText(getShortTeamName(currentGame.getHome_team()));
        textViewHomeScore.setText(currentGame.getHome_score());

        textViewCorrect.setText(String.valueOf(dbHelper.getCorrect(SEASON)));
        textViewIncorrect.setText(String.valueOf(dbHelper.getIncorrect(SEASON)));

        textViewQuestionCount.setText(dbHelper.getGamesToPlayCounter(SEASON) + "/" + dbHelper.getGamesToPlayTotal(SEASON));

        imageViewCardAway.setImageDrawable(getCardName(currentGame.getAway_team()));
        imageViewCardHome.setImageDrawable(getCardName(currentGame.getHome_team()));

        timeLeftInMillis = COUNTDOWN_IN_MILLISEC;
        startCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountdownText();
            }
        }.start();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewCountdown.setText(timeFormatted);

        if (timeLeftInMillis < 5000) {
            textViewCountdown.setTextColor(Color.RED);
        } else {
            textViewCountdown.setTextColor(Color.BLACK);
        }
    }

    public Drawable getCardName(String teamName) {
        if (!teamName.equals("Washington Football Team") && !teamName.equals("San Francisco 49ers")) {
            String[] parts = teamName.split(" ");
            teamName = parts[parts.length - 1].toLowerCase(Locale.ROOT);
        } else if (teamName.equals("Washington Football Team")) {
            teamName = "washington";
        } else if (teamName.equals("San Francisco 49ers")) {
            teamName = "sf49ers";
        }
        Log.d("TEAM NAME", teamName);

        Resources res = getResources();
        int resID = res.getIdentifier(teamName + "_card", "drawable", getPackageName());
        return res.getDrawable(resID);
    }

    public int createRandomGameId(int size) {
        Random r = new Random();
//        int low = 0;
//        int high = size;
//        return r.nextInt(size - low) + low;
        return r.nextInt(size);
    }

    public boolean checkIfCorrectOrIncorrect(String choosen, String notChoosen) {
        return Integer.parseInt(choosen) > Integer.parseInt(notChoosen);
    }

    public String fromListToJSONString(ArrayList listOfAllGamesFromSelectedSeason) {
        Gson gson = new Gson();
        return gson.toJson(listOfAllGamesFromSelectedSeason);
    }

    public ArrayList fromJSONStringToList(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<ArrayList<Datensatz>>() {
        });
    }

    public String getShortTeamName(String fullTeamName) {
        if (fullTeamName.equals("Washington Football Team")) {
            return "Washington";
        }
        String[] parts = fullTeamName.split(" ");
        String shortTeamName = parts[parts.length - 1];
        return shortTeamName;
    }

    public void backToSeasonPickerActivity() {
        Intent intent = new Intent(this, SeasonPickerActivity.class);
        startActivity(intent);
    }


}