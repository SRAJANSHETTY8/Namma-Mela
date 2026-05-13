package com.srajan.nammamela_anbookingapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DramaListActivity : AppCompatActivity() {

    data class DramaItem(
        val title: String,
        val subtitle: String,
        val genre: String,
        val time: String,
        val venue: String,
        val duration: String,
        val rating: String,
        val imageRes: Int
    )

    private val allDramas = listOf(
        DramaItem("Vaali Nataka", "The Roar of the Mythical Beast", "MYTHOLOGICAL",
            "7:30 PM", "Sri Rangamandira", "2h 45m", "4.9", R.drawable.vaali_nataka),

        DramaItem("Mahishasura Mardini", "Victory of Good over Evil", "MYTHOLOGICAL",
            "7:00 PM", "Udupi Open Theatre", "3h 00m", "4.8", R.drawable.drama_poster),

        DramaItem("Kamsa Vadhe", "The Slaying of Kamsa", "MYTHOLOGICAL",
            "8:00 PM", "Kota Shivaram Karanth", "2h 30m", "4.7", R.drawable.kansavadhe),

        DramaItem("Bhima Pratapa", "The Might of Bhima", "EPIC",
            "6:30 PM", "Town Hall", "2h 15m", "4.6", R.drawable.bhima),

        DramaItem("Abhimanyu Vadhe", "The Fall of Abhimanyu", "EPIC",
            "8:30 PM", "Ravindra Kalakshetra", "2h 50m", "4.5", R.drawable.abi),

        DramaItem("Draupadi Vastraharana", "The Disrobing of Draupadi", "EPIC",
            "7:15 PM", "Jaganmohan Palace", "3h 10m", "4.9", R.drawable.dhr)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_drama_list)

        findViewById<TextView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        val recyclerView =
            findViewById<RecyclerView>(R.id.dramaListRecycler)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            DramaListAdapter(allDramas) { position ->

                val intent =
                    Intent(this, SeatBookingActivity::class.java)

                intent.putExtra(
                    "drama_title",
                    allDramas[position].title
                )

                intent.putExtra(
                    "drama_time",
                    allDramas[position].time
                )

                intent.putExtra(
                    "drama_venue",
                    allDramas[position].venue
                )

                startActivity(intent)
            }
    }

    inner class DramaListAdapter(
        private val items: List<DramaItem>,
        private val onBookClick: (Int) -> Unit
    ) : RecyclerView.Adapter<DramaListAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {

            val poster: ImageView =
                view.findViewById(R.id.listPoster)

            val genre: TextView =
                view.findViewById(R.id.listGenre)

            val title: TextView =
                view.findViewById(R.id.listTitle)

            val subtitle: TextView =
                view.findViewById(R.id.listSubtitle)

            val time: TextView =
                view.findViewById(R.id.listTime)

            val venue: TextView =
                view.findViewById(R.id.listVenue)

            val duration: TextView =
                view.findViewById(R.id.listDuration)

            val rating: TextView =
                view.findViewById(R.id.listRating)

            val bookBtn: Button =
                view.findViewById(R.id.listBookBtn)

            val castBtn: Button =
                view.findViewById(R.id.listCastBtn)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_drama_list,
                        parent,
                        false
                    )

            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {

            val drama = items[position]

            holder.poster.setImageResource(drama.imageRes)

            holder.genre.text = drama.genre

            holder.title.text = drama.title

            holder.subtitle.text = drama.subtitle

            holder.time.text = drama.time

            holder.venue.text = drama.venue

            holder.duration.text = drama.duration

            holder.rating.text = "★ ${drama.rating}"

            holder.bookBtn.setOnClickListener {
                onBookClick(position)
            }

            holder.castBtn.setOnClickListener {

                val intent =
                    Intent(
                        holder.itemView.context,
                        CastActivity::class.java
                    )

                intent.putExtra(
                    "drama_name",
                    drama.title
                )

                holder.itemView.context.startActivity(intent)
            }
        }

        override fun getItemCount() = items.size
    }
}