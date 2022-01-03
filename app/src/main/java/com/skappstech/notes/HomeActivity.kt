package com.skappstech.notes

import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.ConnectivityManager
import android.opengl.Visibility
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import androidx.appcompat.widget.SearchView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlin.collections.ArrayList
import android.widget.Toast

import android.net.NetworkInfo
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi


class HomeActivity : AppCompatActivity(),NoteAdapter.MyOnClickListener {

    private lateinit var database : DatabaseReference
    private lateinit var dataRecyclerview : RecyclerView
    private lateinit var dataArrayList : ArrayList<Data>
    private lateinit var tempArrayList : ArrayList<Data>
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private var mAuth: FirebaseAuth? = null
    private lateinit var auth: FirebaseAuth
    private var onlineUserId = ""
    private lateinit var rvSearchView : SearchView

    private lateinit var loadCreate: TextView
    private lateinit var pbload : ProgressBar
    private lateinit var noInternet : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val fab = findViewById<FloatingActionButton>(R.id.fab_add_note)
        auth = FirebaseAuth.getInstance()

        fab.setOnClickListener{
            startActivity(Intent(this,CreateNoteActivity::class.java))
        }


        loadCreate = findViewById(R.id.load_create)
        pbload = findViewById(R.id.pbload)
        noInternet = findViewById(R.id.tv_internet)

        loadCreate.visibility = View.GONE
        pbload.visibility = View.VISIBLE

        rvSearchView = findViewById(R.id.rv_searchView)
        rvSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               tempArrayList.clear()
               val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    dataArrayList.forEach {
                        if (it.title!!.lowercase(Locale.getDefault()).contains(searchText) ||
                            it.note!!.lowercase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                            dataRecyclerview.adapter = NoteAdapter(tempArrayList,this@HomeActivity)
                            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                            dataRecyclerview.layoutManager = staggeredGridLayoutManager
                        }
                    }
                    dataRecyclerview.adapter!!.notifyDataSetChanged()
                }else{
                    tempArrayList.clear()
                    tempArrayList.addAll(dataArrayList)
                    dataRecyclerview.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_auth))
            .requestEmail()
            .build()
        mGoogleSignInClient= GoogleSignIn.getClient(this,gso)


        dataRecyclerview = findViewById(R.id.recyclerview)
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        dataRecyclerview.layoutManager = staggeredGridLayoutManager
        dataRecyclerview.setHasFixedSize(true)
        dataArrayList = arrayListOf()
        tempArrayList = arrayListOf()
        getUserData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.logout -> {
                        mGoogleSignInClient.signOut().addOnCompleteListener {
                        val intent= Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun getUserData() {

        mAuth = FirebaseAuth.getInstance()
        onlineUserId = mAuth!!.currentUser?.uid.toString()
        database = FirebaseDatabase.getInstance().reference.child("note").child(onlineUserId)
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (userSnapshot in snapshot.children){
                        val data = userSnapshot.getValue(Data::class.java)
                        dataArrayList.add(0,data!!)
                    }
                    pbload.visibility = View.GONE
                    noInternet.visibility = View.GONE
                    dataRecyclerview.adapter = NoteAdapter(dataArrayList,this@HomeActivity)
                }else{
                    loadCreate.visibility = View.VISIBLE
                    pbload.visibility = View.GONE
                    noInternet.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    override fun onClick(position: Int) {
        val intent = Intent(this@HomeActivity,UpdateNoteActivity::class.java)
                intent.putExtra("title",dataArrayList[position].title)
                intent.putExtra("note",dataArrayList[position].note)
                intent.putExtra("id",dataArrayList[position].id)
                startActivity(intent)
    }


}