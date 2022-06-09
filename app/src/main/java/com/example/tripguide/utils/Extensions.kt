package com.example.tripguide.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText


// 문자열이 제이슨 형태인지, 제이슨 배열 형태인지
fun String?.isJsonobject(): Boolean = this?.startsWith("{") == true && this.endsWith("}")

fun String?.isJsonArray(): Boolean = this?.startsWith("{") == true && this.endsWith("}")

//에딧 텍스트에 대한 익스텐션
fun EditText.onMyTextChaged(completion: (Editable?) -> Unit){
    this.addTextChangedListener(object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            completion(editable)
        }

    })
}