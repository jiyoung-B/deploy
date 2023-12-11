package com.example.project3.dto.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public abstract String getId();
    public abstract String getName();
    public abstract String getImageUrl();
    public abstract String getEmail();

}