package com.example.al_quran

data class SurahListResponse(
    val data: List<Surah>
)

data class Surah(
    val number: Int,
    val name: String,
    val englishName: String,
    val numberOfAyahs: Int
)

data class SurahDetailsResponse(
    val data: SurahDetails
)

data class SurahDetails(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
    val numberOfAyahs: Int,
    val ayahs: List<RawAyah>
)

data class RawAyah(
    val number: Int,
    val numberInSurah: Int,
    val text: String
)


data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val arabicText: String,
    val translationText: String
)
