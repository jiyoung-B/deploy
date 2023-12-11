package com.example.project3.dto.response;

import com.example.project3.entity.main.CmmMsgHeader;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "OpenAPI_ServiceResponse")
public class OpenAPI_ServiceResponse {

    private CmmMsgHeader cmmMsgHeader;

    @XmlElement(name = "cmmMsgHeader")
    public CmmMsgHeader getCmmMsgHeader() {
        return cmmMsgHeader;
    }

    public void setCmmMsgHeader(CmmMsgHeader cmmMsgHeader) {
        this.cmmMsgHeader = cmmMsgHeader;
    }
}
