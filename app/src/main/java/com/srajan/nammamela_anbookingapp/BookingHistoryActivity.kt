package com.srajan.nammamela_anbookingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class BookingHistoryActivity : AppCompatActivity() {

    data class Booking(
        val id: String,
        val drama: String,
        val seats: String,
        val date: String,
        val time: String,
        val venue: String,
        val amount: Int,
        val user: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_history)

        val container =
            findViewById<LinearLayout>(R.id.bookingsContainer)

        val emptyState =
            findViewById<TextView>(R.id.historyText)

        val bookings = loadBookings()

        if (bookings.isEmpty()) {

            emptyState.visibility = View.VISIBLE

        } else {

            emptyState.visibility = View.GONE

            bookings.forEach { booking ->

                val card = createBookingCard(booking)

                container.addView(
                    card,
                    container.childCount - 1
                )
            }
        }
    }

    private fun loadBookings(): List<Booking> {

        val prefs =
            getSharedPreferences(
                "BookingHistory",
                MODE_PRIVATE
            )

        val count =
            prefs.getInt("booking_count", 0)

        val bookings = mutableListOf<Booking>()

        for (i in 0 until count) {

            val booking = Booking(
                id = prefs.getString(
                    "booking_${i}_id",
                    ""
                ) ?: "",

                drama = prefs.getString(
                    "booking_${i}_drama",
                    ""
                ) ?: "",

                seats = prefs.getString(
                    "booking_${i}_seats",
                    ""
                ) ?: "",

                date = prefs.getString(
                    "booking_${i}_date",
                    ""
                ) ?: "",

                time = prefs.getString(
                    "booking_${i}_time",
                    ""
                ) ?: "",

                venue = prefs.getString(
                    "booking_${i}_venue",
                    ""
                ) ?: "",

                amount = prefs.getInt(
                    "booking_${i}_amount",
                    0
                ),

                user = prefs.getString(
                    "booking_${i}_user",
                    ""
                ) ?: ""
            )

            bookings.add(booking)
        }

        return bookings.reversed()
    }

    private fun createBookingCard(
        booking: Booking
    ): View {

        val inflater = LayoutInflater.from(this)

        val cardView =
            inflater.inflate(
                R.layout.item_booking_card,
                null
            ) as CardView

        val poster =
            cardView.findViewById<ImageView>(R.id.cardPoster)

        when (booking.drama) {

            "Vaali Nataka" -> {
                poster.setImageResource(R.drawable.vaali_nataka)
            }

            "Mahishasura Mardini" -> {
                poster.setImageResource(R.drawable.drama_poster)
            }

            "Kamsa Vadhe" -> {
                poster.setImageResource(R.drawable.kansavadhe)
            }
        }

        cardView.findViewById<TextView>(R.id.cardGenre)
            .text = "MYTHOLOGICAL"

        cardView.findViewById<TextView>(R.id.cardTitle)
            .text = booking.drama

        cardView.findViewById<TextView>(R.id.cardTime)
            .text = "${booking.date} · ${booking.time}"

        cardView.findViewById<TextView>(R.id.cardVenue)
            .text = booking.venue

        cardView.findViewById<TextView>(R.id.cardSeats)
            .text = "Seat ${booking.seats}"

        cardView.findViewById<TextView>(R.id.cardBookingId)
            .text = booking.id

        cardView.findViewById<TextView>(R.id.cardAmount)
            .text = "₹${booking.amount}"

        return cardView
    }
}