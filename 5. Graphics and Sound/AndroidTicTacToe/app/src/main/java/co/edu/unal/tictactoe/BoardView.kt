package co.edu.unal.tictactoe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class BoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mHumanBitmap: Bitmap? = null
    private var mComputerBitmap: Bitmap? = null
    val GRID_WIDTH = 6
    private var mPaint: Paint? = null
    init {
        initialize();
    }

    private var mGame: TicTacToeGame? = null

    fun setGame(game: TicTacToeGame?) {
        mGame = game
    }
    fun getBoardCellWidth(): Int {
        return width / 3
    }

    fun getBoardCellHeight(): Int {
        return height / 3
    }

    fun initialize() {
        mHumanBitmap = BitmapFactory.decodeResource(resources, R.drawable.human)
        mComputerBitmap = BitmapFactory.decodeResource(resources, R.drawable.difficulty_level)
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Determine the width and height of the View
        val boardWidth = width
        val boardHeight = height

        // Make thick, light gray lines
        mPaint!!.color = Color.LTGRAY
        mPaint!!.strokeWidth = GRID_WIDTH.toFloat()

        // Draw the two vertical board lines
        val cellWidth = boardWidth / 3.0f
        canvas.drawLine(cellWidth, 0f, cellWidth, boardHeight.toFloat(), mPaint!!)
        canvas.drawLine(cellWidth * 2, 0f, cellWidth * 2, boardHeight.toFloat(), mPaint!!)

        // Draw the two horizontal board lines
        val cellHeight = boardHeight / 3.0f

        canvas.drawLine(0f,cellHeight, boardHeight.toFloat(),cellHeight, mPaint!!)
        canvas.drawLine(0f,cellHeight * 2, boardHeight.toFloat(), cellHeight * 2, mPaint!!)

        for (i in 0 until (mGame?.BOARD_SIZE ?: 0)){
            var col = i % 3
            var row = i / 3

            // Define the boundaries of a destination rectangle for the image
            var left = col * cellWidth
            var top = row *cellHeight
            var right = (col+1) * cellWidth
            var bottom = (row+1) * cellHeight
            println("-----------------------------------------")
            println(left)
            if (mGame != null && mGame!!.getBoardOccupant(i) == mGame!!.HUMAN_PLAYER) {
                mHumanBitmap?.let {
                    canvas.drawBitmap(
                        it,
                        null,  // src
                        Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt()),  // dest
                        null)
                };
            }
            else if (mGame != null && mGame!!.getBoardOccupant(i) == mGame!!.COMPUTER_PLAYER) {
                mComputerBitmap?.let {
                    canvas.drawBitmap(
                        it,
                        null,  // src
                        Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt()),  // dest
                        null)
                };
            }
        }

    }

}