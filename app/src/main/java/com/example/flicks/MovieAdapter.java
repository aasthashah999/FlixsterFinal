package com.example.flicks;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Config;
import com.example.flicks.models.Movie;
import com.example.flicks.models.MovieDetailsActivity;

import org.parceler.Parcels;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    //
    ArrayList<Movie> movies;
    Config config;

    //conetxt for rendering
    Context context;

    public MovieAdapter(ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public Config getConfig(){return config;}
    public void setConfig(Config config){this.config = config;}

    @NonNull
    @Override
    //creates and inflates a new view
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_movie Layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    //binds an inflated view to a new item
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        //get movie at a specified location
        Movie movie = movies.get(position);

        //populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        //determine the current orientation
        boolean isPotrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        String imageUrl = null;

        if (isPotrait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }else{
            //called if landscape
            imageUrl = config.getImageUrl(config.getPosterSize(),movie.getBackdropPath());
        }

        //get the correct placeholder and imageview for the current orientation
        int placeholderId = isPotrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPotrait ? holder.ivPosterImage: holder.ivBackdropImage;

        //create image url

        //load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 15,10))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);


    }

    @Override
    //returns the total number of items in the list
    public int getItemCount() {
        return movies.size();
    }

    //create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPosterImage = itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = itemView.findViewById(R.id.ivPosterImage);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //get item position
            int position = getAdapterPosition();

            //ensure the position exists in the view
            if(position != RecyclerView.NO_POSITION){
                Movie movie = movies.get(position);

                //creating intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);

                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                // show the activity
                context.startActivity(intent);
            }

        }
    }
}
