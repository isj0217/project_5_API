package com.example.assignment_5;

public class ForecastUnit {

    private String fcstDate;    // 예측의 대상이 되는 날짜
    private String fcstTime;    // 예측의 대상이 되는 시간

    private String POP_value;         // 강수확률 (0~100 , %단위)
    private String PTY_value;         // 강수형태 (없음(0) / 비(1) / 비/눈(2)-진눈깨비 / 눈(3) / 소나기(4))
    private String R06_value;         // 6시간 강수량 (소수점, mm단위)
    private String REH_value;         // 습도 (5단위 움직임, %단위)
    private String S06_value;         // 6시간 신적설 (1cm 단위)
    private String SKY_value;         // 하늘 상태 (맑음(1) / 구름많음(3) / 흐림(4) )
    private String T3H_value;         // 3시간 기온 (정수, 섭씨 단위)
    private String TMN_value;         // 아침 최저기온 (소수, 섭씨 단위)
    private String TMX_value;         // 낮 최고기온 (소수, 섭씨 단위)
    private String WSD_value;         // 풍속 (소수, m/s 단위)

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public String getPOP_value() {
        return POP_value;
    }

    public void setPOP_value(String POP_value) {
        this.POP_value = POP_value;
    }

    public String getPTY_value() {
        return PTY_value;
    }

    public void setPTY_value(String PTY_value) {
        this.PTY_value = PTY_value;
    }

    public String getR06_value() {
        return R06_value;
    }

    public void setR06_value(String r06_value) {
        R06_value = r06_value;
    }

    public String getREH_value() {
        return REH_value;
    }

    public void setREH_value(String REH_value) {
        this.REH_value = REH_value;
    }

    public String getS06_value() {
        return S06_value;
    }

    public void setS06_value(String s06_value) {
        S06_value = s06_value;
    }

    public String getSKY_value() {
        return SKY_value;
    }

    public void setSKY_value(String SKY_value) {
        this.SKY_value = SKY_value;
    }

    public String getT3H_value() {
        return T3H_value;
    }

    public void setT3H_value(String t3H_value) {
        T3H_value = t3H_value;
    }

    public String getTMN_value() {
        return TMN_value;
    }

    public void setTMN_value(String TMN_value) {
        this.TMN_value = TMN_value;
    }

    public String getTMX_value() {
        return TMX_value;
    }

    public void setTMX_value(String TMX_value) {
        this.TMX_value = TMX_value;
    }

    public String getWSD_value() {
        return WSD_value;
    }

    public void setWSD_value(String WSD_value) {
        this.WSD_value = WSD_value;
    }
}
