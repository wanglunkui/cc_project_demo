package com.wlk.android.club.http;

import java.io.Serializable;

/**
 * Created by wanglunkui on 2018/1/18.
 */

public class AuthorizationJSON implements Serializable {
    // AuthorizationJSON
    public String access_token;
    public String token_type;
    public String expire_in;
    public String refresh_token;
    public String scope;
    public String timestamp;
    public boolean new_created = false;

    public String user_id;

    // ResponseJSON<?>
    public String status;
    public String description;

    // TokenErrorJSON
    public int error_code;
}