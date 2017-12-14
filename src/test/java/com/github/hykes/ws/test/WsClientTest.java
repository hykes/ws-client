package com.github.hykes.ws.test;

import com.github.hykes.ws.WsClient;
import com.github.hykes.ws.test.interceptor.DefaultRequestInterceptor;
import com.github.hykes.ws.test.request.WeatherMsg;
import com.github.hykes.ws.test.response.SupportCityResponse;

import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
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