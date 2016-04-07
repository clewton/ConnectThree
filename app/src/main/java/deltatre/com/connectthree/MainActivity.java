package deltatre.com.connectthree;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "Connect Three";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initGame();
    }

    public void playAgain(View view) {
        initGame();
    }

    private void initGame() {
        gameState = gameStateInit.clone();
        isYellowTurn = true;

        View playAgainLayout = findViewById(R.id.playAgainLayout);
        //playAgainLayout.setAlpha(0);
        playAgainLayout.setVisibility(View.GONE);

        Log.i(LOG_TAG, ">>>>>>>>>>>>>>> RESET GAME <<<<<<<<<<<<<<<<");

        for (int i=0; i<gameState.length; i++){

            String imageName = "imageView"+i;
            int id = getResources().getIdentifier(imageName, "id", getPackageName());

            ImageView counter = (ImageView) findViewById(id);
            //Log.i(LOG_TAG, "String imageName="+imageName+", int id="+id + ", value="+counter);
            if(counter!=null) {
                //counter.setAlpha(0);
                //counter.setVisibility(View.INVISIBLE);
                counter.setImageResource(android.R.color.transparent);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    boolean isYellowTurn = true;

    int[] gameStateInit = { 0,0,0,
                            0,0,0,
                            0,0,0 };

    int[] gameState;

    //boolean gameIsPlaying = true;

    public void dropIn(View view) {
        int boardSize = (int) Math.sqrt(gameState.length);

        int pos = Integer.parseInt(view.getTag().toString());


        if (gameState[pos] == 0) {
            gameState[pos] = isYellowTurn ? 1 : 2;
            Log.i(LOG_TAG, "gameState at pos: " + pos + " is set to "+gameState[pos]);

            ImageView counter = (ImageView) view;
            counter.setTranslationY(-1000f);
            counter.setImageResource(isYellowTurn ? R.drawable.yellow : R.drawable.red);
            //counter.animate().translationYBy(1000f).alpha(1).setDuration(300);
            counter.animate().translationYBy(1000f).setDuration(300);
            //counter.setVisibility(View.VISIBLE);
            isYellowTurn = !isYellowTurn;

            GameResult gameResult = getGameResult(boardSize);

            switch (gameResult.getCondition()) {
                case GAME_WIN_HORIZONTAL:
                    Log.i(LOG_TAG, "Horizontal win for Player #" +gameResult.getPlayer()+ "on row "+gameResult.getIdentifier());
                    break;
                case GAME_WIN_VERTICAL:
                    Log.i(LOG_TAG, "Vertical win for Player #" +gameResult.getPlayer()+ "on column "+gameResult.getIdentifier());
                    break;
                case GAME_WIN_DIAGONAL:
                    Log.i(LOG_TAG, "Diagonal win for Player #" +gameResult.getPlayer()+ "from square id="+gameResult.getIdentifier());
                    break;
                default:
                    Log.i(LOG_TAG, "Play On!");
                    break;
            }
            if(gameResult.getCondition() != GameCondition.GAME_CONTINUE) {
                Toast.makeText(MainActivity.this, "WIN!!! for player #" + gameResult.getPlayer(), Toast.LENGTH_SHORT).show();

                View playAgainLayout = findViewById(R.id.playAgainLayout);
                TextView winnerText = (TextView) findViewById(R.id.winnerMessage);

                winnerText.setText("WIN!!! for player #" + gameResult.getPlayer());

                playAgainLayout.setVisibility(View.VISIBLE);
                playAgainLayout.setAlpha(1);

            }

        }

        Log.i(LOG_TAG, "Position: " + pos + " >>> gameState:" + Arrays.toString(gameState));

    }

    protected GameResult getGameResult(int boardSize) {
        GameResult gresult = checkHorizontalWin(boardSize);
        if(gresult.getCondition()==GameCondition.GAME_CONTINUE) {
            gresult = checkVerticalWin(boardSize);
            if(gresult.getCondition()==GameCondition.GAME_CONTINUE) {
                gresult = checkDiagonalWin(boardSize);
            }
        }
        return gresult;
    }

    protected GameResult checkHorizontalWin(int boardSize) {
        int gameScore = 0;
        int rowNum = 0;
        for( int i=0; i<boardSize; i++ ) {
            int j = i * boardSize;
            gameScore  = gameState[j];
            for ( int k=0; k<boardSize; k++ ) {
                gameScore  = gameScore  & gameState[k+j];
                //Log.i(LOG_TAG, "HHH CHECKING SQUARE# "+(k+j));
                if(gameScore ==0) {
                    break;
                }
            }
            if(gameScore >0) {
                rowNum = i;
                break;
            }
        }
        if (gameScore  > 0) {
            return new GameResult(GameCondition.GAME_WIN_HORIZONTAL, gameScore , rowNum);
        } else {
            return new GameResult(GameCondition.GAME_CONTINUE);
        }
    }

    protected GameResult checkVerticalWin(int boardSize) {
        int gameScore = 0;
        int colNum = 0;


        for( int i=0; i<boardSize; i++ ) {
            Log.i(LOG_TAG, "i >>>>>>>>>>>>>>>>>>>>>>>> " +i);
            gameScore = gameState[i*boardSize];


            for ( int j=0; j<boardSize; j++ ) {
                Log.i(LOG_TAG, "@>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+ i+","+j);

                gameScore  = gameScore & gameState[i+(j*boardSize)];

                Log.i(LOG_TAG, "VVV CHECKING SQUARE# x="+i+" y="+j+" pos="+(i+(j*boardSize)) +"  val="+gameState[i+(j*boardSize)]);

                if(gameScore ==0) {
                //    break;
                }
            }
            if(gameScore > 0) {
                colNum = i;
                Log.i(LOG_TAG, "LOOK YOU WON THE FUCKING GAME ALREADY!!!");
                //break;
            }

        }
        Log.i(LOG_TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< "+gameScore);

        if (gameScore  > 0) {
            return new GameResult(GameCondition.GAME_WIN_VERTICAL, gameScore , colNum);
        } else {
            return new GameResult(GameCondition.GAME_CONTINUE);
        }
    }

    protected GameResult checkDiagonalWin(int boardSize) {
        int gameScoreLR = gameState[0];
        int gameScoreRL = gameState[boardSize-1];
        int cornerNum = 0;
        for( int i=0; i<boardSize; i++ ){
            gameScoreLR = gameScoreLR & gameState[i+(i*boardSize)];
            gameScoreRL = gameScoreRL & gameState[(i*(boardSize-1))+(boardSize-1)];
            if( gameScoreLR==0 && gameScoreRL==0 ) {
                cornerNum = i;
                break;
            }
        }
        if (gameScoreLR  > 0) {
            return new GameResult(GameCondition.GAME_WIN_DIAGONAL, gameScoreLR, cornerNum);
        } else if ( gameScoreRL > 0 ) {
            return new GameResult(GameCondition.GAME_WIN_DIAGONAL, gameScoreRL, cornerNum);
        } else {
            return new GameResult(GameCondition.GAME_CONTINUE);
        }
    }


    public static enum GameCondition {
        GAME_CONTINUE,
        GAME_WIN_HORIZONTAL,
        GAME_WIN_VERTICAL,
        GAME_WIN_DIAGONAL
    }

    public class GameResult {
        private int mPlayer = 0;
        private int mIdentifier = 0;
        private GameCondition mCondition;

        public GameResult(GameCondition condition){
            mCondition = condition;
            if(condition!=GameCondition.GAME_CONTINUE) {
                throw new InstantiationError("GameResult must contain an identifier for a win.");
            }
        }
        public GameResult(GameCondition condition, int player, int identifier) {
            mCondition = condition;
            mPlayer = player;
            mIdentifier = identifier;
        }

        public GameCondition getCondition() {
            return mCondition;
        }
        public int getPlayer() {
            return mPlayer;
        }
        public int getIdentifier() {
            return mIdentifier;
        }

    }

}
