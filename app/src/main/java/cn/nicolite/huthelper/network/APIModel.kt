package cn.nicolite.huthelper.network

import cn.nicolite.huthelper.model.bean.Weather
import cn.nicolite.huthelper.model.dao.FreshmanStrategy
import cn.nicolite.huthelper.model.wrapper.RestResult
import io.reactivex.Observable
import retrofit2.http.GET

/**
 * Created by nicolite on 2019/3/5.
 * email nicolite@nicolite.cn
 */
object APIModel {
    val freshmanStrategyAPI = retrofit.create(FreshmanStrategyAPI::class.java)
    val weatherAPI = retrofit.create(WeatherAPI::class.java)
}

interface WeatherAPI {
    //http://wthrcdn.etouch.cn/weather_mini?city=株洲
    @GET("http://wthrcdn.etouch.cn/weather_mini?city=%E6%A0%AA%E6%B4%B2")
    abstract fun getWeather(): Observable<Weather>
}

interface FreshmanStrategyAPI {
    @GET("/huthelper/getFreshmanStrategyList")
    fun getFreshmanStrategyList(): Observable<RestResult<List<FreshmanStrategy>>>
}