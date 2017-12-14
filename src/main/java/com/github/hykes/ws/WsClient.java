package com.github.hykes.ws;

import com.sun.org.apache.xml.internal.utils.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;

/**
 * Web Service Client
 * @author hykes
 * @date 2017/11/24
 */
public class WsClient {

    /**
     * soap请求消息
     */
    private SOAPMessage requestMessage;

    /**
     * soap响应消息
     */
    private SOAPMessage responseMessage;

    /**
     * 命名空间
     */
    private String namespace;

    /**
     * 请求方法
     */
    private String method;

    /**
     * WSDL地址
     */
    private String wsdl;

    /**
     * 字符编码，默认UTF-8
     */
    private String charset;

    /**
     * soap协议
     */
    private String protocol;

    /**
     * 自定义请求头列表
     */
    private Map<String, String> headers = new HashMap<String, String>();

    /**
     * 请求处理器列表
     */
    private List<RequestInterceptor> requestInterceptors = new ArrayList();

    /**
     * 响应处理器列表
     */
    private List<ResponseInterceptor> responseInterceptors = new ArrayList();

    /**
     * 获取请求消息
     * @return
     */
    public SOAPMessage getRequestMessage() {
        return this.requestMessage;
    }

    /**
     * 获取响应消息
     * @return
     */
    public SOAPMessage getResponseMessage() {
        return this.responseMessage;
    }

    /**
     * 获取命名空间
     * @return
     */
    public String getNamespace() {
        if (this.namespace == null) {
            throw new WsException("ws.ns.is.null");
        }
        return this.namespace;
    }

    /**
     * 获取ws调用方法
     * @return
     */
    public String getMethod() {
        if (this.method == null) {
            throw new WsException("ws.method.is.null");
        }
        return this.method;
    }

    /**
     * 获取wsdl地址
     * @return
     */
    public String getWsdl(){
        if (this.wsdl == null) {
            throw new WsException("wsdl.is.null");
        }
        return this.wsdl;
    }

    /**
     * 获取请求报文
     * @return
     */
    public String getRequestXml() throws Exception {
        if (this.requestMessage == null) {
            throw new WsException("request.soap.message.is.null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.requestMessage.writeTo(baos);
        return baos.toString();
    }

    /**
     * 获取响应报文
     * @return
     */
    public String getResponseXml() throws Exception {
        if (this.responseMessage == null) {
            throw new WsException("response.soap.message.is.null");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        this.responseMessage.writeTo(baos);
        return baos.toString();
    }

    /**
     * 获取字符编码，默认utf-8
     * @return
     */
    public String getCharset(){
        if (this.charset == null) {
            return "UTF-8";
        }
        return this.charset;
    }

    /**
     * 获取soap协议，默认soap1.1
     * @return
     */
    public String getProtocol(){
        if (this.protocol == null) {
            return SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        return this.protocol;
    }

    /**
     * 获取自定义请求头
     * @return
     */
    public Map<String, String> getHeaders(){
        return this.headers;
    }

    /**
     * 链式设置参数
     * @param obj
     * @return
     * @throws Exception
     */
    public WsClient object(Object obj, Class clazz) throws Exception {

        SOAPMessage soapMessage = MessageFactory.newInstance(this.getProtocol()).createMessage();
        SOAPEnvelope soapEnvelope = soapMessage.getSOAPPart().getEnvelope();
        soapEnvelope.addNamespaceDeclaration("soap", soapEnvelope.getNamespaceURI("env"));
        soapEnvelope.removeNamespaceDeclaration("env");
        soapEnvelope.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        soapEnvelope.addNamespaceDeclaration("xsd", "http://www.w3.org/2001/XMLSchema");
        soapEnvelope.setPrefix("soap");

        SOAPHeader soapHeader = soapMessage.getSOAPHeader();
        soapHeader.setPrefix("soap");

        SOAPBody soapBody = soapMessage.getSOAPBody();
        soapBody.setPrefix("soap");

        Document paramDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, this.getCharset());// //编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息

        XMLFilterImpl nsfFilter = new XMLFilterImpl() {
            private String rootNamespace = null;
            private Stack<String> nsStack = new Stack<String>();
            private Map<String, String> nsMap = new HashMap<String, String>();

            @Override
            public void startDocument() throws SAXException {
                super.startDocument();
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
                if ("".equals(uri)) {
                    uri = this.rootNamespace;
                }
                if (!"".equals(uri)) {
                    this.rootNamespace = uri;
                }
                nsStack.push(qName);
                nsMap.put(qName, uri);
                super.startElement(uri, localName, localName, atts);
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                nsStack.pop();
                if (!nsStack.empty()) {
                    String root = nsStack.peek();
                    this.rootNamespace = nsMap.get(root);
                }
                super.endElement(uri, localName, localName);
            }

            @Override
            public void startPrefixMapping(String prefix, String url) throws SAXException {
                super.startPrefixMapping("", url);
            }
        };

        DOMBuilder domBuilder =  new DOMBuilder(paramDoc);
        nsfFilter.setContentHandler(domBuilder);
        marshaller.marshal(obj, nsfFilter);

        soapBody.addDocument(paramDoc);

        soapMessage.saveChanges();
        this.requestMessage = soapMessage;

        return this;
    }

    /**
     * 链式设置命名空间
     * @param namespace
     * @return
     */
    public WsClient namespace(String namespace){
        this.namespace = namespace;
        return this;
    }

    /**
     * 链式设置请求方法
     * @param method
     * @return
     */
    public WsClient method(String method){
        this.method = method;
        return this;
    }

    /**
     * 链式设置wsdl地址
     * @param wsdl
     * @return
     */
    public WsClient wsdl(String wsdl){
        this.wsdl = wsdl;
        return this;
    }

    /**
     * 链式设置字符编码
     * @param charset
     * @return
     */
    public WsClient charset(String charset){
        this.charset = charset;
        return this;
    }

    /**
     * 链式设置soap协议
     * @param protocol
     * @return
     */
    public WsClient protocol(String protocol){
        this.protocol = protocol;
        return this;
    }

    /**
     * 链式设置headers
     * @param headers
     * @return
     */
    public WsClient headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * 链式设置header
     * @param name
     * @param value
     * @return
     */
    public WsClient addHeader(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * 添加前置处理句柄
     * @param clientRequestHandler
     */
    public void addRequestHandler(RequestInterceptor clientRequestHandler){
        requestInterceptors.add(clientRequestHandler);
    }

    /**
     * 添加后置处理句柄
     * @param clientResponseHandler
     */
    public void addResponseHandler(ResponseInterceptor clientResponseHandler){
        responseInterceptors.add(clientResponseHandler);
    }

    /**
     * 发起ws请求
     * @return
     * @throws Exception
     */
    public WsClient send() throws Exception {

        for (Map.Entry<String, String> entry: this.getHeaders().entrySet()) {
            this.requestMessage.getMimeHeaders().addHeader(entry.getKey(), entry.getValue());
        }

        for(RequestInterceptor handler: requestInterceptors){
            handler.handleMessage(this.requestMessage);
        }
        if (this.requestMessage.saveRequired()) {
            this.requestMessage.saveChanges();
        }

        SOAPConnectionFactory soapConnFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = soapConnFactory.createConnection();

        URL url = new URL(this.getWsdl());
        this.responseMessage = connection.call(this.requestMessage, url);

        for(ResponseInterceptor handler: responseInterceptors){
            handler.handleMessage(this.requestMessage);
        }
        if (this.responseMessage.saveRequired()) {
            this.responseMessage.saveChanges();
        }
        return this;
    }

    /**
     * pojo转换成xml
     *
     * @param obj 待转化的对象
     * @return xml格式字符串
     * @throws Exception JAXBException
     */
    private String convertToXml(Object obj, Class... clazz) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Marshaller marshaller = context.createMarshaller();
        // 指定是否使用换行和缩排对已编组 XML 数据进行格式化的属性名称。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, this.getCharset());
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    /**
     *  xml转换成JavaBean
     * @param clazz 待转化的对象
     * @return 转化后的对象
     * @throws Exception
     */
    public <T> T convert(Class<T> clazz) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        if (this.responseMessage == null) {
            return null;
        }
        /**
         * @see javax.xml.soap.SOAPBody#extractContentAsDocument()
         */
        Document document = this.responseMessage.getSOAPBody().extractContentAsDocument();
        JAXBElement<T> element = unmarshaller.unmarshal(document, clazz);
        this.responseMessage.getSOAPBody().addDocument(document);
        if (this.responseMessage.saveRequired()) {
            this.responseMessage.saveChanges();
        }
        return element.getValue();
    }

    public static class WsException extends RuntimeException {

        private static final long serialVersionUID = -2061666676241079034L;

        /**
         * Constructs a {@code WsException} with no detail message.
         */
        private WsException() { super(); }

        /**
         * Constructs a {@code WsException} with the specified
         * detail message.
         *
         * @param   s   the detail message.
         */
        public WsException(String s) {
            super(s);
        }
    }

    /**
     * 请求处理器接口
     */
    public interface RequestInterceptor {

        /**
         * 处理 web service 请求数据
         * @param soapMessage
         */
        void handleMessage(SOAPMessage soapMessage);

    }

    /**
     * 响应处理器接口
     */
    public interface ResponseInterceptor {

        /**
         * 处理 web service 响应数据
         * @param soapMessage
         */
        void handleMessage(SOAPMessage soapMessage);
    }

}
