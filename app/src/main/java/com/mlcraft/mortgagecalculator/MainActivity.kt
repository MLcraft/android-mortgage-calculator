package com.mlcraft.mortgagecalculator

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.util.Locale
import kotlin.math.pow

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {
    private lateinit var etMortgageAmount: EditText
    private lateinit var etDownPaymentAmount: EditText
    private lateinit var sbInterestRate: SeekBar
    private lateinit var tvInterestRateLabel: TextView
    private lateinit var rgTermSelection: RadioGroup
    private lateinit var tvMonthlyPayment: TextView
    private lateinit var bCalculate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.layout)
        etMortgageAmount = findViewById(R.id.etMortgageAmount)
        etDownPaymentAmount = findViewById(R.id.etDownPaymentAmount)
        sbInterestRate = findViewById(R.id.sbInterestRate)
        tvInterestRateLabel = findViewById(R.id.tvInterestRateLabel)
        rgTermSelection = findViewById(R.id.rgTermSelection)
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment)
        bCalculate = findViewById(R.id.bCalculate)

        var progressValue = 5.0f;

        sbInterestRate.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.i(TAG, "onProgressChanged $progress")
                progressValue = (progress + 1) * 0.01f
                val percentageProgress = String.format(Locale.getDefault(), "%.2f", progressValue) + "%"
                tvInterestRateLabel.text = percentageProgress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        bCalculate.setOnClickListener { calculate() }

    }

    private fun calculate() {
        val mortgage = etMortgageAmount.text.toString().toDouble()
        val downPayment = etDownPaymentAmount.text.toString().toDouble()
        val interestRate = (sbInterestRate.progress.toString().toDouble() + 1) * 0.01f
        val termSelection = findViewById<RadioButton>(rgTermSelection.checkedRadioButtonId).text.toString().toDouble()

        Log.i(TAG, "CALCULATION BUTTON WAS PRESSED!!! With these params: mortgage $mortgage, down payment $downPayment, interest rate $interestRate, and term $termSelection")

        val principle = mortgage - downPayment
        val per_payment_interest = interestRate / 1200
        val number_of_payments = termSelection * 12

        val compounded_interest_value = (1 + per_payment_interest).pow(number_of_payments)

        Log.i(TAG, "Doing calculation with principle $principle, per payment interest $per_payment_interest, number of payments $number_of_payments, and compounded interest value $compounded_interest_value")

        val payment = principle * ((per_payment_interest * ((1 + per_payment_interest).pow(number_of_payments))) / (((1 + per_payment_interest).pow(number_of_payments)) - 1))

        tvMonthlyPayment.text = String.format(Locale.getDefault(), "%.2f", payment)
    }
}
