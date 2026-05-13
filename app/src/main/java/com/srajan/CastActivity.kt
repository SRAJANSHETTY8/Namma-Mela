package com.srajan.nammamela_anbookingapp

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class CastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cast)

        val container = findViewById<LinearLayout>(R.id.castContainer)

        // Collect all cast cards (CardViews) from the container
        val cards = mutableListOf<CardView>()
        var i = 0
        while (i < container.childCount) {
            val child = container.getChildAt(i)
            if (child is CardView) {
                cards.add(child)
            }
            i++
        }

        // Remove all cards
        cards.forEach { container.removeView(it) }

        // Shuffle and add back in random order
        cards.shuffled().forEach { card ->
            container.addView(card)
        }
    }
}