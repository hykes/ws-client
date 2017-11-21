package com.github.hykes.ws.response;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 * Mail: hehaiyang@terminus.io
 * Date: 2017/11/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getSupportCityResponse")
public class SupportCityResponse {

    @XmlElementWrapper(name = "getSupportCityResult")
    @XmlElement(name = "string")
    private List<String> strings = new ArrayList();

}