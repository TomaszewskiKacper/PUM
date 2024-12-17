package com.example.lista4

import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lista4.ui.theme.Lista4Theme

class MainActivity : ComponentActivity() {

    private val questionList = listOf(
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

    private val answerList = listOf(
        listOf("Wektorowa wielkość fizyczna", "Zmiana położenia ciała w czasie", "Ilość materii zawarta w ciele", "Rodzaj energii"),
        listOf("10.8", "9.8", "11.1", "9.0"),
        listOf("7.25 Km", "12.5 Km", "25 Km", "100 Km"),
        listOf("Ruch ciała jest zawsze prostoliniowy", "Siła działająca na ciało jest równa iloczynowi masy i przyspieszenia", "Każdej akcji towarzyszy reakcja o tej samej wartości, ale przeciwnym kierunku", "Jeśli na ciało nie działa żadna siła, to jego prędkość jest stała lub ciało pozostaje w spoczynku"),
        listOf("0", "1", "0.5", "10"),
        listOf("Pierza", "Stali", "obie ważą tyle samo", "Brak odpowiedzi"),
        listOf("neutron", "Foton", "Proton", "Elektron"),
        listOf("Brak Odpowiedzi", "4", "8", "6"),
        listOf("a", "m", "V", "W"),
        listOf("Obserwator", "Ziemia", "Nie ma centrum", "Nikt nie wie")
    )

    private val correctAnswers = listOf(1, 2, 2, 4, 2, 3, 2, 4, 3, 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Lista4Theme {
                QuizScreen(questionList, answerList, correctAnswers)
            }
        }
    }
}

@Composable
fun QuizScreen(questionList: List<String>, answerList: List<List<String>>, correctAnswers: List<Int>) {
    var currentQuestion by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }

    if (currentQuestion < questionList.size) {  //IF QUESTIONS LEFT
        //COLUMN WITH QUIZ
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //CURRENT QUESTION TXT
            Text(
                text = "Pytanie "+ (currentQuestion + 1)+"/10",
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            //QUESTION
            Text(
                text = questionList[currentQuestion],
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                modifier = Modifier.background(Color(222,222,222), RectangleShape).padding(10.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))

            Column(modifier = Modifier.fillMaxWidth()) {    //COLUMN WITH RADIO BUTTONS
                answerList[currentQuestion].forEachIndexed { index, answer ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color(222,222,222))
                    ) {
                        RadioButton(
                            selected = selectedAnswer == index,
                            onClick = { selectedAnswer = index
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = answer)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                if (selectedAnswer == correctAnswers[currentQuestion] - 1) {
                    score++
                }
                currentQuestion++
            }) {
                Text(text = "Dalej")
            }
        }
    } else {    //RESULT SCREEN
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Gratulacje",
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Zdobyłeś " + score +"pkt",
                textAlign = TextAlign.Center,
                fontSize = 30.sp)
        }
    }
}
