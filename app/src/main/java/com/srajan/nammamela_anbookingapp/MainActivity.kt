package com.srajan.nammamela_anbookingapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    data class Drama(
        val title: String,
        val subtitle: String,
        val genre: String,
        val time: String,
        val venue: String,
        val duration: String,
        val rating: String,
        val imageRes: Int
    )

    data class Actor(
        val name: String,
        val type: String,
        val imageRes: Int
    )

    data class Applause(
        val name: String,
        val initial: String,
        val review: String,
        val timeAgo: String,
        val avatarColor: Int
    )

    private val dramas = listOf(

        Drama(
            "Vaali Nataka",
            "The Roar of the Mythical Beast",
            "MYTHOLOGICAL",
            "7:30 PM",
            "Sri Rangamandira",
            "2h 45m",
            "4.9",
            R.drawable.vaali_nataka
        ),

        Drama(
            "Mahishasura Mardini",
            "Victory of Good over Evil",
            "MYTHOLOGICAL",
            "7:00 PM",
            "Udupi Open Theatre",
            "3h 00m",
            "4.8",
            R.drawable.drama_poster
        ),

        Drama(
            "Kamsa Vadhe",
            "The Slaying of Kamsa",
            "MYTHOLOGICAL",
            "8:00 PM",
            "Kota Shivaram Karanth",
            "2h 30m",
            "4.7",
            R.drawable.kansavadhe
        )
    )

    private val allRealActors = listOf(

        Actor(
            "Raghavendra",
            "Yakshagana",
            R.drawable.raghavendra
        ),

        Actor(
            "Shobha",
            "Devotional",
            R.drawable.shobha
        ),

        Actor(
            "Naveen",
            "Bhagavata",
            R.drawable.naveen
        ),

        Actor(
            "Ganesh",
            "Traditional",
            R.drawable.ganesh
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupDramaSlider()
        setupActorsList()
        setupApplauseList()
        setupBottomNavigation()
        setupClickListeners()
    }

    private fun setupDramaSlider() {

        val viewPager =
            findViewById<ViewPager2>(R.id.dramaViewPager)

        val tabLayout =
            findViewById<TabLayout>(R.id.pageIndicator)

        viewPager.adapter =
            DramaSliderAdapter(dramas) { position ->

                val intent =
                    Intent(
                        this,
                        SeatBookingActivity::class.java
                    )

                intent.putExtra(
                    "drama_title",
                    dramas[position].title
                )

                intent.putExtra(
                    "drama_time",
                    dramas[position].time
                )

                intent.putExtra(
                    "drama_venue",
                    dramas[position].venue
                )

                startActivity(intent)
            }

        TabLayoutMediator(
            tabLayout,
            viewPager
        ) { _, _ -> }.attach()
    }

    private fun setupActorsList() {

        val recyclerView =
            findViewById<RecyclerView>(R.id.actorsRecyclerView)

        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )

        val shuffledActors =
            allRealActors.shuffled(
                Random(System.currentTimeMillis())
            )

        val selectedActors =
            shuffledActors.take(3)

        recyclerView.adapter =
            ActorsAdapter(selectedActors)
    }

    private fun setupApplauseList() {

        val recyclerView =
            findViewById<RecyclerView>(R.id.applauseRecyclerView)

        val emptyState =
            findViewById<LinearLayout>(R.id.emptyApplauseState)

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        val realApplause =
            FanWallActivity.getApplauseList(this)

        if (realApplause.isEmpty()) {

            recyclerView.visibility = View.GONE

            emptyState.visibility = View.VISIBLE

        } else {

            recyclerView.visibility = View.VISIBLE

            emptyState.visibility = View.GONE

            val homeApplause =
                realApplause.take(3).map {

                    Applause(
                        name = it.name,
                        initial = it.initial,
                        review = it.comment,
                        timeAgo = it.timeAgo,
                        avatarColor = R.drawable.bg_avatar_gold
                    )
                }

            recyclerView.adapter =
                ApplauseAdapter(homeApplause)
        }
    }

    private fun setupBottomNavigation() {

        findViewById<LinearLayout>(R.id.navHome)
            .setOnClickListener {

                findViewById<androidx.core.widget.NestedScrollView>(
                    R.id.mainScrollView
                ).smoothScrollTo(0, 0)
            }

        findViewById<LinearLayout>(R.id.navDramas)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        DramaListActivity::class.java
                    )

                startActivity(intent)
            }

        findViewById<LinearLayout>(R.id.navBookings)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        BookingHistoryActivity::class.java
                    )

                startActivity(intent)
            }

        findViewById<LinearLayout>(R.id.navApplause)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        FanWallActivity::class.java
                    )

                startActivity(intent)
            }
    }

    private fun setupClickListeners() {

        findViewById<TextView>(R.id.seeAllActors)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        CastActivity::class.java
                    )

                startActivity(intent)
            }

        findViewById<TextView>(R.id.seeAllApplause)
            .setOnClickListener {

                val intent =
                    Intent(
                        this,
                        FanWallActivity::class.java
                    )

                startActivity(intent)
            }
    }

    override fun onResume() {
        super.onResume()
        setupApplauseList()
    }

    inner class DramaSliderAdapter(
        private val items: List<Drama>,
        private val onBookClick: (Int) -> Unit
    ) : RecyclerView.Adapter<DramaSliderAdapter.DramaViewHolder>() {

        inner class DramaViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {

            val poster: ImageView =
                view.findViewById(R.id.sliderPoster)

            val genre: TextView =
                view.findViewById(R.id.sliderGenre)

            val title: TextView =
                view.findViewById(R.id.sliderTitle)

            val subtitle: TextView =
                view.findViewById(R.id.sliderSubtitle)

            val time: TextView =
                view.findViewById(R.id.sliderTime)

            val venue: TextView =
                view.findViewById(R.id.sliderVenue)

            val duration: TextView =
                view.findViewById(R.id.sliderDuration)

            val rating: TextView =
                view.findViewById(R.id.sliderRating)

            val bookBtn: Button =
                view.findViewById(R.id.sliderBookBtn)

            val castBtn: Button =
                view.findViewById(R.id.sliderCastBtn)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): DramaViewHolder {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_drama_slider,
                        parent,
                        false
                    )

            return DramaViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: DramaViewHolder,
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

    inner class ActorsAdapter(
        private val items: List<Actor>
    ) : RecyclerView.Adapter<ActorsAdapter.ActorViewHolder>() {

        inner class ActorViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {

            val image: ImageView =
                view.findViewById(R.id.actorImage)

            val name: TextView =
                view.findViewById(R.id.actorName)

            val type: TextView =
                view.findViewById(R.id.actorType)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ActorViewHolder {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_actor,
                        parent,
                        false
                    )

            return ActorViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ActorViewHolder,
            position: Int
        ) {

            val actor = items[position]

            holder.image.setImageResource(actor.imageRes)

            holder.name.text = actor.name

            holder.type.text = actor.type
        }

        override fun getItemCount() = items.size
    }

    inner class ApplauseAdapter(
        private val items: List<Applause>
    ) : RecyclerView.Adapter<ApplauseAdapter.ApplauseViewHolder>() {

        inner class ApplauseViewHolder(view: View) :
            RecyclerView.ViewHolder(view) {

            val avatar: TextView =
                view.findViewById(R.id.applauseAvatar)

            val name: TextView =
                view.findViewById(R.id.applauseName)

            val time: TextView =
                view.findViewById(R.id.applauseTime)

            val review: TextView =
                view.findViewById(R.id.applauseReview)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ApplauseViewHolder {

            val view =
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_applause,
                        parent,
                        false
                    )

            return ApplauseViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ApplauseViewHolder,
            position: Int
        ) {

            val item = items[position]

            holder.avatar.text = item.initial

            holder.avatar.setBackgroundResource(
                item.avatarColor
            )

            holder.name.text = item.name

            holder.time.text = item.timeAgo

            holder.review.text = item.review
        }

        override fun getItemCount() = items.size
    }
}