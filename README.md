### 一个简单的web service客户端

- jdk8
- 链式调用

```java
public class WsClientTest {

    public static void main(String[] args) throws Exception {

        WeatherMsg weatherMsg = new WeatherMsg();
        weatherMsg.setName("北京");
        weatherMsg.setAge("123");

        WsClient wsClient = new WsClient();
        wsClient.addRequestHandler(new DefaultRequestInterceptor());
        wsClient.protocol(SOAPConstants.SOAP_1_2_PROTOCOL)
                .wsdl("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl")
                .method("getSupportCity").namespace("http://WebXml.com.cn/")
                .object(weatherMsg, WeatherMsg.class).send();
//                .convert(SupportCityResponse.class);

        System.out.println(wsClient.getRequestXml());
        System.out.println(wsClient.getResponseXml());
    }

}
```