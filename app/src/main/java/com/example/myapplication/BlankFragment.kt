package com.example.myapplication

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.findFragment
import com.bumptech.glide.Glide
import com.example.myapplication.BaseActivity.Companion.APIKEY
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory




class BlankFragment : Fragment() {
    // TODO: Rename and change types of parameters

    public lateinit var param1: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view =   inflater.inflate(R.layout.fragment_blank, container, false)

        if(param1!=null){
            val api: OmdbApi = Retrofit.Builder()
                .baseUrl("http://www.omdbapi.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OmdbApi::class.java)

            api.getMovieDetails(APIKEY, param1!!).enqueue(object : Callback<MovieDetails>{
                override fun onResponse(
                    call: Call<MovieDetails>,
                    response: Response<MovieDetails>
                ) {

                    var movieDetails= response.body()
                    // assuming you have a reference to a MovieDetails object named movieDetails
                    val imageview =view.findViewById<ImageView>(R.id.poster)
                    view.findViewById<TextView>(R.id.title).text = movieDetails?.title
                    view.findViewById<TextView>(R.id.year).text = movieDetails?.year
                    view.findViewById<TextView>(R.id.runtime).text = movieDetails?.runtime
                    view.findViewById<TextView>(R.id.genre).text = movieDetails?.genre
                    view.findViewById<TextView>(R.id.director).text = movieDetails?.director
                    view.findViewById<TextView>(R.id.writer).text = movieDetails?.writer
                    view.findViewById<TextView>(R.id.actors).text = movieDetails?.actors
                    view.findViewById<TextView>(R.id.plot).text = movieDetails?.plot

                    if(context!=null ) {
                        Glide.with(context!!)
                            .load(movieDetails?.posterUrl)
                            .placeholder(R.drawable.placeholder_poster)
                            .thumbnail(0.1f)
                            .into(imageview)

                    }


                }




                override fun onFailure(call: Call<MovieDetails>, t: Throwable) {

                    Toast.makeText(context,"Sorry No Result Found",Toast.LENGTH_SHORT).show()
                    val handler= Handler()
                    handler.postDelayed({
                        requireActivity().onBackPressed()
                    }, 1000)
                }

            })
        }
        return view
    }






}