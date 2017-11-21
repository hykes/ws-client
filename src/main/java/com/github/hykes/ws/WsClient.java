package com.github.hykes.ws;

import com.github.hykes.ws.exception.WsException;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Strings;

import javax.xml.bind.*;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.*;

/**
 * Desc: Web Service 通用客户端
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/13
 */
public class WsClient {

    final public static String SOAP1_1_CONTENT_TYPE = "text/xml; charset=utf-8";

    final public static String SOAP1_2_CONTENT_TYPE = "application/soap+xml; charset=utf-8";

    final public static String DEFAULT_ENCODING = "UTF-8";

    public static String requestSoap1_1(String wsdlUrl, String msg) throws Exception{
        return request(wsdlUrl, msg, SOAP1_1_CONTENT_TYPE, DEFAULT_ENCODING);
    }

    public static String requestSoap1_2(String wsdlUrl, String msg) throws Exception{
        return request(wsdlUrl, msg, SOAP1_2_CONTENT_TYPE, DEFAULT_ENCODING);
    }

    public static String request(String wsdlUrl, String msg, String contentType, String charset) throws Exception{
        // 创建post方法，使用wsdl地址作为参数
        HttpRequest request = HttpRequest.post(wsdlUrl);
        request.contentType(contentType);
        byte[] b = msg.getBytes(charset);
        if (request.send(b).ok()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.receive(baos);
            return baos.toString();
        }
        return null;
    }

    /**
     * pojo转换成xml 默认编码UTF-8
     *
     * @param obj 待转化的对象
     * @return xml格式字符串
     * @throws Exception JAXBException
     */
    public static String convertToXml(Object obj, Class... clazz) throws Exception {
        return convertToXml(obj, DEFAULT_ENCODING, clazz);
    }

    /**
     * pojo转换成xml
     *
     * @param obj 待转化的对象
     * @param encoding 编码
     * @return xml格式字符串
     * @throws Exception JAXBException
     */
    public static String convertToXml(Object obj, String encoding, Class... clazz) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller marshaller = context.createMarshaller();
        // 指定是否使用换行和缩排对已编组 XML 数据进行格式化的属性名称。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    /**
     * xml转换成JavaBean
     *
     * @param soapXml soap报文
     * @param clazz 待转化的对象
     * @param protocol soap协议
     * @return 转化后的对象
     * @throws Exception JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> JAXBElement convertToJavaBean(String soapXml, Class<T> clazz, String protocol) throws WsException, JAXBException, SOAPException, IOException {

        if (Strings.isNullOrEmpty(soapXml)) {
            throw new WsException("soap.xml.is.empty");
        }
        if (Strings.isNullOrEmpty(protocol)) {
            throw new WsException("soap.protocol.is.empty");
        }

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream in = new ByteArrayInputStream(soapXml.getBytes());
        SOAPMessage soapMessage = MessageFactory.newInstance(protocol).createMessage(null, in);

        JAXBElement<T> element = unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument(), clazz);
        return element;
    }

    public static <T> JAXBElement convertToJavaBean(String xml, Class<T> clazz) throws Exception {
        return convertToJavaBean(xml, clazz, SOAPConstants.SOAP_1_1_PROTOCOL);
    }

}
