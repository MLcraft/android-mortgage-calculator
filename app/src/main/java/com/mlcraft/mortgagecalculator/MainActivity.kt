package com.mlcraft.mortgagecalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mlcraft.mortgagecalculator.ui.theme.MortgageCalculatorTheme
import java.util.Locale
import kotlin.math.pow

private const val TAG = "MortgageCalculator"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MortgageCalculatorTheme {
                MortgageCalculator()
            }
        }
    }
}

@Composable
fun MortgageCalculator() {

    var sliderPosition by remember { mutableStateOf(0f) }
    var mAmount by remember { mutableStateOf("") }
    var dpAmount by remember { mutableStateOf("") }
    var monthlyPayment by remember { mutableStateOf("") }

    val radioOptions = listOf(25, 30)
    val (selectedTerm, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }


    val pattern = remember { Regex("^\\d+\$") }

    Column(
        modifier = Modifier.padding(24.dp, 64.dp).width(350.dp),
        verticalArrangement = Arrangement.spacedBy(35.dp),
        horizontalAlignment = Alignment.End
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { MortgageText()
            OutlinedTextField(
                value = mAmount,
                onValueChange = { if ((it.isEmpty() || it.matches(pattern)) && it.length <= 10) {
                    mAmount = it
                } },
                label = { Text("Label") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(150.dp),
            )}


        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { DownPaymentText()
            OutlinedTextField(
                value = dpAmount,
                onValueChange = { if ((it.isEmpty() || it.matches(pattern)) && it.length <= 10) {
                    dpAmount = it
                } },
                label = { Text("Label") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(150.dp),
            )}

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { InterestRateText()
            Row (
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row (
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(150.dp)
                ) {
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        modifier = Modifier.width(100.dp).height(20.dp),
                    )
                    Text(text = String.format("%.2f", sliderPosition * 8) + "%")
                }
            }}

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { TermText()
            Row(modifier = Modifier.selectableGroup().width(150.dp)) { radioOptions.forEach { text ->
                Row(
                    Modifier
                        .width(75.dp)
                        .height(30.dp)
                        .selectable(
                            selected = (text == selectedTerm),
                            onClick = { onOptionSelected(text) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedTerm),
                        onClick = null
                    )
                    Text(
                        text = "$text",
                        modifier = Modifier.padding(start = 5.dp)
                    )

                }
            } }}

        Button(
            onClick = {
                var currentPercentage = sliderPosition * 8 / 100
                Log.i(TAG, "Button Clicked with inputs $mAmount, $dpAmount, $currentPercentage, $selectedTerm")
                monthlyPayment = calculate(mAmount.toInt(), dpAmount.toInt(), currentPercentage.toDouble(), selectedTerm)      },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Calculate",
            )
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) { MonthlyPaymentText()
            Text(
                text = monthlyPayment,
                modifier = Modifier.width(150.dp)
            )}
    }
}

private fun calculate(mortgage: Int, downPayment: Int, interestRate: Double, termSelection: Int): String {

    val principle = mortgage - downPayment
    val per_payment_interest = interestRate / 12
    val number_of_payments = termSelection * 12

    val compounded_interest_value = (1 + per_payment_interest).pow(number_of_payments)

    Log.i(TAG, "Doing calculation with principle $principle, per payment interest $per_payment_interest, number of payments $number_of_payments, and compounded interest value $compounded_interest_value")

    // if no interest, then the monthly payment is the principle divided by the number of payments
    if (per_payment_interest == 0.0) {
        return (principle / number_of_payments).toString()
    }
    val payment = principle * ((per_payment_interest * ((1 + per_payment_interest).pow(number_of_payments))) / (((1 + per_payment_interest).pow(number_of_payments)) - 1))

    return String.format(Locale.getDefault(), "%.2f", payment)
}

@Composable
fun MortgageText() {
    Text(
        text = "Mortgage Amount",
    )
}

@Composable
fun DownPaymentText() {
    Text(
        text = "Down Payment Amount",
    )
}

@Composable
fun InterestRateText() {
    Text(
        text = "Interest Rate",
    )
}

@Composable
fun TermText() {
    Text(
        text = "Term",
    )
}

@Composable
fun MonthlyPaymentText() {
    Text(
        text = "Monthly Payment",
    )
}