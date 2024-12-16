package com.example.lista1

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lista1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //View binding
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val question_list = listOf(
        "Co to jest siła?",
        "Ile wynosi (w przybliżeniu) przyśpieszenie grawitacyjne na Ziemi?",
        "Jeśli ciało porusza się z prędkością 50 km/h, jaki dystans pokona po 15 min?",
        "Jak brzmi pierwsza zasada dynamiki Newtona?",
        "Jakie jest ciśnienie atmosferyczne na poziomie morza?",
        "Co waży więcej, Kilogram pierza czy kilogram Stali?",
        "Które z poniższych, NIE jest częścią atomu",
        "2 + 2 * 2 = ?",
        "Który symbol odpowiada prędkości?",
        "Co znajduje się w centrum obserwowalnego wszechświata?"
    )
    private val answer_list = listOf(
        listOf("Wektorowa wielkość fizyczna","Zmiana położenia ciała w czasie","Ilość materii zawarta w ciele","Rodzaj energii"),
        listOf("10.8","9.8","11.1","9.0"),
        listOf("7.25 Km","12.5 Km","25 Km","100 Km"),
        listOf("Ruch ciała jest zawsze prostoliniowy","Siła działająca na ciało jest równa iloczynowi masy i przyspieszenia","Każdej akcji towarzyszy reakcja o tej samej wartości, ale przeciwnym kierunku","Jeśli na ciało nie działa żadna siła, to jego prędkość jest stała lub ciało pozostaje w spoczynku"),
        listOf("0","1","0.5","10"),
        listOf("Pierza","Stali","obie warzą tyle samo","Brak odpowiedzi"),
        listOf("neutron","Foton","Proton","Elektron"),
        listOf("Brak Odpowiedzi","4","8","6"),
        listOf("a","m","V","W"),
        listOf("Obserwator","Ziemia","Nie ma centrum","Nikt nie wie"),
    )
    private val correct_list = listOf(1, 2, 2, 4, 2, 3, 2, 4, 3, 1)
    private var current_number = 0;

    private var score = 0



    fun press(){
        //check answer
        if(current_number > 0){
            when(correct_list[current_number-1]){
                1 -> if (binding.answer1.isSelected) { score++ }
                2 -> if (binding.answer2.isSelected) { score++ }
                3 -> if (binding.answer3.isSelected) { score++ }
                4 -> if (binding.answer4.isSelected) { score++ }
            }
        }




        //increment number
        current_number++

        //if all questions answered
        if (current_number > 10){
            binding.nextButton.visibility = View.GONE
            binding.answerGroup.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
            binding.questionText.visibility = View.GONE

            binding.scoreN.visibility = View.VISIBLE

            binding.scoreN.text = "Zdobyłeś " + score.toString() + "pkt"
            binding.questionNumber.text = "Gratulacje"
            return
        }




        //update texts
        binding.questionNumber.text = "Pytanie " + current_number.toString() + "/10"
        binding.progressBar.progress = current_number*10
        binding.questionText.text = question_list[current_number-1]
        binding.answer1.text = answer_list[current_number-1][0]
        binding.answer2.text = answer_list[current_number-1][1]
        binding.answer3.text = answer_list[current_number-1][2]
        binding.answer4.text = answer_list[current_number-1][3]
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        press()
        binding.nextButton.setOnClickListener { press() }
        // Set click listeners for the answers
        binding.answer1.setOnClickListener {
            binding.answer1.isSelected = true
            binding.answer2.isSelected = false
            binding.answer3.isSelected = false
            binding.answer4.isSelected = false
        }

        binding.answer2.setOnClickListener {
            binding.answer1.isSelected = false
            binding.answer2.isSelected = true
            binding.answer3.isSelected = false
            binding.answer4.isSelected = false
        }

        binding.answer3.setOnClickListener {
            binding.answer1.isSelected = false
            binding.answer2.isSelected = false
            binding.answer3.isSelected = true
            binding.answer4.isSelected = false
        }

        binding.answer4.setOnClickListener {
            binding.answer1.isSelected = false
            binding.answer2.isSelected = false
            binding.answer3.isSelected = false
            binding.answer4.isSelected = true
        }


    }
}