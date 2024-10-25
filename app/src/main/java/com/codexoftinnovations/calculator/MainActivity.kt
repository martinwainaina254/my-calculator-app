package com.codexoftinnovations.calculator

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvResult: TextView
    private var input = StringBuilder()
    private var lastNumeric = false
    private var lastDot = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvResult = findViewById(R.id.tvResult)

        setNumberButtonListeners()
        setOperationButtonListeners()
        setSpecialButtonListeners()
    }

    private fun setNumberButtonListeners() {
        val buttons = listOf<Button>(
            findViewById(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8),
            findViewById(R.id.btn9)
        )

        buttons.forEach { button ->
            button.setOnClickListener {
                input.append(button.text)
                tvResult.text = input.toString() /*Gets the input value and displays the results*/
                lastNumeric = true
            }
        }
    }

    private fun setOperationButtonListeners() {
        val operators = listOf<Button>(
            findViewById(R.id.btnAdd), findViewById(R.id.btnSubtract),
            findViewById(R.id.btnMultiply), findViewById(R.id.btnDivide)
        )

        operators.forEach { button ->
            button.setOnClickListener {
                if (lastNumeric && !endsWithOperator()) {
                    input.append(button.text)
                    tvResult.text = input.toString()
                    lastNumeric = false
                    lastDot = false
                }
            }
        }
    }

    private fun setSpecialButtonListeners() {
        // Clear button
        findViewById<Button>(R.id.btnClear).setOnClickListener {
            input.clear()
            tvResult.text = "0"
            lastNumeric = false
            lastDot = false
        }

        // Equal button
        findViewById<Button>(R.id.btnEqual).setOnClickListener {
            if (lastNumeric) {
                val result = calculateResult(input.toString())
                tvResult.text = result.toString()
                input.clear().append(result)
                lastNumeric = true
                lastDot = true
            }
        }
    }

    private fun endsWithOperator(): Boolean {
        return input.endsWith("+") || input.endsWith("-") || input.endsWith("*") || input.endsWith("/")
    }

    private fun calculateResult(expression: String): Double {
        return try {
            val tokens = expression.split("(?<=[-+*/])|(?=[-+*/])".toRegex())
            val values = mutableListOf<Double>()
            val operations = mutableListOf<Char>()

            for (token in tokens) {
                when {
                    token.isDouble() -> values.add(token.toDouble())
                    token.length == 1 && "+-*/".contains(token) -> operations.add(token[0])
                }
            }

            while (operations.isNotEmpty()) {
                val index = operations.indexOfFirst { it == '*' || it == '/' }.takeIf { it >= 0 } ?: 0
                val op = operations.removeAt(index)
                val v1 = values.removeAt(index)
                val v2 = values.removeAt(index)
                val result = when (op) {
                    '+' -> v1 + v2
                    '-' -> v1 - v2
                    '*' -> v1 * v2
                    '/' -> v1 / v2
                    else -> 0.0
                }
                values.add(index, result)
            }

            values[0]
        } catch (e: Exception) {
            0.0
        }
    }

    private fun String.isDouble(): Boolean {
        return this.toDoubleOrNull() != null
    }
}
