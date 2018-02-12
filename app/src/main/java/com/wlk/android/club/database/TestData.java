package com.wlk.android.club.database;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by wanglunkui on 2018/1/18.
 */
@Entity
public class TestData {
    @Id(autoincrement = true)
    private Long id;
    private String end_time;
    @Transient
    private String end_timea;
    @Generated(hash = 1665807378)
    public TestData(Long id, String end_time) {
        this.id = id;
        this.end_time = end_time;
    }
    @Generated(hash = 1580692206)
    public TestData() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEnd_time() {
        return this.end_time;
    }
    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }
}
