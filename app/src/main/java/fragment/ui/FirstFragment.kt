package fragment.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.tripguide.R
import kotlinx.android.synthetic.main.fragment_first.*

class FirstFragment : Fragment(){
    val TAG: String = "로그"
    lateinit var navController: NavController //네비게이션 컨트롤러 선언

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "FirstFragment - onCreateView() called")
        return inflater.inflate(R.layout.fragment_first, container, false)
    }
}