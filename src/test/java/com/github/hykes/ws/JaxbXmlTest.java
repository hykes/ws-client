package com.github.hykes.ws;


import com.github.hykes.ws.model.WsBody;
import com.github.hykes.ws.model.WsFault;
import com.github.hykes.ws.request.WeatherEntity;
import com.github.hykes.ws.request.WeatherMsg;
import com.github.hykes.ws.response.SupportCityResponse;
import com.github.hykes.ws.response2.SupportCityResponse2;

import javax.xml.bind.JAXBElement;

/**
 * Desc:
 * Mail: hehaiyang@terminus.io
 * Date: 2017/11/10
 */
public class JaxbXmlTest {

    public static void main(String[] args) throws Exception {

        beanToXml();
//        xml();

//        xmlToBean("ss");
    }

    public static String beanToXml() throws Exception{
        WeatherEntity entity = new WeatherEntity();
        WsBody body = new WsBody();
        WeatherMsg weatherMsg = new WeatherMsg();
        weatherMsg.setName("北京");
        body.setMsg(weatherMsg);

        entity.setBody(body);

        String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?wsdl";
        String msg = WsClient.convertToXml(entity, entity.getClass(), weatherMsg.getClass());

        System.out.println(msg);
        String result = WsClient.requestSoap1_1(url, msg);
        System.out.println(result);
        return result;
    }

    public static void xmlToBean(String xml) throws Exception{

        xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<soap:Body>" +
                "<getSupportCityResponse xmlns=\"http://WebXml.com.cn/\">" +
                "<getSupportCityResult>" +
                "<string>北京 (54511)</string>" +
                "<string>上海 (58367)</string>" +
                "<string>天津 (54517)</string>" +
                "<string>重庆 (57516)</string>" +
                "</getSupportCityResult>" +
                "</getSupportCityResponse>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        JAXBElement<SupportCityResponse> element = WsClient.convertToJavaBean(xml, SupportCityResponse.class);

        element.getValue();

        String xml2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
                "<soap:Body>" +
                "<getSupportCity xmlns=\"http://WebXml.com/\">" +
                "<getSupportCityResult>" +
                "<string>北京 (54511)</string>" +
                "<string>上海 (58367)</string>" +
                "<string>天津 (54517)</string>" +
                "<string>重庆 (57516)</string>" +
                "</getSupportCityResult>" +
                "</getSupportCity>" +
                "</soap:Body>" +
                "</soap:Envelope>";

        JAXBElement<SupportCityResponse2> element2 = WsClient.convertToJavaBean(xml2, SupportCityResponse2.class);

        element2.getValue();

        String xml3 = "<?xml version='1.0' encoding='UTF-8'?>  \n" +
                "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">  \n" +
                "    <S:Body>  \n" +
                "        <S:Fault xmlns:ns4=\"http://www.w3.org/2003/05/soap-envelope\">  \n" +
                "            <faultcode>  \n" +
                "                S:Client  \n" +
                "            </faultcode>  \n" +
                "            <faultstring>  \n" +
                "                找不到{}sayHelloWorldFrom的分派方法  \n" +
                "            </faultstring>  \n" +
                "        </S:Fault>  \n" +
                "    </S:Body>  \n" +
                "</S:Envelope>  ";

        JAXBElement<WsFault> element3 = WsClient.convertToJavaBean(xml3, WsFault.class);

        element3.getValue();
        System.out.println();

    }

}