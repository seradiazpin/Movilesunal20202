package co.edu.unal.tictactoe

import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var mGame: TicTacToeGame = TicTacToeGame()
    private var mGameOver = false
    // Buttons making up the board
    private var mBoardButtons: Array<Button?> = arrayOfNulls(mGame.BOARD_SIZE)
    private var mBoardView: BoardView? = null
    var mHumanMediaPlayer: MediaPlayer? = null
    var mComputerMediaPlayer: MediaPlayer? = null

    inner class mTouchListener : View.OnTouchListener{
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val col = (mBoardView?.getBoardCellWidth()?.let { event?.x?.div(it) })?.toInt()
            val row = (mBoardView?.getBoardCellHeight()?.let { event?.y?.div(it) })?.toInt()
            val pos: Int = (row!! * 3 + col!!)
            var done = false

            if(!mGameOver && setMove(mGame.HUMAN_PLAYER, pos)) {
                var winner: Int = mGame.checkForWinner()
                if (winner == 0) {
                    mInfoTextView?.text = "It's Android's turn."
                    val move: Int = mGame.getComputerMove()
                    setMove(mGame.COMPUTER_PLAYER, move)
                    winner = mGame.checkForWinner()
                }
                if (winner == 0) mInfoTextView?.text = "It's your turn."
                else if (winner == 1) {
                    mInfoTextView?.text = "It's a tie!"
                    mGame.ties()
                    mGameOver = true
                    findViewById<TextView>(R.id.ties_score).text = "Ties: " + mGame.ties.toString()
                } else if (winner == 2) {
                    mInfoTextView?.text = "You won!"
                    mGame.humanWin()
                    mGameOver = true
                    findViewById<TextView>(R.id.human_score).text =
                        "Human: " + mGame.humanWins.toString()
                } else {
                    mInfoTextView?.text = "Android won!"
                    mGame.androidWin()
                    mGameOver = true
                    findViewById<TextView>(R.id.android_score).text =
                        "Android: " + mGame.androidWins.toString()
                }
            }

            return false
        }
    }
    // Various text displayed
    private var mInfoTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mInfoTextView = findViewById<TextView>(R.id.information)

        mGame = TicTacToeGame()
        mBoardView = findViewById(R.id.board)
        mBoardView?.setGame(mGame)
        mBoardView?.setOnTouchListener(mTouchListener());
        startNewGame()

    }
    private fun startNewGame(){
        mGame.humanStart = !mGame.humanStart
        mGame.clearBoard()
        mBoardView?.invalidate()
        mGameOver = false
        if(!mGame.humanStart){
            mInfoTextView?.text = "It's Android's turn."
            val move: Int = mGame.getComputerMove()
            setMove(mGame.COMPUTER_PLAYER, move)
            mInfoTextView?.text = "It's your turn."
        }else{
            mInfoTextView?.text = "You go first."
        }
    }
    private fun setMove(player: Char, location: Int):Boolean {
        if(mGame.HUMAN_PLAYER == player){
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
        inflater.inflate(R.menu.options_menu,menu)
        return true
    }
    val DIALOG_DIFFICULTY_ID = 0
    val DIALOG_QUIT_ID = 1

    fun createSimpleDialog(id:Int): AlertDialog {
        val levels = arrayOf<CharSequence>(
            resources.getString(R.string.difficulty_easy),
            resources.getString(R.string.difficulty_harder),
            resources.getString(R.string.difficulty_expert)
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        var selected:Int = 0
        when(mGame.getDifficultyLevel()){
            TicTacToeGame.DifficultyLevel.Easy->{
                selected = 0
            }
            TicTacToeGame.DifficultyLevel.Harder->{
                selected = 1
            }
            TicTacToeGame.DifficultyLevel.Expert->{
                selected = 2
            }

        }
        when (id) {
            DIALOG_DIFFICULTY_ID ->{
                builder.setTitle("Choose difficulty")
                builder.setSingleChoiceItems(levels, selected){ dialog, item ->
                    when(item){
                        0 ->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Easy)
                        }
                        1 ->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Harder)
                        }
                        2 ->{
                            mGame.setDifficultyLevel(TicTacToeGame.DifficultyLevel.Expert)
                        }
                    }
                    // Display the selected difficulty level
                    Toast.makeText(
                        applicationContext, levels[item],
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            DIALOG_QUIT_ID ->{
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
            R.id.new_game ->{
                startNewGame()
            }
            R.id.ai_difficulty ->{
                createSimpleDialog(DIALOG_DIFFICULTY_ID).show()
                startNewGame()
                return true
            }
            R.id.quit ->{
                createSimpleDialog(DIALOG_QUIT_ID).show()
                return true
            }
        }
        return true
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