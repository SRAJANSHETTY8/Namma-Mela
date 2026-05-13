package com.srajan.nammamela_anbookingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class FanWallActivity : AppCompatActivity() {

    data class ApplauseItem(
        val name: String,
        val email: String,
        val comment: String,
        val timeAgo: String,
        val initial: String,
        val timestamp: Long = System.currentTimeMillis()
    )

    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var commentCount: TextView
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var commentInput: EditText
    private lateinit var postBtn: Button

    private val gson = Gson()
    private var applauseList = mutableListOf<ApplauseItem>()

    companion object {
        const val PREFS_NAME = "FanWallData"
        const val KEY_APPLAUSE_LIST = "applause_list"

        fun getApplauseList(context: Context): List<ApplauseItem> {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(KEY_APPLAUSE_LIST, null)
            return if (json != null) {
                val type = object : TypeToken<List<ApplauseItem>>() {}.type
                Gson().fromJson(json, type)
            } else {
                emptyList()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_wall)

        initViews()
        loadComments()
        setupPostButton()
    }

    private fun initViews() {
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        commentInput = findViewById(R.id.commentInput)
        postBtn = findViewById(R.id.postBtn)
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView)
        emptyState = findViewById(R.id.emptyCommentCard)
        commentCount = findViewById(R.id.commentCount)

        commentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadComments() {
        applauseList = getApplauseList(this).toMutableList()
        updateUI()
    }

    private fun updateUI() {
        commentCount.text = "${applauseList.size} applause"

        if (applauseList.isEmpty()) {
            commentsRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            commentsRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            commentsRecyclerView.adapter = ApplauseAdapter(applauseList)
        }
    }

    private fun setupPostButton() {
        postBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val comment = commentInput.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (comment.isEmpty()) {
                Toast.makeText(this, "Enter your comment", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create new applause item
            val initial = name.take(1).uppercase()
            val timeAgo = "Just now"

            val newApplause = ApplauseItem(
                name = name,
                email = email,
                comment = comment,
                timeAgo = timeAgo,
                initial = initial
            )

            // Add to list (newest first)
            applauseList.add(0, newApplause)

            // Save to SharedPreferences
            saveApplauseList()

            // Update UI
            updateUI()

            // Clear inputs
            nameInput.text.clear()
            emailInput.text.clear()
            commentInput.text.clear()

            Toast.makeText(this, "Applause posted! 👏", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveApplauseList() {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(applauseList)
        prefs.edit().putString(KEY_APPLAUSE_LIST, json).apply()
    }

    // Adapter for RecyclerView
    inner class ApplauseAdapter(private val items: List<ApplauseItem>) :
        RecyclerView.Adapter<ApplauseAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val avatar: TextView = view.findViewById(R.id.itemApplauseAvatar)
            val name: TextView = view.findViewById(R.id.itemApplauseName)
            val time: TextView = view.findViewById(R.id.itemApplauseTime)
            val comment: TextView = view.findViewById(R.id.itemApplauseComment)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_fanwall_applause, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.avatar.text = item.initial
            holder.name.text = item.name
            holder.time.text = item.timeAgo
            holder.comment.text = item.comment
        }

        override fun getItemCount() = items.size
    }
}