package com.github.hykes.ws.request;

import com.github.hykes.ws.model.WsMsg;
import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Desc:
 * Mail: hehaiyang@terminus.io
 * Date: 2017/11/13
 */
@Data
@XmlRootElement(name = "web:getSupportCity")
public class WeatherMsg extends WsMsg {

    @XmlElement(required = true, name="web:byProvinceName")
    private String name;
}
