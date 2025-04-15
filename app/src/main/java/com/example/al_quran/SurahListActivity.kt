package com.example.al_quran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SurahListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SurahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surah_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SurahAdapter { surah -> openSurahDetails(surah) }
        recyclerView.adapter = adapter

        fetchSurahList()
    }

    private fun fetchSurahList() {
        RetrofitClient.instance.getSurahList().enqueue(object : Callback<SurahListResponse> {
            override fun onResponse(call: Call<SurahListResponse>, response: Response<SurahListResponse>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("API_SUCCESS", "Jumlah Surah: ${data?.data?.size}")
                    data?.data?.let { adapter.submitList(it) }
                } else {
                    Log.e("API_ERROR", "Response tidak sukses: ${response.code()} - ${response.message()}")
                    Toast.makeText(this@SurahListActivity, "Gagal memuat data (response gagal)", Toast.LENGTH_SHORT).show()
                }            }

            override fun onFailure(call: Call<SurahListResponse>, t: Throwable) {
                Log.e("API_ERROR", "Retrofit gagal: ${t.message}")
                Toast.makeText(this@SurahListActivity, "Gagal memuat data (jaringan)", Toast.LENGTH_SHORT).show()            }
        })
    }

    private fun openSurahDetails(surah: Surah) {
        val intent = Intent(this, SurahDetailsActivity::class.java)
        intent.putExtra("SURAH_ID", surah.number)
        intent.putExtra("SURAH_NAME", surah.englishName)
        intent.putExtra("SURAH_TRANSLATION", surah.englishNameTranslation)
        intent.putExtra("SURAH_TYPE", surah.revelationType)
        startActivity(intent)
    }
}
