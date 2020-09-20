package co.edu.unal.tictactoe

import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private var mGame: TicTacToeGame = TicTacToeGame()

    // Buttons making up the board
    private var mBoardButtons: Array<Button?> = arrayOfNulls(mGame.BOARD_SIZE)

    // Various text displayed
    private var mInfoTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mBoardButtons[0] = findViewById<Button>(R.id.one)
        mBoardButtons[1] = findViewById<Button>(R.id.two)
        mBoardButtons[2] = findViewById<Button>(R.id.three)
        mBoardButtons[3] = findViewById<Button>(R.id.four)
        mBoardButtons[4] = findViewById<Button>(R.id.five)
        mBoardButtons[5] = findViewById<Button>(R.id.six)
        mBoardButtons[6] = findViewById<Button>(R.id.seven)
        mBoardButtons[7] = findViewById<Button>(R.id.eight)
        mBoardButtons[8] = findViewById<Button>(R.id.nine)

        mInfoTextView = findViewById<TextView>(R.id.information)

        mGame = TicTacToeGame()
        startNewGame()

    }
    private fun startNewGame(){
        mGame.humanStart = !mGame.humanStart
        mGame.clearBoard()

        for (i in mBoardButtons.indices) {
            mBoardButtons[i]!!.text = ""
            mBoardButtons[i]!!.isEnabled = true
            mBoardButtons[i]!!.setOnClickListener {
                var winner: Int = mGame.checkForWinner()
                if (mBoardButtons[i]?.isEnabled!! && winner == 0) {
                    setMove(mGame.HUMAN_PLAYER, i)

                    // If no winner yet, let the computer make a move
                    var winner: Int = mGame.checkForWinner()
                    if (winner == 0) {
                        mInfoTextView?.text = "It's Android's turn."
                        val move: Int = mGame.getComputerMove()
                        setMove(mGame.COMPUTER_PLAYER, move)
                        winner = mGame.checkForWinner()
                    }
                    if (winner == 0) mInfoTextView?.text = "It's your turn."
                    else if (winner == 1){
                        mInfoTextView?.text ="It's a tie!"
                        mGame.ties()
                        findViewById<TextView>(R.id.ties_score).text = "Ties: " + mGame.ties.toString()
                    }
                    else if (winner == 2) {
                        mInfoTextView?.text = "You won!"
                        mGame.humanWin()
                        findViewById<TextView>(R.id.human_score).text = "Human: " +mGame.humanWins.toString()
                    }
                    else {mInfoTextView?.text ="Android won!"
                        mGame.androidWin()
                        findViewById<TextView>(R.id.android_score).text = "Android: " +mGame.androidWins.toString()
                    }
                }
            }
        }
        if(!mGame.humanStart){
            mInfoTextView?.text = "It's Android's turn."
            val move: Int = mGame.getComputerMove()
            setMove(mGame.COMPUTER_PLAYER, move)
            mInfoTextView?.text = "It's your turn."
        }else{
            mInfoTextView?.text = "You go first."
        }
    }
    private fun setMove(player: Char, location: Int) {
        mGame.setMove(player, location)
        mBoardButtons[location]!!.isEnabled = false
        mBoardButtons[location]!!.text = player.toString()
        if (player == mGame.HUMAN_PLAYER)
            mBoardButtons[location]?.setTextColor(Color.rgb(0, 200, 0))
        else
            mBoardButtons[location]?.setTextColor(Color.rgb(200, 0, 0))
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

}