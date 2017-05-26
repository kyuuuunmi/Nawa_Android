package org.sopt.nawa_103.Network;


import org.sopt.nawa_103.Model.DB.MemberContent;
import org.sopt.nawa_103.Model.DB.MemoContent;
import org.sopt.nawa_103.Model.DB.MsgInfo;
import org.sopt.nawa_103.Model.DB.MsgModel;
import org.sopt.nawa_103.Model.DB.PushModel;
import org.sopt.nawa_103.Model.DB.PushSender;
import org.sopt.nawa_103.Model.DB.ScheduleContent;
import org.sopt.nawa_103.Model.DB.ScheduleTmp;
import org.sopt.nawa_103.Model.DB.TodayModel;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * GET 방식과 POST 방식의 사용법을 잘 이해하셔야 합니다.
 * GET("/경로") 경로는 서버 파트에게 물어보세요. (※baseUrl 뒤에 붙는 경로입니다.ex) http://baseUrl/경로)
 * ("/경로/{식별자}) ~~(@Path{"식별자"} String value) 어떤 식별자를 통해 리소스를 구분하여 요청합니다. uri의 정의 기억나시죠? ex) http://baseUrl/경로/value
 * POST 방식은 @Body 에 뭔가를 담아서 보내야하죠?
 */
public interface NetworkService {

    @POST("/login")
    Call<MemberContent> loginMemberContent(@Body MemberContent memberContent);

    @POST("/join")
    Call<MemberContent> joinMemberContent(@Body MemberContent memberContent);

    //회원가입할때 아이디 중복확인
    @GET("/join/{user_id}")
    Call<Void> joinIdCheck(@Path("user_id") String id);


    @POST("/logout/{p_id}")
    Call<Void> logout(@Path("p_id") long p_id);


    @GET("/schedule/{p_id}/{year}/{month}")
    Call<List<ScheduleContent>> getDateScheduleContent(@Path("p_id") long p_id, @Path("year") long year, @Path("month") long month);

    // 사용자 고유 ID와 일정 고유 ID인 s_id 요청해서 일정 관련 정보 얻어오기 -> 달력 밑 간단 정보, 상세보기 할 때
    // 응답: 일정관련 정보 모두s
    @GET("/schedule/{s_id}")
    Call<List<ScheduleTmp>> getInfoScheduleContent(@Path("s_id") long s_id);

    // 새로운 일정 추가
    @POST("/schedule")
    Call<ScheduleContent> insertScheduleContent(@Body ScheduleContent scheduleContent);

    // 기존 일정 수정
    @POST("/schedule/{s_id}/update")
    Call<ScheduleContent> updateScheduleContent(@Path("s_id") long s_id, @Body ScheduleContent scheduleContent);

    //기존 일정 삭제
    @POST("/schedule/{s_id}/delete")
    Call<ScheduleContent> deleteScheduleContent(@Path("s_id") long s_id);

    //달력에 년, 월, 사용자고유 ID 요청해서 날짜 얻어오기
    // 응답: 메모 고유 ID(m_id), 날짜(date)
    @GET("/memo/{p_id}/{year}/{month}")
    Call<List<MemoContent>> getDateMemoContent(@Path("p_id") long p_id, @Path("year") long year, @Path("month") long month);

    // 사용자 고유 ID와 메모 고유 ID인 m_id 요청해서 메모 관련 정보 얻어오기
    // 응답: 해당 메모관련 정보 모두
    @GET("/memo/{p_id}/{m_id}")
    Call<MemoContent> getInfoMemoContent(@Path("p_id") long p_id, @Path("m_id") long m_id);

    // 새로운 메모 추가
    @POST("/memo")
    Call<MemoContent> insertMemoContent(@Body MemoContent memoContent);

    // 기존 메모 수정
    @POST("/memo/{m_id}/update")
    Call<MemoContent> updateMemoContent(@Path("m_id") long m_id, @Body MemoContent memoContent);

    // 기존 메모 삭제
    @POST("/memo/{m_id}/delete")
    Call<MemoContent> deleteMemoContent(@Path("m_id") long m_id);

    //기존 일정 삭제
    @POST("/schedule/{p_id}/{s_id}/delete")
    Call<ScheduleContent> deleteScheduleContent(@Path("p_id") long p_id,@Path("s_id") long s_id);

    // 일정 검색
    @GET("/schedule/allSearch/{p_id}")
    Call<List<ScheduleContent>> searchScheduleContent(@Path("p_id") long p_id);

    //일정등록푸시
    @POST("/push")
    Call<PushModel> push(@Body PushModel pushModel);

    //쪽지보내기
    @POST("/push/quick")
    Call<MsgModel> sendMsg(@Body MsgModel msgModel);

    //푸시보관함 오늘의약속
    @GET("/push/{p_id}/{year}/{month}/{date}")
    Call<List<TodayModel>> getTodaySchedule(@Path("p_id") long p_id, @Path("year") long year,
                                            @Path("month") long month, @Path("date") long date);

    //오늘 이전 푸시 삭제
    @POST("/push/{p_id}/delete")
    Call<PushSender> deletePushList(@Path("p_id") long p_id);

    //푸시 리스트 불러오기
    @GET("/push/pushlist/{p_id}")
    Call<ArrayList<PushSender>> getPushList(@Path("p_id") long p_id);


    //쪽지 아이템 클릭 - 일정제목(title), 쪽지내용(msg), 쪽지보낸시간(pushtime)
    @GET("/push/quickinfo/{p_id}/{push_id}")
    Call<MsgInfo> getMsgItem(@Path("p_id") long p_id, @Path("push_id") long push_id);

}


