package co.edu.unal.tictactoe

import java.util.*
import kotlin.random.Random

class TicTacToeGame {

    private val mBoard = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
    public val BOARD_SIZE = 9
    public var androidWins = 0
    public var humanWins = 0
    public var humanStart = false
    public var ties = 0
    val HUMAN_PLAYER = 'X'
    val COMPUTER_PLAYER = 'O'
    val OPEN_SPOT = ' '
    private var mRand: Random = Random

    private fun displayBoard() {
        println()
        println(mBoard[0].toString() + " | " + mBoard[1] + " | " + mBoard[2])
        println("-----------")
        println(mBoard[3].toString() + " | " + mBoard[4] + " | " + mBoard[5])
        println("-----------")
        println(mBoard[6].toString() + " | " + mBoard[7] + " | " + mBoard[8])
        println()
    }

    public fun humanWin(){
        this.humanWins++;
    }
    public fun androidWin(){
        this.androidWins++;
    }
    public fun ties(){
        this.ties++;
    }
    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won
    public fun checkForWinner(): Int {

        // Check horizontal wins
        run {
            var i = 0
            while (i <= 6) {
                if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 1] == HUMAN_PLAYER && mBoard[i + 2] == HUMAN_PLAYER
                ) return 2
                if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 1] == COMPUTER_PLAYER && mBoard[i + 2] == COMPUTER_PLAYER
                ) return 3
                i += 3
            }
        }

        // Check vertical wins
        for (i in 0..2) {
            if (mBoard[i] == HUMAN_PLAYER && mBoard[i + 3] == HUMAN_PLAYER && mBoard[i + 6] == HUMAN_PLAYER
            ) return 2
            if (mBoard[i] == COMPUTER_PLAYER && mBoard[i + 3] == COMPUTER_PLAYER && mBoard[i + 6] == COMPUTER_PLAYER
            ) return 3
        }

        // Check for diagonal wins
        if (mBoard[0] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[8] == HUMAN_PLAYER ||
            mBoard[2] == HUMAN_PLAYER && mBoard[4] == HUMAN_PLAYER && mBoard[6] == HUMAN_PLAYER
        ) return 2
        if (mBoard[0] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[8] == COMPUTER_PLAYER ||
            mBoard[2] == COMPUTER_PLAYER && mBoard[4] == COMPUTER_PLAYER && mBoard[6] == COMPUTER_PLAYER
        ) return 3

        // Check for tie
        for (i in 0 until BOARD_SIZE) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) return 0
        }

        // If we make it through the previous loop, all places are taken, so it's a tie
        return 1
    }

    public fun getComputerMove():Int {
        var move: Int

        // First see if there's a move O can make to win
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i]
                mBoard[i] = COMPUTER_PLAYER
                if (checkForWinner() == 3) {
                    println("Computer is moving to " + (i + 1))
                    return i
                } else mBoard[i] = curr
            }
        }

        // See if there's a move O can make to block X from winning
        for (i in 0 until BOARD_SIZE) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                val curr = mBoard[i] // Save the current number
                mBoard[i] = HUMAN_PLAYER
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER
                    println("Computer is moving to " + (i + 1))
                    return i
                } else mBoard[i] = curr
            }
        }

        // Generate random move
        do {
            move = mRand.nextInt(BOARD_SIZE)
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER)
        println("Computer is moving to " + (move + 1))
        mBoard[move] = COMPUTER_PLAYER
        return move
    }

    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public fun clearBoard(){
        var reset = charArrayOf('1', '2', '3', '4', '5', '6', '7', '8', '9')
        for (i in 0 until BOARD_SIZE){
            mBoard[i] = reset[i]
        }
    }

    /** Set the given player at the given location on the game board.
     *  The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public fun setMove(player: Char, location:Int){
        if(mBoard[location] != HUMAN_PLAYER && mBoard[location] != COMPUTER_PLAYER) {
            if (player == HUMAN_PLAYER)
                mBoard[location] = HUMAN_PLAYER
            if (player == COMPUTER_PLAYER)
                mBoard[location] = COMPUTER_PLAYER
        }
    }



}