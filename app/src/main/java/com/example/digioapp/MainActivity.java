package com.example.digioapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    EditText et_num;
    Button btn_start;
    TableLayout table;
    TextView txt_player;
    RecyclerView lv_history;

    Button[][] buttons;
    boolean isPlayer1Turn = true, isReplay=false;
    int num, allRoundCount, roundCount, count, gameCount, position_on_board, replay_count;
    int[] gameState;
    List<int[]> winPosition;

    SQLiteDatabase myDatabase;
    List<HistoryModel> gameList, historyList;
    HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_num = findViewById(R.id.et_num);
        btn_start = findViewById(R.id.btn_start);
        txt_player = findViewById(R.id.txt_player);
        table = findViewById(R.id.table);
        lv_history = findViewById(R.id.lv_history);

        gameList = new ArrayList<>();
        historyList = new ArrayList<>();

        init();
    }

    private void init() {
        setupHistoryAdapter();
        setupHistoryDb();

        btn_start.setOnClickListener(v -> {
            String str_num = et_num.getText().toString();
            if (!"".equalsIgnoreCase(str_num)) {
                num = Integer.parseInt(str_num);
                if (num <= 8) {
                    resetBoard();
                } else {
                    Toast.makeText(this, "Limit up to 8x8 board", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupHistoryDb() {
        gameCount = 0;

        myDatabase = openOrCreateDatabase("xo_db", MODE_PRIVATE, null);
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS history(id INT, user INT, table_num INT, position INT, isWon INT);");
        myDatabase.execSQL("DELETE FROM history;");
    }

    private void insertHistory(int player, int position, int isWon) {
        myDatabase.execSQL("INSERT INTO history VALUES(" + gameCount + "," + player + "," + num + "," + position + "," + isWon + ");");
    }

    private void updateHistory() {
//        gameList.clear();

        Cursor resultSet = myDatabase.rawQuery("Select * from history WHERE isWon in (1,2) GROUP BY id;", null);
        resultSet.moveToLast();
//        resultSet.moveToFirst();
//        do {
            gameList.add(new HistoryModel(resultSet.getInt(0), resultSet.getInt(1),
                    resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4)));
//        } while (resultSet.moveToNext());

        adapter.notifyItemInserted(gameList.size()-1);
    }

    private void setupHistoryAdapter() {
        adapter = new HistoryAdapter(gameList);
        lv_history.setLayoutManager(new LinearLayoutManager(this));
        lv_history.setAdapter(adapter);

        adapter.setOnClickListener(v -> {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            int value = gameList.get(position).getId();
            playHistory(value);
        });
    }

    private void playHistory(int id) {
        replay_count =0;
        historyList.clear();

        Cursor resultSet = myDatabase.rawQuery("Select * from history WHERE id=" + id + ";", null);
        resultSet.moveToFirst();
        do {
            historyList.add(new HistoryModel(resultSet.getInt(0), resultSet.getInt(1),
                    resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4)));
        } while (resultSet.moveToNext());

        num = historyList.get(0).getTable_num();
        resetBoard();

        isReplay = true;

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        int position = historyList.get(replay_count).getPosition();
                        int resID = getResources().getIdentifier(position + "", "id", getPackageName());
                        TextView txt = findViewById(resID);
                        runOnUiThread(txt::performClick);

                        replay_count++;
                        if (replay_count == historyList.size()){
                            timer.cancel();
                        }
                    }
                },
                1000, 2000);
    }

    private void resetBoard() {
        btn_start.setText("Reset");
        btn_start.setBackgroundColor(getResources().getColor(R.color.red, null));

        table.setEnabled(true);

        buttons = new Button[num][num];
        allRoundCount = num * num;
        roundCount = 0;
        count = 0;
        position_on_board = 99;
        gameCount++;

        isPlayer1Turn = true;
        txt_player.setText("Player 1 Turn [X]");

        isReplay = false;

        gameState = new int[allRoundCount];
        for (int i = 0; i < allRoundCount; i++) {
            gameState[i] = 2;
        }

        winPosition = new ArrayList<>();
        // 0 1 2
        // 3 4 5
        // 6 7 8

        //0 4 8
        int[] position_cross_left = new int[num];
        for (int pos = 0; pos < num; pos++) {
            position_cross_left[pos] = pos + (num * pos);
        }
        winPosition.add(position_cross_left);

        // 2 4 6
        int[] position_cross_right = new int[num];
        int rth_position = num;
        for (int pos = 0; pos < num; pos++) {
            position_cross_right[--rth_position] = (num - pos) * (num - 1);
        }
        winPosition.add(position_cross_right);

        for (int i = 0; i < num; i++) {
            int[] position_row = new int[num];
            int[] position_col = new int[num];

            for (int pos = 0; pos < num; pos++) {
                //0 3 6 //1 4 7 //2 5 8
                position_col[pos] = i + (num * pos);
                //0 1 2 //3 4 5 //6 7 8
                position_row[pos] = (i * num) + pos;
            }

            winPosition.add(position_col);
            winPosition.add(position_row);

        }

        table.removeAllViews();
        setupTable();
    }

    public void setupTable() {
        for (int row = 0; row < num; row++) {
            TableRow tbrow = new TableRow(this);

            for (int col = 0; col < num; col++) {
                TextView txt = new TextView(this);
                txt.setBackgroundResource(R.drawable.squre_button);
                txt.setTextSize(30f);
                txt.setGravity(Gravity.CENTER);

                txt.setId(count);
                count++;

                txt.setOnClickListener(v -> {
                    //if empty slot
                    if ("".equalsIgnoreCase(txt.getText().toString())) {
                        position_on_board = txt.getId();

                        if (isPlayer1Turn) {
                            //0
                            txt.setText("X");
                            txt.setTextColor(getResources().getColor(R.color.red, null));
                            gameState[position_on_board] = 0;
                            txt_player.setText("Player 2 Turn [O]");
                        } else {
                            //1
                            txt.setText("O");
                            gameState[position_on_board] = 1;
                            txt_player.setText("Player 1 Turn [X]");
                        }

                        roundCount++;

                        //check isWin
                        if (checkForWin()) {
                            if (isPlayer1Turn) {
                                insertHistory(0, position_on_board, 1);
                                playerWins(0);
                            } else {
                                insertHistory(1, position_on_board, 1);
                                playerWins(1);
                            }
                        } else if (roundCount == allRoundCount) {
                            if (isPlayer1Turn) {
                                insertHistory(0, position_on_board, 2);
                            } else {
                                insertHistory(1, position_on_board, 2);
                            }

                            //draw
                            playerWins(2);
                        } else {
                            if (isPlayer1Turn) {
                                insertHistory(0, position_on_board, 0);
                            } else {
                                insertHistory(1, position_on_board, 0);
                            }

                            //if not win or draw goto next turn
                            isPlayer1Turn = !isPlayer1Turn;

                            if (!isPlayer1Turn) {
                                player2Click();
                            }
                        }

                    }
                });

                tbrow.addView(txt);
            }

//            table.setWeightSum(1);
//            table.setStretchAllColumns(true);
            table.addView(tbrow);
        }
    }

    private boolean checkForWin() {
        for (int i = 0; i < winPosition.size(); i++) {
            int first = 99, next;
            int[] forWinPosition = winPosition.get(i);

            for (int j = 0; j < forWinPosition.length; j++) {
                if (first == 99) {
                    first = gameState[forWinPosition[j]];
                } else {
                    next = gameState[forWinPosition[j]];
                    if (next == 2 || first != next) {
                        //not match
                        break;
                    } else {
                        //match then check next
                        first = next;
                        if (j == forWinPosition.length - 1) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private void player2Click() {
        new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                for (int i = 0; i < winPosition.size(); i++) {
                    int first = 99, next;
                    int[] forWinPosition = winPosition.get(i);

                    for (int j = 0; j < forWinPosition.length; j++) {
                        if (first == 99) {
                            first = gameState[forWinPosition[j]];
                        } else {
                            next = gameState[forWinPosition[j]];

                            int position = forWinPosition[j];
                            int resID = getResources().getIdentifier(position + "", "id", getPackageName());
                            TextView txt = findViewById(resID);

                            if (next == 2 && first == 0) {
                                //not match
                                txt.performClick();
                                return;
                            } else {
                                //match then check next
                                first = next;
                            }
                        }
                    }
                }

                //if not match condition
                boolean clicked = false;
                while (!clicked) {
                    int position = (int) Math.floor(Math.random() * allRoundCount);
                    if (gameState[position] == 2) {
                        int resID = getResources().getIdentifier(position + "", "id", getPackageName());
                        TextView txt = findViewById(resID);
                        txt.performClick();

                        clicked = true;
                    }
                }
            }
        }.start();
    }


    private void playerWins(int player) {
        if (player == 0) {
            txt_player.setText("You Won!");
        } else if (player == 1) {
            txt_player.setText("You Lose!");
        } else {
            txt_player.setText("Draw!");
        }

        if (!isReplay) {
            updateHistory();
        }
    }
}