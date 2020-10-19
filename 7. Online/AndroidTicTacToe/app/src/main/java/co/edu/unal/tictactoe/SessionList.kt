package co.edu.unal.tictactoe

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SessionList : AppCompatActivity() {

    private var listView: ListView? = null
    private var games = mutableListOf<String>()

    private var players = mutableListOf<Int>()


    private var imageid = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session_list)
        loadSessions()
        val button: (FloatingActionButton)  = findViewById(R.id.create_session)
        button.setOnClickListener(View.OnClickListener {
            Toast.makeText(applicationContext, "New", Toast.LENGTH_SHORT)
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("session")
            if(games[0] == "No hay partidas disponibles"){
                games = mutableListOf<String>()
                players = mutableListOf<Int>()
                imageid = mutableListOf<Int>()
            }
            games.add("New Game " + (games.size+1))
            players.add(0)
            imageid.add(R.drawable.human)
            myRef.setValue(mutableListOf(games as List<String>, players as List<Int>, imageid as List<Int>));
        })

    }


    fun createList(){
        val customList = CustomList(this, games, players, imageid)

        listView = findViewById(R.id.listView)
        listView!!.adapter = customList

        listView!!.onItemClickListener = OnItemClickListener { adapterView, view, i, l ->
            if(players[i].compareTo(2) != 0){
                players[i] = players[i]+1
                val database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("session")
                myRef.setValue(mutableListOf(games as List<String>, players as List<Long>, imageid as List<Int>));
                val intent = Intent(baseContext, MainActivity::class.java)
                intent.putExtra("session", i)
                intent.putExtra("title", games[i])
                intent.putExtra("players", players[i])
                startActivity(intent)
            }else{
                Toast.makeText(getApplicationContext(),"You Clicked "+games[i] + "players completed",Toast.LENGTH_SHORT).show();
            }

        }
    }
    fun loadSessions(){
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("session")
        //myRef.setValue(mutableListOf(games as List<String>, players as List<Long>, imageid as List<Int>));
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.value != null){
                val value =
                    dataSnapshot.value!! as MutableList<*>

                    games = value[0] as MutableList<String>
                    players = value[1] as MutableList<Int>
                    imageid = value[2] as MutableList<Int>
                }else{
                    games = mutableListOf("No hay partidas disponibles")
                    players = mutableListOf(0)
                    imageid = mutableListOf(R.drawable.quit_game)
                }

                createList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                println( "Failed to read value.")
            }
        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.new_game -> {
                startActivity(Intent(this, MainActivity::class.java))
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
    override fun onDestroy() {
        super.onDestroy()
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("session")
        val myRefGames = database.getReference("games")
        myRef.setValue(null)
        myRefGames.setValue(null)
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

}