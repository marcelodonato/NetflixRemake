package com.marcelodonato.netflixremake.kotlin

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.bumptech.glide.request.target.Target
import com.marcelodonato.netflixremake.R
import com.marcelodonato.netflixremake.model.Movie
import com.marcelodonato.netflixremake.model.MovieDetail
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.activity_movie.image_view_cover
import kotlinx.android.synthetic.main.activity_movie.view.*
import kotlinx.android.synthetic.main.movie_item_similar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)

        intent.extras?.let {
            val id = it.getInt("id")

            retrofit().create(NetflixAPI::class.java)
                .getMovieBy(id)
                .enqueue(object : Callback<MovieDetail> {
                    override fun onResponse(
                        call: Call<MovieDetail>,
                        response: Response<MovieDetail>
                    ) {
                        if (response.isSuccessful) {
                            response.body().let { movieDetail: MovieDetail? ->

                                text_view_title.text = movieDetail?.title
                                text_view_desc.text = movieDetail?.desc
                                text_view_cast.text = getString(R.string.cast, movieDetail?.cast)

                                Glide.with(this@MovieActivity)
                                    .load(movieDetail?.coverUrl)
                                    .listener(object : RequestListener<Drawable> {
                                        override fun onLoadFailed(
                                            e: GlideException?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            return true
                                        }

                                        override fun onResourceReady(
                                            resource: Drawable?,
                                            model: Any?,
                                            target: Target<Drawable>?,
                                            dataSource: DataSource?,
                                            isFirstResource: Boolean
                                        ): Boolean {
                                            val drawble: LayerDrawable? = ContextCompat.getDrawable(
                                                baseContext,
                                                R.drawable.shadows
                                            ) as LayerDrawable?
                                            drawble?.let {
                                                drawble.setDrawableByLayerId(
                                                    R.id.cover_drawable,
                                                    resource
                                                )
                                                (target as DrawableImageViewTarget).view.setImageDrawable(
                                                    drawble
                                                )
                                            }

                                            return true
                                        }

                                    })
                                    .into(image_view_cover)

                                movieAdapter.movies.clear()
                                if (movieDetail != null) {
                                    movieAdapter.movies.addAll(movieDetail.moviesSimilar)
                                }
                                movieAdapter.notifyDataSetChanged()

                            }
                        }
                    }

                    override fun onFailure(call: Call<MovieDetail>, t: Throwable) {
                        Toast.makeText(this@MovieActivity, t.message, Toast.LENGTH_SHORT).show()
                    }

                })

            setSupportActionBar(toolbar)

            supportActionBar?.let { toolbar ->
                toolbar.setDisplayHomeAsUpEnabled(true)
                toolbar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
                toolbar.title = null

            }

            val movies = arrayListOf<Movie>()
            movieAdapter = MovieAdapter(movies)
            recycler_view_similar.adapter = movieAdapter
            recycler_view_similar.layoutManager = GridLayoutManager(this, 3)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private inner class MovieAdapter(val movies: MutableList<Movie>) :
        RecyclerView.Adapter<MovieHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder =
            MovieHolder(layoutInflater.inflate(R.layout.movie_item_similar, parent, false))

        override fun getItemCount() = movies.size

        override fun onBindViewHolder(holder: MovieHolder, position: Int) =
            holder.bind(movies[position])

    }

    private class MovieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            with(itemView) {
                Glide.with(context).load(movie.coverUrl).placeholder(R.drawable.placeholder_bg)
                    .into(image_view_cover)
            }
        }
    }

}