package com.example.chapter4

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.chapter4.databinding.ActivityInputBinding

class InputActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.bloodTypeSpinner.adapter = ArrayAdapter.createFromResource(
            this,
            R.array.blood_types,
            android.R.layout.simple_list_item_1
        )

        binding.birthdateLayer.setOnClickListener {
            val listener = OnDateSetListener{ _, year, month, dayOfMonth ->
                binding.birthdateValueTextView.text = "$year-$month-$dayOfMonth"
            }
            DatePickerDialog(
                this,
                listener,
                2000,
                1,
                1
            ).show()
        }
        
        binding.warningCheckBox.setOnCheckedChangeListener { _, isChecked ->
            binding.cautionValueEditText.isVisible = isChecked
        }
        binding.cautionValueEditText.isVisible = binding.warningCheckBox.isChecked


        binding.saveButton.setOnClickListener {
            saveData()
            finish()
        }
    }

    private fun saveData(){
        //val editor = getSharedPreferences("userInformation", Context.MODE_PRIVATE).edit()
        // editor.putString(NAME, binding.nameValueEditText.text.toString())
        with(getSharedPreferences(USER_INFORMATION, Context.MODE_PRIVATE).edit()){
            putString(NAME, binding.nameValueEditText.text.toString())
            putString(BLOOD_TYPE, getBloodType())
            putString(EMERGENCY_CONTACT, binding.emergencyValueNumberEditText.text.toString())
            putString(BIRTHDATE, binding.birthdateValueTextView.text.toString())
            putString(WARNING, getWarning())
            apply() // editor.commit()보다 editor.apply()를 권장
        }

        Toast.makeText(this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun getBloodType(): String{
        val bloodAlphabet = binding.bloodTypeSpinner.selectedItem.toString()
        val bloodSign = if(binding.bloodTypePlus.isChecked)"+" else "-"
        return "$bloodSign$bloodAlphabet"
    }

    private fun getWarning(): String{
        return if (binding.warningCheckBox.isChecked) binding.cautionValueEditText.text.toString() else ""
    }

}