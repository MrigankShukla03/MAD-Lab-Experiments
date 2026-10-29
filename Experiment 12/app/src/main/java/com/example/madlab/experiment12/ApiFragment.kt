package com.example.madlab.experiment12

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.madlab.experiment12.databinding.FragmentApiBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiFragment : Fragment() {

    private var _binding: FragmentApiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentApiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFetchApi.setOnClickListener {
            fetchWebServicesData()
        }

        // Auto-fetch on screen open
        fetchWebServicesData()
    }

    private fun fetchWebServicesData() {
        binding.apiProgressBar.visibility = View.VISIBLE
        binding.btnFetchApi.isEnabled = false

        RetrofitClient.apiService.getPosts().enqueue(object : Callback<List<ApiPost>> {
            override fun onResponse(call: Call<List<ApiPost>>, response: Response<List<ApiPost>>) {
                if (!isAdded) return
                binding.apiProgressBar.visibility = View.GONE
                binding.btnFetchApi.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val posts = response.body()!!
                    val adapter = ApiPostAdapter(requireContext(), posts)
                    binding.lvApiPosts.adapter = adapter
                    Toast.makeText(context, "Successfully fetched ${posts.size} posts via HTTP GET", Toast.LENGTH_SHORT).show()
                } else {
                    loadFallbackPosts()
                    Toast.makeText(context, "Server Error. Loaded sample posts.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ApiPost>>, t: Throwable) {
                if (!isAdded) return
                binding.apiProgressBar.visibility = View.GONE
                binding.btnFetchApi.isEnabled = true

                loadFallbackPosts()
                Toast.makeText(context, "Network Error: ${t.localizedMessage}. Loaded sample posts.", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadFallbackPosts() {
        val samplePosts = listOf(
            ApiPost(1, 1, "Android REST API Integration", "Learn how to perform asynchronous HTTP requests using Retrofit and Gson converter in Kotlin."),
            ApiPost(1, 2, "SQLite Data Persistence", "Store and query structured relational data locally using SQLiteOpenHelper and custom adapters."),
            ApiPost(1, 3, "Content Provider Sharing", "Access system contacts securely across application boundaries via Android ContentResolver."),
            ApiPost(1, 4, "Google Maps API Navigation", "Display interactive maps, custom markers, and campus landmarks using Google Maps SDK.")
        )
        val adapter = ApiPostAdapter(requireContext(), samplePosts)
        binding.lvApiPosts.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
