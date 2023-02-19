package com.example.myapplication

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.BaseActivity.Companion.APIKEY


class BaseActivity : AppCompatActivity() ,clickadapter {

    companion object{
        val APIKEY="b9bd48a6"
    }

    lateinit var moviesRecyclerView: RecyclerView;
    val REQUEST_CODE= 202
    lateinit var searchView: SearchView;
    lateinit var search_text: TextView;
    lateinit var searchQuery:String  ;
    private lateinit var moviesAdapter: MoviesAdapter
    private  var isLoading : Boolean = false;
    private var currentPage = 1
    private var backPressedTime: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
         searchView = findViewById(R.id.searchView)
        search_text = findViewById(R.id.search_text)
         moviesRecyclerView = findViewById(R.id.moviesRecyclerView)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.title = "Search Movie"


        var viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)





        moviesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                var hasMoreResults :Boolean = false
                if(totalItemCount>6){
                    hasMoreResults=true
                }


                if (!isLoading && hasMoreResults && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                    // User has reached the end of the list, fetch the next set of results
                    viewModel.searchMovies(searchQuery, currentPage + 1)
                    isLoading = true
                }
            }
        })


        viewModel.moviesLiveData.observe(this) { movies ->

            if (movies != null) {
                if (moviesRecyclerView.adapter == null) {
                    moviesRecyclerView.adapter =
                        MoviesAdapter(movies as MutableList<Movie>?, applicationContext,this)

                    moviesAdapter = moviesRecyclerView.adapter as MoviesAdapter
                }
                moviesAdapter.addMovies(movies)
                moviesRecyclerView.visibility = View.VISIBLE
                search_text?.visibility=View.GONE
            }else{
                search_text?.visibility=View.VISIBLE

            }
        }






        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {

                    viewModel.searchMovies(query,currentPage)


                } else {

                    searchQuery = query
                    ActivityCompat.requestPermissions(this@BaseActivity, arrayOf(Manifest.permission.INTERNET), REQUEST_CODE)
                }
                return true


            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (!isNetworkConnected()) {
                    val toast = Toast.makeText(applicationContext, "No connection!", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                    return false
                }

                viewModel.searchMovies(newText,currentPage)
                searchQuery = newText
                if(newText.length<=0){
                    if( moviesRecyclerView.adapter!=null && moviesAdapter!=null) {
                        moviesAdapter.clearMovies()
                    }
                }
                return true
            }
        })




    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.sdc)
        if(currentFragment is BlankFragment){
            super.onBackPressed()
        }else{

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
                if( moviesRecyclerView.adapter!=null && moviesAdapter!=null) {
                    moviesAdapter.clearMovies()
            }
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted, start your internet-related task
                var viewModel = ViewModelProvider(this).get(MovieViewModel::class.java)
                viewModel.searchMovies(searchQuery,currentPage)
            } else {
                Toast.makeText(this, "Internet permission is required to use this app", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onItemClick(c :String) {
        val fragment = BlankFragment()
            fragment.param1=c;
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.sdc, fragment)
        transaction.addToBackStack(null)
        transaction.commit()


    }


    private fun isNetworkConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

}


class MovieViewModel : ViewModel() {

    private val api: OmdbApi = Retrofit.Builder()
        .baseUrl("http://www.omdbapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OmdbApi::class.java)

    private val _moviesLiveData = MutableLiveData<List<Movie>>()
    val moviesLiveData: LiveData<List<Movie>>
        get() = _moviesLiveData

    private val _errorMessageLiveData = MutableLiveData<String>()
    val errorMessageLiveData: LiveData<String>
        get() = _errorMessageLiveData


    private var currentQuery = ""

    fun searchMovies(query: String,currentPage: Int) {

        currentQuery = query

        api.searchMovies(APIKEY, currentQuery,"movie", currentPage).enqueue(object : Callback<SearchResults> {
            override fun onResponse(call: Call<SearchResults>, response: Response<SearchResults>) {
                if (response.isSuccessful) {
                    val searchResults = response.body()
                    searchResults?.let {
                        _moviesLiveData.value = it.movies
                    }

                } else {
                    _errorMessageLiveData.value = "Error: ${response.code()} ${response.message()}"
                }
            }

            override fun onFailure(call: Call<SearchResults>, t: Throwable) {
                _errorMessageLiveData.value = "Error: ${t.message}"
            }
        })
    }



}

interface clickadapter {
    fun onItemClick( c :String)
}



