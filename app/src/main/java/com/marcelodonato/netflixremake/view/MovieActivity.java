package com.marcelodonato.netflixremake.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.marcelodonato.netflixremake.R;
import com.marcelodonato.netflixremake.model.Movie;
import com.marcelodonato.netflixremake.model.MovieDetail;
import com.marcelodonato.netflixremake.util.ImageDownloaderTask;
import com.marcelodonato.netflixremake.util.MovieDetailTask;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MovieActivity extends AppCompatActivity implements MovieDetailTask.MovieDetailLoader {

    private TextView txtTitle;
    private TextView txtDesc;
    private TextView txtCast;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapeter;
    private ImageView imgCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        txtTitle = findViewById(R.id.text_view_title);
        txtDesc = findViewById(R.id.text_view_desc);
        txtCast = findViewById(R.id.text_view_cast);
        recyclerView = findViewById(R.id.recycler_view_similar);
        imgCover = findViewById(R.id.image_view_cover);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24);
            getSupportActionBar().setTitle(null);
        }

        List<Movie> movies = new ArrayList<>();
        movieAdapeter = new MovieAdapter(movies);
        recyclerView.setAdapter(movieAdapeter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int id = extras.getInt("id");
            MovieDetailTask movieDetailTask = new MovieDetailTask(this);
            movieDetailTask.setMovieDetailLoader(this);
            movieDetailTask.execute("https://tiagoaguiar.co/api/netflix/" + id);
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResult(MovieDetail movieDetail) {
        txtTitle.setText(movieDetail.getMovie().getTitle());
        txtDesc.setText(movieDetail.getMovie().getDesc());
        txtCast.setText(movieDetail.getMovie().getCast());

        ImageDownloaderTask imageDownloaderTask = new ImageDownloaderTask(imgCover);
        imageDownloaderTask.setShadowEnabled(true);
        imageDownloaderTask.execute(movieDetail.getMovie().getCoverUrl());

        movieAdapeter.setMovies(movieDetail.getMoviesSimilar());
        movieAdapeter.notifyDataSetChanged();
    }

    private static class MovieHolder extends RecyclerView.ViewHolder {

        final ImageView imageViewCover;

        public MovieHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.image_view_cover);
        }

    }

    private class MovieAdapter extends RecyclerView.Adapter<MovieHolder> {

        private List<Movie> movies;

        private MovieAdapter(List<Movie> movies) {
            this.movies = movies;
        }

        void setMovies(List<Movie> movies) {
            this.movies.clear();
            this.movies.addAll(movies);
        }

        @NonNull
        @Override
        public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MovieHolder(getLayoutInflater().inflate(R.layout.movie_item_similar, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
            Movie movie = movies.get(position);
            new ImageDownloaderTask(holder.imageViewCover).execute(movie.getCoverUrl());
        }

        @Override
        public int getItemCount() {
            return movies.size();
        }


    }
}