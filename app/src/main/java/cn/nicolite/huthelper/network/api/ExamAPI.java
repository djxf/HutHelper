package cn.nicolite.huthelper.network.api;

import cn.nicolite.huthelper.model.bean.ExamResult;
import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * 考试计划API
 * Created by nicolite on 17-11-2.
 */

public interface ExamAPI {
    @GET("opensource/staticAPI/exam.json")
    Observable<ExamResult> getExamData();
}
