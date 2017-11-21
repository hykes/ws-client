package com.github.hykes.ws.response2;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Mail: hehaiyang@terminus.io
 * Date: 2017/11/13
 */
@XmlRootElement(name = "getSupportCity")
public class SupportCityResponse2 {

    @XmlElementWrapper(name = "getSupportCityResult")
    @XmlElement(name = "string")
    private List<String> strings = new ArrayList();

    public List<String> getStrings() {
        return strings;
    }

}