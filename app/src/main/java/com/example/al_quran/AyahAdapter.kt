import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.al_quran.Ayah
import com.example.al_quran.R

class AyahAdapter : ListAdapter<Ayah, AyahAdapter.AyahViewHolder>(AyahDiffCallback()) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentlyPlayingPosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ayah, parent, false)
        return AyahViewHolder(view)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    inner class AyahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val arabicText: TextView = itemView.findViewById(R.id.textArabic)
        private val translationText: TextView = itemView.findViewById(R.id.textTranslation)
        private val playPauseButton: ImageButton = itemView.findViewById(R.id.btnPlayPause)

        fun bind(ayah: Ayah, position: Int) {
            val arabicNumber = convertToArabicNumber(ayah.numberInSurah)
            arabicText.text = "$arabicNumber. ${ayah.arabicText}"
            translationText.text = ayah.translationText

            val isPlaying = position == currentlyPlayingPosition
            playPauseButton.setImageResource(
                if (isPlaying) R.drawable.pause_24px else R.drawable.play_arrow_24px
            )

            playPauseButton.setOnClickListener {
                if (isPlaying) {
                    stopAudio()
                } else {
                    playAudio(ayah.audioUrl, position)
                }
            }
        }

        private fun convertToArabicNumber(number: Int): String {
            val arabicDigits = arrayOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
            return number.toString().map { arabicDigits[it.toString().toInt()] }.joinToString("")
        }
    }

    private fun playAudio(url: String, position: Int) {
        stopAudio()

        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
            start()
            setOnCompletionListener {
                currentlyPlayingPosition = null
                notifyItemChanged(position)
            }
        }

        currentlyPlayingPosition = position
        notifyItemChanged(position)
    }

    private fun stopAudio() {
        mediaPlayer?.apply {
            stop()
            release()
        }
        mediaPlayer = null

        currentlyPlayingPosition?.let {
            notifyItemChanged(it)
        }
        currentlyPlayingPosition = null
    }
}

class AyahDiffCallback : DiffUtil.ItemCallback<Ayah>() {
    override fun areItemsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
        return oldItem.number == newItem.number
    }

    override fun areContentsTheSame(oldItem: Ayah, newItem: Ayah): Boolean {
        return oldItem == newItem
    }
}
