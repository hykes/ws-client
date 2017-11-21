### 一个简单的web service客户端

- 支持soap1.1和soap1.2协议
- jdk6+，jaxbs使用注解方式生成xml
- 依赖com.github.kevinsawicki:http-request

#### 使用方式

1. 编写请求实体类，封装消息体

```java
@Data
@XmlRootElement(name = "web:getSupportCity")
public class WeatherMsg extends WsMsg {

    @XmlElement(required = true, name="web:byProvinceName")
    private String name;
}
```

```java
WeatherEntity entity = new WeatherEntity();
WsBody body = new WsBody();
WeatherMsg weatherMsg = new WeatherMsg();
weatherMsg.setName("北京");
body.setMsg(weatherMsg);
entity.setBody(body);
```

2. 生成请求报文，发起请求

```java 
String msg = WsClient.convertToXml(entity, entity.getClass(), weatherMsg.getClass());
```

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<soapenv:Envelope xmlns:web="http://WebXml.com.cn/" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
    <soapenv:Header/>
    <soapenv:Body>
        <web:getSupportCity xsi:type="weatherMsg" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <web:byProvinceName>北京</web:byProvinceName>
        </web:getSupportCity>
    </soapenv:Body>
</soapenv:Envelope>
```

```java
String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
String result = WsClient.requestSoap1_1(url, msg);
```

```xml
<?xml version="1.0" encoding="utf-8"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    <soap:Body>
        <getSupportCity xmlns="http://WebXml.com/">
            <getSupportCityResult>
                <string>北京 (54511)</string>
                <string>上海 (58367)</string>
                <string>天津 (54517)</string>
                <string>重庆 (57516)</string>
            </getSupportCityResult>
        </getSupportCity>
    </soap:Body>
</soap:Envelope>
```

3. 编写响应实体类，解析响应报文

```java
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "getSupportCityResponse")
public class SupportCityResponse {

    @XmlElementWrapper(name = "getSupportCityResult")
    @XmlElement(name = "string")
    private List<String> strings = new ArrayList();

}
```

```java
JAXBElement<SupportCityResponse> element = WsClient.convertToJavaBean(xml, SupportCityResponse.class);
```