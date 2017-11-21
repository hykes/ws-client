package com.github.hykes.ws.model;

import lombok.Data;

import javax.xml.bind.annotation.*;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"header", "body"})
@XmlRootElement(name = "soapenv:Envelope")
public class WsEntity {

    @XmlAttribute(name="xmlns:soapenv")
    protected String soapenv="http://schemas.xmlsoap.org/soap/envelope/";

    @XmlElement(required = true, name="soapenv:Header")
    protected WsHeader header = new WsHeader();

    @XmlElement(required = true, name="soapenv:Body")
    protected WsBody body = new WsBody();

}
