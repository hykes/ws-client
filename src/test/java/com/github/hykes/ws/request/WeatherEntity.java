package com.github.hykes.ws.request;

import com.github.hykes.ws.model.WsEntity;
import lombok.Data;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Desc:
 * Mail: hehaiyang@terminus.io
 * Date: 2017/11/13
 */
@Data
@XmlRootElement(name = "soapenv:Envelope")
public class WeatherEntity extends WsEntity {

    @XmlAttribute(name = "xmlns:web")
    protected String web = "http://WebXml.com.cn/";

}
