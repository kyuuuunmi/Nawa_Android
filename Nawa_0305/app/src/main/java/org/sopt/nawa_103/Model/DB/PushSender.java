package org.sopt.nawa_103.Model.DB;

/**
 * Created by jiyeon on 2016-02-22.
 */
public class PushSender {

    int ps_id;

    int s_id; // 푸시 상세보기할 때
    int push_id; //쪽지 상세보기 할 때
    int p_id; //쪽지 상세보기 할 때
    String name;
    int pmcheck; //0이면 푸시, 1이면 쪽지

    public PushSender(int s_id, int push_id, int p_id, String name, int pmcheck) {
        this.s_id = s_id;
        this.push_id = push_id;
        this.p_id = p_id;
        this.name = name;
        this.pmcheck = pmcheck;
    }

    public int getPs_id() {
        return ps_id;
    }

    public void setPs_id(int ps_id) {
        this.ps_id = ps_id;
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public int getPush_id() {
        return push_id;
    }

    public void setPush_id(int push_id) {
        this.push_id = push_id;
    }

    public int getP_id() {
        return p_id;
    }

    public void setP_id(int p_id) {
        this.p_id = p_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPmcheck() {
        return pmcheck;
    }

    public void setPmcheck(int pmcheck) {
        this.pmcheck = pmcheck;
    }
}