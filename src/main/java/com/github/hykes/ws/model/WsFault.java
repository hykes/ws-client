package com.github.hykes.ws.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Desc: 报文header
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/13
 */
@Data
@XmlRootElement(name = "fault")
@XmlAccessorType(XmlAccessType.FIELD)
public class WsFault {

    @XmlElement(required = true, name="faultcode")
    protected String faultCode;

    @XmlElement(required = true, name="faultstring")
    protected String faultString;

    @XmlElement(required = true, name="detail")
    protected String detail;

}