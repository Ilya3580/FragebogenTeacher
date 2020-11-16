package com.example.fragebogenteacher

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var dataBase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference
    var lstA = ArrayList<StudentClass>()
    private lateinit var button: Button
    private lateinit var buttonAdd:Button
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView = findViewById(R.id.listView)
        button = findViewById(R.id.button)
        buttonAdd = findViewById(R.id.addButton)
        textView  = findViewById(R.id.textView)
        dataBase = Firebase.database
        myRef = dataBase.reference
        generateList()

        addButton.setOnClickListener {
            alertDialogPassword()
        }

        button.setOnClickListener {
            alertDialogGenerate()
        }

        listView.setOnItemClickListener{ parent, view, position, id ->
            val clipboard =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("", lstA[position].key.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "ключ скопирован", Toast.LENGTH_LONG).show()
        }
        
        listView.setOnItemLongClickListener { parent, view, position, id ->
            alertDialog("Сгенерировать новый ключ для: " + lstA[position].surname.toString(), position)
            return@setOnItemLongClickListener true
        }

    }

    private fun generateKey():String
    {
        var firstKey= (1000000000..1900000000).random()
        var i = 0
        while (i < lstA.count())
        {
            var keyt = lstA[i].key.toString()
            if(keyt == firstKey.toString(16))
            {
                i=0
                firstKey= (1000000000..1900000000).random()
            }else {
                i++
            }
        }
        return firstKey.toString(16)
    }

    private fun generateKeys(){
        var firstKey = (1000000000..1900000000).random()
        for(i in (0 until lstA.size))
        {
            firstKey += 1000
            lstA[i].key = firstKey.toString(16)
            lstA[i].result = "не начинал(а) тестирование"
            myRef.child("students").child(lstA[i].surname).setValue(lstA[i])
        }


    }
    private fun generateList() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var count = 0
                lstA.clear()
                for (ds in dataSnapshot.children) {
                    if(ds.key== "students") {
                        for (dsChild in ds.children) {
                            var mas = ArrayList<String>()
                            for(dsChilChild in dsChild.children)
                            {
                                mas.add(dsChilChild.value.toString())
                            }
                            if(mas.count() == 3) {
                                if(mas[0][0] == '+' && mas[1].length > 15)
                                {
                                    count++
                                }
                                lstA.add(StudentClass(mas[0], mas[1], mas[2]))
                            }else{
                                var user = StudentClass(generateKey(), "не начинал(а) тестирование", dsChild.key.toString())
                                myRef.child("students").child(user.surname).setValue(user)
                            }
                        }

                    }
                }
                listView.adapter = Adapter(lstA, applicationContext)
                textView.text = "Количество участников: ${lstA.size}\nПишут тест: ${count}"

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAGA", "Failed to read value.", error.toException())
            }
        })
    }
    private fun alertDialog(text:String, id: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(text)
        builder.setPositiveButton("OK") { dialog, which ->
            if(lstA[id].surname == "aaa")
            {
                lstA[id].key = "123"
            }else{
                lstA[id].key = generateKey()
            }

            lstA[id].result = "не начинал(а) тестирование"
            myRef.child("students").child(lstA[id].surname).setValue(lstA[id])
        }.setNegativeButton("Отмена") { dialog, which ->
        }

        builder.show()
    }
    private fun alertDialogPassword() {
        val builder =  AlertDialog.Builder(this)
        builder.setTitle("Введите фамилию")
        val input = EditText(this)
        input.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        builder.setView(input)
        builder.setPositiveButton("OK") { dialog, which ->
            var user = StudentClass(generateKey(), "не начинал(а) тестирование", input.text.toString())
            myRef.child("students").child(user.surname).setValue(user)
        }
        builder.show()
    }
    private fun alertDialogGenerate() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Сгенировать ключи?")
        builder.setMessage("Вы уверенны, что хотите сгенировать ключи всем участникам?")
        builder.setPositiveButton("OK") { dialog, which ->
            generateKeys()
        }.setNegativeButton("Отмена") { dialog, which ->
        }

        builder.show()
    }
}
