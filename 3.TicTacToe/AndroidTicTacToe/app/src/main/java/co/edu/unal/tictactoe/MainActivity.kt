package co.edu.unal.tictactoe

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
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
        menu.add("New Game")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mGame.humanStart = !mGame.humanStart
        startNewGame()
        return true
    }

}