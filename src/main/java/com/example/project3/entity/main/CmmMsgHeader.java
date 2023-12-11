package com.example.project3.entity.main;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmmMsgHeader {
    private String errMsg;
    private String returnAuthMsg;
    private int returnReasonCode;
}
