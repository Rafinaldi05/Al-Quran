package com.example.al_quran

import AyahAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*

class SurahDetailsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AyahAdapter
    private lateinit var revelationTypeText: TextView

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surah_details)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AyahAdapter()
        recyclerView.adapter = adapter
        revelationTypeText = findViewById(R.id.textRevelationType)

        val surahId = intent.getIntExtra("SURAH_ID", 1)
        val surahName = intent.getStringExtra("SURAH_NAME")
        val surahTranslation = intent.getStringExtra("SURAH_TRANSLATION")
        val surahType = intent.getStringExtra("SURAH_TYPE")
        title = if (surahName != null && surahTranslation != null) {
            "$surahName ($surahTranslation)"
        } else {
            surahName ?: "Surah"
        }
        revelationTypeText.text = "Revelation: $surahType"

        fetchSurahDetails(surahId)
    }

    private fun fetchSurahDetails(surahId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val arabicResponse = RetrofitClient.instance.getSurahDetails(surahId, "ar.alafasy")
                val translationResponse = RetrofitClient.instance.getSurahDetails(surahId, "id.indonesian")

                val arabicAyat = arabicResponse.data.ayahs
                val translationAyat = translationResponse.data.ayahs

                val combined = arabicAyat.zip(translationAyat).map { (ar, tr) ->
                    Ayah(
                        number = ar.number,
                        numberInSurah = ar.numberInSurah,
                        arabicText = ar.text,
                        translationText = tr.text,
                        audioUrl = ar.audio
                    )
                }
                withContext(Dispatchers.Main) {
                    adapter.submitList(combined)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SurahDetailsActivity, "Gagal memuat ayat", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
