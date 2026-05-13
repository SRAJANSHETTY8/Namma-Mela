package com.srajan.nammamela_anbookingapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View // ADD THIS IMPORT
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SeatBookingActivity : AppCompatActivity() {

    data class SeatState(
        val seatId: String,
        var isBooked: Boolean = false,
        var isSelected: Boolean = false
    )

    private lateinit var dramaTitle: String
    private val seatPrice = 150
    private val selectedSeats = mutableListOf<String>()

    private val allSeatIds = listOf(
        "A1", "A2", "A3", "A4", "A5",
        "B1", "B2", "B3", "B4", "B5",
        "C1", "C2", "C3", "C4", "C5",
        "D1", "D2", "D3", "D4", "D5",
        "E1", "E2", "E3", "E4", "E5",
        "F1", "F2", "F3", "F4", "F5"
    )

    private val seatButtons = mutableMapOf<String, Button>()
    private val seatStates = mutableMapOf<String, SeatState>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seat_booking)

        dramaTitle = intent.getStringExtra("drama_title") ?: "Default Drama"
        findViewById<TextView>(R.id.dramaTitleText).text = dramaTitle

        loadSeatStates()
        setupSeatButtons()

        findViewById<Button>(R.id.confirmBtn).setOnClickListener {
            confirmBooking()
        }
    }

    private fun getPrefsKey(): String {
        return "BookedSeats_${dramaTitle.replace(" ", "_")}"
    }

    private fun loadSeatStates() {
        val prefs = getSharedPreferences(getPrefsKey(), MODE_PRIVATE)
        allSeatIds.forEach { seatId ->
            val isBooked = prefs.getBoolean(seatId, false)
            seatStates[seatId] = SeatState(seatId, isBooked, false)
        }
    }

    private fun setupSeatButtons() {
        allSeatIds.forEach { seatId ->
            val buttonId = resources.getIdentifier(seatId, "id", packageName)
            val button = findViewById<Button>(buttonId)

            if (button != null) {
                seatButtons[seatId] = button
                updateSeatAppearance(seatId)

                button.setOnClickListener {
                    onSeatClicked(seatId)
                }
            }
        }
    }

    private fun onSeatClicked(seatId: String) {
        val state = seatStates[seatId] ?: return

        if (state.isBooked) {
            showSeatPopup("Seat $seatId is already booked!", "Booked", "#C41E3A")
            return
        }

        if (state.isSelected) {
            state.isSelected = false
            selectedSeats.remove(seatId)
            showSeatPopup("Seat $seatId deselected", "Available", "#4CAF50")
        } else {
            if (selectedSeats.size >= 4) {
                showSeatPopup("You can select maximum 4 seats", "Limit Reached", "#FF9800")
                return
            }
            state.isSelected = true
            selectedSeats.add(seatId)
            showSeatPopup("Seat $seatId selected!", "Selected", "#D4A843")
        }

        updateSeatAppearance(seatId)
        updateSelectedInfo()
    }

    private fun updateSeatAppearance(seatId: String) {
        val button = seatButtons[seatId] ?: return
        val state = seatStates[seatId] ?: return

        when {
            state.isBooked -> {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_seat_booked)
                button.setTextColor(Color.WHITE)
                button.isEnabled = false
            }
            state.isSelected -> {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_seat_selected)
                button.setTextColor(Color.BLACK)
            }
            else -> {
                button.background = ContextCompat.getDrawable(this, R.drawable.bg_seat_available)
                button.setTextColor(Color.WHITE)
            }
        }
    }

    private fun updateSelectedInfo() {
        val selectedText = findViewById<TextView>(R.id.selectedSeatsText)
        val totalText = findViewById<TextView>(R.id.totalAmountText)

        if (selectedSeats.isEmpty()) {
            selectedText.text = "None"
            totalText.text = "₹0"
        } else {
            selectedText.text = selectedSeats.joinToString(", ")
            val total = selectedSeats.size * seatPrice
            totalText.text = "₹$total"
        }
    }

    private fun showSeatPopup(message: String, title: String, colorHex: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_seat_popup, null)

        val dialog = AlertDialog.Builder(this, R.style.PopupDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.popupTitle).text = title
        dialogView.findViewById<TextView>(R.id.popupMessage).text = message

        // FIX: Use findViewById with proper type
        val colorIndicator = dialogView.findViewById<View>(R.id.popupColorIndicator)
        colorIndicator.setBackgroundColor(Color.parseColor(colorHex))

        dialogView.findViewById<Button>(R.id.popupOkBtn).setOnClickListener {
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

    private fun confirmBooking() {
        if (selectedSeats.isEmpty()) {
            showSeatPopup("Please select at least one seat", "No Selection", "#FF9800")
            return
        }

        val prefs = getSharedPreferences(getPrefsKey(), MODE_PRIVATE)
        val editor = prefs.edit()

        selectedSeats.forEach { seatId ->
            editor.putBoolean(seatId, true)
            seatStates[seatId]?.isBooked = true
            seatStates[seatId]?.isSelected = false
        }
        editor.apply()

        selectedSeats.forEach { updateSeatAppearance(it) }

        val seatsText = selectedSeats.joinToString(", ")
        val total = selectedSeats.size * seatPrice
        showSeatPopup(
            "Seats $seatsText booked successfully!\nTotal: ₹$total",
            "Booking Confirmed",
            "#4CAF50"
        )

        selectedSeats.clear()
        updateSelectedInfo()

        findViewById<Button>(R.id.confirmBtn).postDelayed({
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("seatNumbers", seatsText)
            intent.putExtra("dramaTitle", dramaTitle)
            intent.putExtra("totalAmount", total)
            startActivity(intent)
        }, 1500)
    }
}