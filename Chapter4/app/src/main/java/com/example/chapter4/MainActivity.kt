package com.example.chapter4

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chapter4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    lateinit var resultText: TextView
    lateinit var workingText: TextView

    private var canAddOperation = false
    private var canAddDecimal = true


    lateinit var btn1: AppCompatButton
    lateinit var btn2: AppCompatButton
    lateinit var btn3: AppCompatButton
    lateinit var btn4: AppCompatButton
    lateinit var btn5: AppCompatButton
    lateinit var btn6: AppCompatButton
    lateinit var btn7: AppCompatButton
    lateinit var btn8: AppCompatButton
    lateinit var btn9: AppCompatButton
    lateinit var btn0: AppCompatButton
    lateinit var btnC: AppCompatButton
    lateinit var btnAC: AppCompatButton
    lateinit var btnDot: AppCompatButton
    lateinit var btnAdd: AppCompatButton
    lateinit var btnSub: AppCompatButton
    lateinit var btnDiv: AppCompatButton
    lateinit var btnMul: AppCompatButton
    lateinit var btnEqual: AppCompatButton
    lateinit var btnRightParenthesis: AppCompatButton
    lateinit var btnLeftParenthesis: AppCompatButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        workingText = binding.equationTextview
        resultText = binding.resultTextview

        findViews()
        setClickListeners()


    }

    private fun findViews(){
        btn1 = binding.button1
        btn2 = binding.button2
        btn3 = binding.button3
        btn4 = binding.button4
        btn5 = binding.button5
        btn6 = binding.button6
        btn7 = binding.button7
        btn8 = binding.button8
        btn9 = binding.button9
        btn0 = binding.button0
        btnRightParenthesis = binding.buttonRightParentheses
        btnLeftParenthesis = binding.buttonLeftParentheses
        btnC = binding.buttonClear
        btnAC = binding.buttonAllClear
        btnDot = binding.buttonDot
        btnAdd = binding.buttonPlus
        btnSub = binding.buttonMinus
        btnMul = binding.buttonMultiply
        btnDiv = binding.buttonDivide
        btnEqual =binding.buttonEqual
    }

    private fun setClickListeners(){
        val numberButtons = listOf(btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0, btnDot)

        val operatorButtons = listOf(btnAdd, btnSub, btnMul, btnDiv)

        btnAC.setOnClickListener { allClearAction(it)}

        btnC.setOnClickListener { backSpaceAction(it) }

        btnEqual.setOnClickListener { equalClicked(it)}

        numberButtons.forEach{
            button -> button.setOnClickListener{numberClicked(it)}
        }

        operatorButtons.forEach{
            button -> button.setOnClickListener { operatorClicked(it) }
        }
    }


    fun numberClicked(view: View?){
        // button 값을 String으로 변환시켜준다.
        val buttonString = (view as? Button)?.text.toString() ?: ""

        // 연산자 뒤에 바로 .을 찍을 때 화면에 0.x로 표시
        if (buttonString == "."){
            val lastChar = if (workingText.text.isNotEmpty()) workingText.text.last() else ' '
            if (canAddDecimal){
                if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/'){
                    workingText.append("0.")
                } else{
                    workingText.append(buttonString)
                }
                canAddDecimal = false
            }
        } else {
        workingText.append(buttonString)
        }

        canAddOperation = true
    }

    fun operatorClicked(view: View?) {
        val operatorString = (view as? Button)?.text.toString() ?: ""

        if (operatorString.isNotEmpty() && canAddOperation){
            workingText.append(operatorString)
            canAddOperation = false
            canAddDecimal = true
        }
    }

    fun allClearAction(view: View?){
        workingText.text = ""
        resultText.text = ""
    }

    fun backSpaceAction(view: View?){

        val length = workingText.length()
        if (length > 0 ){
            workingText.text = workingText.text.subSequence(0, length-1)
        }
    }

    fun equalClicked(view: View?){
        resultText.text = calculateResults()
    }

    private fun calculateResults(): String {
        // 입력된 값을 숫자와 연산자로 분리
        val digitOperator = digitsOperators()
        if (digitOperator.isEmpty()) return ""

        // 곱셈, 나눗셈 연산을 먼저 수행
        val timesDivision = timesDivisionCalculate(digitOperator)
        if (timesDivision.isEmpty()) return ""

        val result = addSubtractCalculate(timesDivision)

        return result.toString()
    }

    private fun addSubtractCalculate(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices){
            if (passedList[i] is Char && i != passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i+1] as Float
                if (operator == '+'){
                    result += nextDigit
                }
                if (operator == '-'){
                    result -= nextDigit
                }
            }
        }
        return result
    }

    private fun timesDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {

        var list = passedList
        // * 또는 /가 남아있으면 calcTimesDiv()를 반복 호출하여 먼저 처리
        while (list.contains('*') || list.contains('/')){
            // newList가 return 된다.
            list = calcTimesDiv(list)
        }
        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        // 새로운 리스트 생성 (결과 저장용)
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        // indices -> list의 인덱스를 다 돈다는 의미
        for (i in passedList.indices){

            // 현재 passedList[i]가 Char 타입인지. 즉, 연산자인지 판별
            // 연산자가 아니라 숫자(float)이면 넘어감
            // i < restartIndex는 이미 처리된 연산을 중복 실행하지 않도록 방지
            if (passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i-1] as Float
                val nextDigit = passedList[i+1] as Float
                when(operator){
                    '*' ->
                    {
                        newList.add(prevDigit*nextDigit)
                        restartIndex = i + 1 // 이미 계산된 값을 건너뜀
                    }
                    '/' ->
                    {
                        newList.add(prevDigit/nextDigit)
                        restartIndex = i + 1 // 이미 계산된 값을 건너뜀
                    }
                    else ->
                    {   // 곱셈, 나눗셈이 아닌 경우 숫자와 연산자를 그대로 새로운 리스트에 저장
                        // 곱셈, 나눗셈에서 계산된 값을 newList에 넣었다. 이 값이 prevDigit와 겹치는 것은 걱정 안해도 된다.
                        // 한 번 계산이 끝나면 newList를 새로 만든 후, 그 list에 대해 다시 연산을 수행한다.
                        newList.add(prevDigit)
                        newList.add(operator)
                    }
                }
            }

            // 나머지 부분을 newList에 넣는다.
            if (i > restartIndex){
                newList.add(passedList[i])
            }
        }

        return newList
    }


    private fun digitsOperators(): MutableList<Any>{

        val list = mutableListOf<Any>()
        var currentDigit = ""
        for (character in workingText.text){
            if (character.isDigit() || character == '.'){
                currentDigit += character
            } else{
                list.add(currentDigit.toFloat()) // 숫자를 float로 변환하여 리스트에 추가
                currentDigit = "" // 새로웃 숫자를 저장하기 위해 초기화
                list.add(character) // 연산자를 리스트에 추가
            }
        }

        if (currentDigit != ""){
            list.add(currentDigit.toFloat())
        }

        return list
    }
}