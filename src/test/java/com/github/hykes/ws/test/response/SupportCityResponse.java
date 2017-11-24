package com.github.hykes.ws.test.response;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getSupportCityResponse")
public class SupportCityResponse {

    @XmlElementWrapper(name = "getSupportCityResult")
    @XmlElement(name = "string")
    private List<String> strings = new ArrayList<>();

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }
}