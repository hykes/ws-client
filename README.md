### 一个简单的web service客户端

- 支持soap1.1和soap1.2协议
- jdk6+
- 依赖com.github.kevinsawicki:http-request-6.0.jar
- 链式调用

```java
public class WsClientTest {

    public static void main(String[] args) throws Exception {

        WeatherMsg weatherMsg = new WeatherMsg();
        weatherMsg.setName("北京");

        WsClient wsClient = new WsClient();
        wsClient.addRequestHandler(new DefaultRequestHandler());
        SupportCityResponse response = wsClient.wsdl("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl")
                .namespace("http://WebXml.com.cn/").method("getSupportCity")
                .object(weatherMsg, WeatherMsg.class).send().convert(SupportCityResponse.class);

        System.out.println(wsClient.getOriginXml());
        System.out.println(wsClient.getResultXml());
        System.out.println();
    }

}
```