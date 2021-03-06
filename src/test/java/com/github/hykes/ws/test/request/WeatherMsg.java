package com.github.hykes.ws.test.request;

import javax.xml.bind.annotation.*;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
@XmlRootElement(name = "getSupportCity", namespace = "http://WebXml.com.cn/")
@XmlAccessorType(XmlAccessType.FIELD)
public class WeatherMsg {

    @XmlElement(name="byProvinceName")
    private String name;

    @XmlTransient
    private String age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
