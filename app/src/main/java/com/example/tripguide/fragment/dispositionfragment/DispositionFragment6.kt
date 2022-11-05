package com.example.tripguide.fragment.dispositionfragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import com.example.tripguide.MainActivity
import com.example.tripguide.R
import com.example.tripguide.databinding.FragmentDisposition6Binding
import com.example.tripguide.model.*
import com.example.tripguide.model.kakaoroute.Destination
import com.example.tripguide.model.kakaoroute.KakaoRoute
import com.example.tripguide.model.kakaoroute.Origin
import com.example.tripguide.retrofit.RetrofitRoute
import com.example.tripguide.fragment.RecommendedTripFragment
import com.example.tripguide.model.SelectViewModel
import com.example.tripguide.model.kakaoroute.Route
import com.example.tripguide.utils.Constants.TAG
import com.example.tripguide.utils.KakaoApi
import kotlinx.coroutines.*
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import kotlin.collections.ArrayList

class DispositionFragment6 : Fragment(), View.OnClickListener {
    private lateinit var mainActivity : MainActivity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    private var mBinding: FragmentDisposition6Binding? = null
    private val binding get() = mBinding!!
    private lateinit var viewModel: SelectViewModel

    private var origin = Origin("", 0.0, 0.0)

    private val tourDestination = ArrayList<Destination>()
    private val foodDestination = ArrayList<Destination>()
    private val hotelDestination = ArrayList<Destination>()

    private val responseRouteList = ArrayList<RouteResult>()


    private val finalRoute = ArrayList<SelectItem>()


    var departStationTime : LocalTime = LocalTime.of(0, 0, 0)
    var arriveStationTime : LocalTime = LocalTime.of(0, 0, 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d(TAG, "DispositionFragment6 - onCreateView() called")
        // View Model 설정
        viewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()) .get(
            SelectViewModel::class.java)
        mBinding = FragmentDisposition6Binding.inflate(inflater, container, false)
        return binding.root
    }
    // 여행의 시작의 기본 시간을 오전 8시로 설정
    var startTime : LocalTime = LocalTime.of(8, 0, 0)
    var liveTime : LocalTime = LocalTime.of(8, 0, 0)
    var stationDurationTime: LocalTime = LocalTime.of(0, 0, 0)
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.beforebtn6.setOnClickListener(this)
        clickShareBtn()

        val tourList = viewModel.tourList.value
        val foodList = viewModel.foodList.value
        val hotelList = viewModel.hotelList.value
        val departRegion = viewModel.departRegion.value
        val departStationList = viewModel.departStationList.value
        val arriveStationList = viewModel.arriveStationList.value
        if(viewModel.departStationTime.value != null) {
            departStationTime = viewModel.departStationTime.value!!
        }
        if(viewModel.arriveStationTime.value != null) {
            arriveStationTime = viewModel.arriveStationTime.value!!
        }

        // 여행의 출발주소를 origin 으로 설정
        departRegion?.forEach {
            origin.name = it.title
            origin.x = it.mapX?.toDouble()
            origin.y = it.mapY?.toDouble()
            Log.d(TAG, "origin - $origin")
            finalRoute.add(it)
        }

        /* 비행기 or 기차를 이용해서 공항이나 역을 가야하면 origin 에서 공항이나 역까지 거리를 getResultSearch 로 구하고
           이전에 tourDestination 에 집어넣었던 공항이나 역값을 제거하고 공항이나 역을 finalRoute 에 add 한다. */
        if (!departStationList?.isEmpty()!!) {
            Log.d(TAG, "departStationList - $departStationList")
            tourDestination.add(Destination(departStationList.first().title,
                departStationList.first().mapX?.toDouble(),
                departStationList.first().mapY?.toDouble()
                ))
            GlobalScope.launch {
                addFinalRoute(getResultSearch(origin, tourDestination.first()), tourDestination, departStationList)
            }


            /* 비행기 or 기차를 이용해서 공항이나 역을 가야하면 도착역을 여행지에서의 일정의 첫번째로 설정하고
               도착시간에서 출발시간을 뺀 값을 초 단위로 duration 에 넣어준다. */
            Log.d(TAG, "arriveStationList - $arriveStationList")
            Log.d(TAG, "departStationTime - $departStationTime, arriveStationTime - $arriveStationTime")
            arriveStationList?.forEach {
                liveTime = LocalTime.of(arriveStationTime.hour, arriveStationTime.minute, 0)
                stationDurationTime = arriveStationTime.minusHours(departStationTime.hour.toLong())
                    .minusMinutes(departStationTime.minute.toLong())
                it.duration = stationDurationTime.hour * 60 * 60 + stationDurationTime.minute * 60
                Log.d(TAG, "stationDurationTime - $stationDurationTime")
                finalRoute.add(it)
                origin.name = it.title
                origin.x = it.mapX?.toDouble()
                origin.y = it.mapY?.toDouble()
            }
        }
        // 비행기 or 기차를 이용하지 않는다면 도착지 여행지 값을 기준으로 해서 이동시간을 계산해 liveTime 을 설정한다.
        else {

        }


        /* 여행지, 식당, 숙소를 입력받은 viewModel 을 경로 계산을 위한 배열 영식으로 바꿔서 넣어준다. */
        tourList?.map {
            tourDestination.add(Destination(it.title, it.mapX?.toDouble(), it.mapY?.toDouble()))
        }
        foodList?.map {
            foodDestination.add(Destination(it.title, it.mapX?.toDouble(),
                it.mapY?.toDouble()))
        }
        hotelList?.map {
            hotelDestination.add(Destination(it.title, it.mapX?.toDouble(), it.mapY?.toDouble()))
        }

        val count = tourList?.count()!! + foodList?.count()!! + hotelList?.count()!!

        var breakFast = 0
        var lunch = 0
        var dinner = 0

        /* Origin 과 여행지 목록을 바탕으로 finalRoute 를 계산한다. */
        for (i in 1..count) {
            /* liveTime 을 기준으로 */
            var type = ""
            type = when(liveTime.hour * 60 + liveTime.minute) {
                in (0..420) -> "tour" // 0시부터 7시에는 여행 시작
                in 421..540 -> "food" // 7시부터 9시는 아침 식사 시간
                in 541..720 -> "tour" // 9시부터 12시는 여행 시간
                in 721..840 -> "food" // 12시부터 2시는 점심 시간
                in 841..1200 -> "tour" // 2시부터 6시는 여행 시간
                in 1201..1320 -> "food" // 6시부터 8시는 저녁 시간
                else -> "hotel" // 8시 이후는 숙소
            }
            when(type) {
                "tour" -> {
                    Log.d(TAG, "liveTime - $liveTime")
                    Log.d(TAG, "tourDestination - $tourDestination")
                    var minLocation = RouteResult(null, 10000000)
                    var nextLocation = RouteResult(null, null)

                    for(destination in tourDestination) {
                        runBlocking {
                            nextLocation = getResultSearch(origin, destination)
                        }

                        if (minLocation.duration!! > nextLocation.duration!!) {
                            minLocation = nextLocation
                        }
                    }

                    Log.d(TAG, "minLocation - $minLocation")
                    addFinalRoute(minLocation, tourDestination, tourList)
                    tourList.map {
                        if(it.title == minLocation.key) {
                            tourDestination.remove(Destination(it.title, it.mapX?.toDouble(), it.mapY?.toDouble()))
                        }
                    }
                }
                "food" -> {
                    Log.d(TAG, "liveTime - $liveTime")
                    Log.d(TAG, "foodDestination - $foodDestination")
                    var minLocation = RouteResult(null, 10000000)
                    var nextLocation = RouteResult(null, null)

                    for(destination in foodDestination) {
                        runBlocking {
                            nextLocation = getResultSearch(origin, destination)
                        }

                        if (minLocation.duration!! > nextLocation.duration!!) {
                            minLocation = nextLocation
                        }
                    }

                    Log.d(TAG, "minLocation - $minLocation")
                    addFinalRoute(minLocation, foodDestination, foodList)
                    foodList.map {
                        if(it.title == minLocation.key) {
                            foodDestination.remove(Destination(it.title, it.mapX?.toDouble(), it.mapY?.toDouble()))
                        }
                    }
                }
                "hotel" ->  {
                    Log.d(TAG, "liveTime - $liveTime")
                    Log.d(TAG, "hotelDestination - $hotelDestination")
                    var minLocation = RouteResult(null, 10000000)
                    var nextLocation = RouteResult(null, null)

                    for(destination in hotelDestination) {
                        runBlocking {
                            nextLocation = getResultSearch(origin, destination)
                        }

                        if (minLocation.duration!! > nextLocation.duration!!) {
                            minLocation = nextLocation
                        }
                    }

                    Log.d(TAG, "minLocation - $minLocation")
                    addFinalRoute(minLocation, hotelDestination, hotelList)
                    hotelList.map {
                        if(it.title == minLocation.key) {
                            hotelDestination.remove(Destination(it.title, it.mapX?.toDouble(), it.mapY?.toDouble()))
                        }
                    }
                }
            }
        }
    }

    object RetrofitClient {
        val retrofitRoute : RetrofitRoute by lazy {
            Retrofit.Builder()
                .baseUrl( "https://apis-navi.kakaomobility.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitRoute::class.java)
        }
    }

    sealed class Result<out T: Any> {
        data class Success<out T : Any>(val data: T) : Result<T>()
        data class Error(val exception: String) : Result<Nothing>()
    }

    suspend fun <T : Any> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val myResp = call.invoke()

            if (myResp.isSuccessful) {
                Result.Success(myResp.body()!!)
            } else {
                Result.Error(myResp.message() ?: "Something goes wrong")
            }

        } catch (e: Exception) {
            Result.Error(e.message ?: "Internet error runs")
        }
    }

    // Origin 에 가장 가까운 Destination Return
    private fun getResultSearch(origin: Origin, destination: Destination) : RouteResult {
        val originstring = "${origin.x},${origin.y},name=${origin.name}"
        val destinationstring = "${destination.x},${destination.y},name=${destination.name}"
        var nextLocation = RouteResult(null, null)
        runBlocking {
            GlobalScope.launch {
                // Result가 성공이냐 실패냐에 따라 동작처리
                when (val result = safeApiCall {
                    RetrofitClient.retrofitRoute.getKakaoRoute(KakaoApi.API_KEY,
                        originstring,
                        destinationstring,
                        "TIME",
                        true)
                }) {
                    is Result.Success -> {
                        nextLocation = addItems(result.data)
                    }
                    is Result.Error -> {
                    }
                }

            }.join()
        }
        return nextLocation
    }


    private fun addItems(searchResult: KakaoRoute?) : RouteResult {
        val nextLocation = RouteResult(null, null)
        if (!searchResult?.routes.isNullOrEmpty()) {
            // Search results available
            for (route in searchResult!!.routes) {
                // 우리는 Origin 에서 가장 가까운 지역만 알면 되기 때문에 첫번째 값만 가진다.
                if (route.result_msg == "길찾기 성공") {
                    nextLocation.key = route.summary.destination.name
                    nextLocation.duration = route.summary.duration
                }
                else Log.d(TAG, "길찾기 실패")
            }
        }
        return nextLocation
    }

    private fun addFinalRoute(nextLocation: RouteResult, destinations: ArrayList<Destination>, list: List<SelectItem>?) {
        destinations.map { x ->
            if( x.name == nextLocation.key ){
                list?.map { y ->
                    if(y.title == x.name) {
                        y.duration = nextLocation.duration
                        liveTime = liveTime.plusSeconds(nextLocation.duration!!.toLong())
                                           .plusHours(2)
                        finalRoute.add(y)
                        Log.d(TAG, "finalRoute - $finalRoute")
                    }
                }
            }
        }
    }

    private fun clickShareBtn(){
        binding.btnshare.setOnClickListener {
            try {
                val sendText = "TripGuide 공유하기"
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, sendText)
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, "Share"))
            } catch (ignored: ActivityNotFoundException) {
                Log.d("test", "ignored : $ignored")
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.beforebtn6 -> {
                mainActivity.removeFragment(RecommendedTripFragment())
            }
        }
    }


}