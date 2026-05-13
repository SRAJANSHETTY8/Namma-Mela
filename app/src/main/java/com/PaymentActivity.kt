package com.srajan.nammamela_anbookingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

class PaymentActivity : AppCompatActivity() {

    private lateinit var nameInput: EditText
    private lateinit var cardInput: EditText
    private lateinit var expiryInput: EditText
    private lateinit var cvvInput: EditText
    private lateinit var payBtn: Button
    private lateinit var bottomPayLayout: LinearLayout
    private lateinit var loadingView: LinearLayout
    private lateinit var ticketCard: CardView
    private lateinit var paymentScrollView: androidx.core.widget.NestedScrollView

    // Summary views
    private lateinit var summaryDrama: TextView
    private lateinit var summarySeats: TextView
    private lateinit var summaryTotal: TextView

    // Ticket views
    private lateinit var ticketBookingId: TextView
    private lateinit var ticketDrama: TextView
    private lateinit var ticketSeats: TextView
    private lateinit var ticketDate: TextView
    private lateinit var ticketTime: TextView
    private lateinit var ticketVenue: TextView
    private lateinit var ticketAmount: TextView
    private lateinit var viewBookingsBtn: Button

    private var dramaTitle: String = ""
    private var seatNumbers: String = ""
    private var totalAmount: Int = 0
    private var dramaTime: String = ""
    private var dramaVenue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        initViews()
        getIntentData()
        updateSummary()
        setupPayButton()
    }

    private fun initViews() {
        nameInput = findViewById(R.id.nameInput)
        cardInput = findViewById(R.id.cardInput)
        expiryInput = findViewById(R.id.expiryInput)
        cvvInput = findViewById(R.id.cvvInput)
        payBtn = findViewById(R.id.payBtn)
        bottomPayLayout = findViewById(R.id.bottomPayLayout)
        loadingView = findViewById(R.id.loadingView)
        ticketCard = findViewById(R.id.ticketCard)
        paymentScrollView = findViewById(R.id.paymentScrollView)

        summaryDrama = findViewById(R.id.summaryDrama)
        summarySeats = findViewById(R.id.summarySeats)
        summaryTotal = findViewById(R.id.summaryTotal)

        ticketBookingId = findViewById(R.id.ticketBookingId)
        ticketDrama = findViewById(R.id.ticketDrama)
        ticketSeats = findViewById(R.id.ticketSeats)
        ticketDate = findViewById(R.id.ticketDate)
        ticketTime = findViewById(R.id.ticketTime)
        ticketVenue = findViewById(R.id.ticketVenue)
        ticketAmount = findViewById(R.id.ticketAmount)
        viewBookingsBtn = findViewById(R.id.viewBookingsBtn)
    }

    private fun getIntentData() {
        dramaTitle = intent.getStringExtra("dramaTitle") ?: "Vyali Nataka"
        seatNumbers = intent.getStringExtra("seatNumbers") ?: "A1"
        totalAmount = intent.getIntExtra("totalAmount", 150)
        dramaTime = intent.getStringExtra("dramaTime") ?: "7:30 PM"
        dramaVenue = intent.getStringExtra("dramaVenue") ?: "Sri Rangamandira"
    }

    private fun updateSummary() {
        summaryDrama.text = dramaTitle
        summarySeats.text = seatNumbers
        summaryTotal.text = "₹$totalAmount"
        payBtn.text = "Pay ₹$totalAmount"
    }

    private fun setupPayButton() {
        payBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val card = cardInput.text.toString().trim()
            val expiry = expiryInput.text.toString().trim()
            val cvv = cvvInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Enter card holder name"
                return@setOnClickListener
            }

            if (card.length < 16) {
                cardInput.error = "Enter valid 16-digit card number"
                return@setOnClickListener
            }

            if (expiry.isEmpty()) {
                expiryInput.error = "Enter expiry date"
                return@setOnClickListener
            }

            if (cvv.length < 3) {
                cvvInput.error = "Enter valid CVV"
                return@setOnClickListener
            }

            // Start payment processing
            startPaymentProcess(name)
        }
    }

    private fun startPaymentProcess(userName: String) {
        // Hide form, show loading
        bottomPayLayout.visibility = View.GONE
        paymentScrollView.smoothScrollTo(0, 0)

        // Show loading after slight delay
        Handler(Looper.getMainLooper()).postDelayed({
            loadingView.visibility = View.VISIBLE

            // Simulate payment processing (3 seconds)
            Handler(Looper.getMainLooper()).postDelayed({
                loadingView.visibility = View.GONE
                showTicket(userName)
            }, 3000)
        }, 200)
    }

    private fun showTicket(userName: String) {
        // Generate booking ID
        val bookingId = generateBookingId()
        val currentDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        // Update ticket views
        ticketBookingId.text = bookingId
        ticketDrama.text = dramaTitle
        ticketSeats.text = seatNumbers
        ticketDate.text = currentDate
        ticketTime.text = dramaTime
        ticketVenue.text = dramaVenue
        ticketAmount.text = "₹$totalAmount"

        // Show ticket card
        ticketCard.visibility = View.VISIBLE
        paymentScrollView.post {
            paymentScrollView.smoothScrollTo(0, ticketCard.top)
        }

        // Save to booking history
        saveBookingHistory(bookingId, userName, currentDate)

        // Setup view bookings button
        viewBookingsBtn.setOnClickListener {
            val intent = Intent(this, BookingHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        Toast.makeText(this, "Payment Successful! 🎉", Toast.LENGTH_LONG).show()
    }

    private fun generateBookingId(): String {
        val dramaCode = dramaTitle.take(4).uppercase()
        val randomNum = Random().nextInt(9000) + 1000
        return "NM-$dramaCode-$randomNum"
    }

    private fun saveBookingHistory(bookingId: String, userName: String, date: String) {
        val prefs = getSharedPreferences("BookingHistory", MODE_PRIVATE)

        // Get existing bookings
        val existingCount = prefs.getInt("booking_count", 0)

        // Save new booking with index
        val editor = prefs.edit()
        editor.putInt("booking_count", existingCount + 1)
        editor.putString("booking_${existingCount}_id", bookingId)
        editor.putString("booking_${existingCount}_drama", dramaTitle)
        editor.putString("booking_${existingCount}_seats", seatNumbers)
        editor.putString("booking_${existingCount}_date", date)
        editor.putString("booking_${existingCount}_time", dramaTime)
        editor.putString("booking_${existingCount}_venue", dramaVenue)
        editor.putInt("booking_${existingCount}_amount", totalAmount)
        editor.putString("booking_${existingCount}_user", userName)
        editor.apply()
    }
}