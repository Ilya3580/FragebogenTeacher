package com.example.fragebogenteacher

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class Adapter(items:ArrayList<StudentClass>, context: Context)
    :ArrayAdapter<StudentClass>(context,R.layout.fragment_listview, items){

    private lateinit var view: View
    private lateinit var textViewSurname:TextView
    private lateinit var textViewResult:TextView
    private lateinit var textViewKey:TextView


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        view = LayoutInflater.from(context).inflate(R.layout.fragment_listview, parent, false)

        textViewResult = view.findViewById(R.id.textViewResult)
        textViewSurname = view.findViewById(R.id.textViewSurname)
        textViewKey = view.findViewById(R.id.textViewKey)

        textViewSurname.text = getItem(position)?.surname
        textViewResult.text = getItem(position)?.result
        if(getItem(position)?.result?.length!! < 15){
            textViewKey.text = "закончил(а) тест"
        } else if(getItem(position)?.key?.get(0) == '+')
        {
            textViewResult.text = "Тест не завершен"
            textViewKey.text = "начал(а) тест"
        }else{
            textViewKey.text = "ключ: " + getItem(position)?.key.toString()
        }
        return view
    }

}