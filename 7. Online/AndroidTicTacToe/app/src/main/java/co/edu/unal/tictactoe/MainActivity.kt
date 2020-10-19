package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private var mGame: TicTacToeGame = TicTacToeGame()
    private var mGameOver = false
    private var sessionId = 0
    private var players = 0
    private var playerTurn = 1
    private var playersOnline = 0

    // Buttons making up the board
    private var mBoardButtons: Array<Button?> = arrayOfNulls(mGame.BOARD_SIZE)
    private var mBoardView: BoardView? = null
    var mHumanMediaPlayer: MediaPlayer? = null
    var mComputerMediaPlayer: MediaPlayer? = null

    inner class mTouchListener : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val col = (mBoardView?.getBoardCellWidth()?.let { event?.x?.div(it) })?.toInt()
            val row = (mBoardView?.getBoardCellHeight()?.let { event?.y?.div(it) })?.toInt()
            val pos: Int = (row!! * 3 + col!!)
            var done = false
            if (players == playerTurn && playersOnline == 2) {
                var icon = 'H';
                if (players == 1) {
                    icon = mGame.HUMAN_PLAYER
                } else {
                    icon = mGame.COMPUTER_PLAYER
                }
                if (!mGameOver && setMove(icon, pos)) {
                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("games")
                    myRef.child(sessionId.toString()).child("board")
                        .setValue(String(mGame.mBoard).split("").slice(1..9) as List<String>)
                    var winner: Int = mGame.checkForWinner()
                    if (winner == 1) {
                        myRef.child(sessionId.toString()).child("message").setValue("It's a tie!")
                        mGame.ties()
                        myRef.child(sessionId.toString()).child("scores").child("0")
                            .setValue(mGame.ties)
                        myRef.child(sessionId.toString()).child("winner").setValue(true)
                    } else if (winner == 2) {
                        var defaultMessage = resources.getString(R.string.result_human_wins)
                        mInfoTextView?.text = mPrefs.getString("victory_message", defaultMessage)
                        mGame.humanWin()
                        myRef.child(sessionId.toString()).child("scores").child("1")
                            .setValue(mGame.humanWins)
                        myRef.child(sessionId.toString()).child("winner").setValue(true)
                    } else if (winner == 3) {
                        var defaultMessage = resources.getString(R.string.result_human_wins)
                        mInfoTextView?.text = mPrefs.getString("victory_message", defaultMessage)
                        myRef.child(sessionId.toString()).child("winner").setValue(true)
                        mGame.androidWin()
                        myRef.child(sessionId.toString()).child("scores").child("2")
                            .setValue(mGame.androidWins)
                    }

                    if (winner == 0) {
                        mInfoTextView?.text = "Other Player's turn."
                    }
                    changeTurn()

                }
            }
            return false
        }
    }

    // Various text displayed
    private var mInfoTextView: TextView? = null
    lateinit var mPrefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mInfoTextView = findViewById<TextView>(R.id.information)
        sessionId = intent.getIntExtra("session", 0)
        players = intent.getIntExtra("players", 0)
        val title = intent.getStringExtra("title")
        var gameTitle = findViewById<TextView>(R.id.game_title)
        gameTitle.text = title
        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.board)
        mBoardView?.setGame(mGame)
        mBoardView?.setOnTouchListener(mTouchListener());

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mSoundOn = mPrefs.getBoolean("sound", true);
        var difficultyLevel =
            mPrefs.getString("difficulty_level", resources.getString(R.string.difficulty_harder))
        if (difficultyLevel.equals(resources.getString(R.string.difficulty_easy))) {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
        } else if (difficultyLevel.equals(resources.getString(R.string.difficulty_harder))) {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
        } else {
            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
        }

        startNewGame()

    }

    fun validateWinner(ties: Int, player1: Int, player2: Int) {
        if (playersOnline != 2) {

            mInfoTextView?.text = "Waiting Other Player to join"


        } else {
            if (players == playerTurn) {
                mInfoTextView?.text = "You Turn."
            } else {
                mInfoTextView?.text = "It's other player's turn."
            }
        }
        var human_score = findViewById<TextView>(R.id.human_score)
        var ties_score = findViewById<TextView>(R.id.ties_score)
        var android_score = findViewById<TextView>(R.id.android_score)
        if (players == 1) {
            human_score.text = "You: " + player1
            ties_score.text = "Ties: " + ties
            android_score.text = "Other Player: " + player2
        } else {
            human_score.text = "You: " + player2
            ties_score.text = "Ties: " + ties
            android_score.text = "Other Player: " + player1
            mInfoTextView?.text
        }
        var winner: Int = mGame.checkForWinner()
        if (winner == 1) {
            mInfoTextView?.text = "Tie!"
        } else if (winner == 2 && players == 1) {
            mInfoTextView?.text = "You Win"
        } else if (winner == 3 && players == 1) {
            mInfoTextView?.text = "Other Player's Wins."
        } else if (winner == 2 && players == 2) {
            mInfoTextView?.text = "Other Player's Wins."
        } else if (winner == 3 && players == 2) {
            mInfoTextView?.text = "You Win"
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("session")
        myRef.child("1").child(sessionId.toString()).setValue(players - 1)
    }

    private fun startNewGame(reset: Boolean = false) {
        mGame.humanStart = !mGame.humanStart
        mGame.clearBoard()
        mBoardView?.invalidate()
        loadGame(reset)

    }

    fun loadGame(reset: Boolean) {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("games")
        val myRef2 = database.getReference("session")

        myRef2.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if (dataSnapshot.value != null) {
                    val value =
                        dataSnapshot.value!! as MutableList<*>

                    var playersOline = value[1] as MutableList<Int>
                    playersOnline = playersOline[sessionId]
                    if (playersOnline != 2) {

                        mInfoTextView?.text = "Waiting Other Player to join"


                    }else {
                        if (players == playerTurn) {
                            mInfoTextView?.text = "You Turn."
                        } else {
                            mInfoTextView?.text = "It's other player's turn."
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                println("Failed to read value.")
            }
        })
        if (reset) {
            var board = String(mGame.mBoard)
            myRef.child(sessionId.toString()).setValue(
                mapOf(
                    "session" to sessionId,
                    "board" to board.split("").slice(1..9) as List<String>,
                    "player_turn" to players,
                    "scores" to mutableListOf(mGame.ties, mGame.humanWins, mGame.androidWins),
                    "winner" to false,
                    "message" to ""
                )
            );
            mBoardView?.invalidate()
        } else {

            //myRef.setValue(mutableListOf(games as List<String>, players as List<Long>, imageid as List<Int>));
            // Read from the database
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.value != null && dataSnapshot.child(sessionId.toString())
                            .exists()
                    ) {

                        val value =
                            dataSnapshot.value!! as MutableList<*>

                        var game = value[sessionId] as Map<*, *>
                        var board = game["board"] as List<String>
                        var scores = game["scores"] as List<Int>
                        var player = game["player_turn"] as Long
                        var winner = game["winner"] as Boolean
                        var boardCharts = board.joinToString("").toCharArray()
                        mGame.mBoard = boardCharts
                        playerTurn = player.toInt()
                        mGame.humanStart = player.toInt() == playerTurn
                        mGameOver = winner
                        mBoardView?.invalidate()
                        validateWinner(scores[0], scores[1], scores[2])
                        mGame.androidWins = scores[2]
                        mGame.humanWins = scores[1]
                        mGame.ties = scores[0]
                    } else {
                        var board = String(mGame.mBoard)
                        myRef.child(sessionId.toString()).setValue(
                            mapOf(
                                "session" to sessionId,
                                "board" to board.split("").slice(1..9) as List<String>,
                                "player_turn" to players,
                                "scores" to mutableListOf(0, 0, 0),
                                "winner" to false,
                                "message" to ""
                            )
                        );
                        mBoardView?.invalidate()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    println("Failed to read value.")
                }
            })
        }
    }

    fun changeTurn() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("games")
        if (players == 1) {
            myRef.child(sessionId.toString()).child("player_turn").setValue(2)
        } else {
            myRef.child(sessionId.toString()).child("player_turn").setValue(1)
        }
    }

    private fun setMove(player: Char, location: Int): Boolean {
        if (mGame.HUMAN_PLAYER == player && mSoundOn) {
            mComputerMediaPlayer?.start()
        }
        if (mGame.setMove(player, location)) {
            mBoardView?.invalidate()
            return true;
        }
        return false;
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    val DIALOG_QUIT_ID = 1

    fun createSimpleDialog(id: Int): AlertDialog {

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        when (id) {
            DIALOG_QUIT_ID -> {
                builder.setMessage(R.string.quit_question).setCancelable(false).setPositiveButton(
                    R.string.yes,
                    DialogInterface.OnClickListener { dialog, id -> this.finish() })
                    .setNegativeButton(R.string.no, null)
            }
        }

        return builder.create()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.new_game -> {
                startNewGame(true)
            }
            R.id.ai_difficulty -> {
                startActivityForResult(Intent(this, Settings::class.java), 0)
                return true
            }
            R.id.quit -> {
                createSimpleDialog(DIALOG_QUIT_ID).show()
                return true
            }
        }
        return true
    }

    var mSoundOn: Boolean = false
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_CANCELED) {
            // Apply potentially new settings
            mSoundOn = mPrefs.getBoolean("sound", true);
            var difficultyLevel = mPrefs.getString(
                "difficulty_level",
                resources.getString(R.string.difficulty_harder)
            )
            if (difficultyLevel.equals(resources.getString(R.string.difficulty_easy))) {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
            } else if (difficultyLevel.equals(resources.getString(R.string.difficulty_harder))) {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
            } else {
                mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mHumanMediaPlayer = MediaPlayer.create(this, R.raw.human_sound)
        mComputerMediaPlayer = MediaPlayer.create(this, R.raw.android_sound)


    }

    override fun onPause() {
        super.onPause()
        mHumanMediaPlayer?.release()
        mComputerMediaPlayer?.release()
    }
}