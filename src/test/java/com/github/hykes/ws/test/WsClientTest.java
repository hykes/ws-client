package com.github.hykes.ws.test;


import com.github.hykes.ws.WsClient;
import com.github.hykes.ws.test.handler.DefaultRequestHandler;
import com.github.hykes.ws.test.request.WeatherMsg;
import com.github.hykes.ws.test.response.SupportCityResponse;

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
        wsClient.addRequestHandler(new DefaultRequestHandler());
        SupportCityResponse response = wsClient.wsdl("http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl")
                .namespace("http://WebXml.com.cn/").method("getSupportCity")
                .object(weatherMsg, WeatherMsg.class).send().convert(SupportCityResponse.class);

        System.out.println(wsClient.getOriginXml());
        System.out.println(wsClient.getResultXml());
        System.out.println();
    }

}