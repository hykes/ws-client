package com.github.hykes.ws;

import com.github.kevinsawicki.http.HttpRequest;

import javax.xml.bind.*;
import javax.xml.bind.annotation.*;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Desc: Web Service Client
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
public class WsClient {

    /**
     * 前置处理句柄列表
     */
    private List<WsClientRequestHandler> requestHandlers = new ArrayList();

    /**
     * 后置处理句柄列表
     */
    private List<WsClientResponseHandler> responseHandlers = new ArrayList();

    /**
     * 添加前置处理句柄
     * @param clientRequestHandler
     */
    public void addRequestHandler(WsClientRequestHandler clientRequestHandler){
        requestHandlers.add(clientRequestHandler);
    }

    /**
     * 添加后置处理句柄
     * @param clientResponseHandler
     */
    public void addResponseHandler(WsClientResponseHandler clientResponseHandler){
        responseHandlers.add(clientResponseHandler);
    }

    /**
     * 响应结果xml
     */
    private String resultXml;

    public String getResultXml() {
        if (Objects.isNull(this.resultXml)) {
            return "";
        }
        return this.resultXml;
    }

    public WsClient send() throws Exception{
        this.resultXml = request();
        return this;
    }

    /**
     * soap请求方法
     * @return
     * @throws Exception
     */
    public String request() throws Exception{

        InputStream in = new ByteArrayInputStream(this.getOriginXml().getBytes());
        SOAPMessage reqMessage = MessageFactory.newInstance(this.getProtocol()).createMessage(null, in);

        for(WsClientRequestHandler handler: requestHandlers){
            handler.request(reqMessage);
        }

        ByteArrayOutputStream soap = new ByteArrayOutputStream();
        reqMessage.writeTo(soap);

        HttpRequest request = HttpRequest.post(this.getWsdl());

        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(this.getProtocol())) {
            request.contentType(SOAPConstants.SOAP_1_1_CONTENT_TYPE + ";charset=" + this.getCharset());
        } else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(this.getProtocol())) {
            request.contentType(SOAPConstants.SOAP_1_2_CONTENT_TYPE + ";charset=" + this.getCharset());
        } else {
            throw new WsException("soap.protocol.error");
        }
        if (!this.getHeaders().isEmpty()) {
            request.headers(this.getHeaders());
        }

        this.originXml = soap.toString();
        byte[] b = this.getOriginXml().getBytes(this.getCharset());
        if (request.send(b).ok()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            request.receive(baos);
            SOAPMessage resMessage = MessageFactory.newInstance(this.getProtocol()).createMessage(null, new ByteArrayInputStream(baos.toByteArray()));
            baos.close();

            for(WsClientResponseHandler handler: responseHandlers){
                handler.response(resMessage);
            }

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            resMessage.writeTo(result);
            result.close();

            return result.toString();
        } else {
            throw new WsException(request.message());
        }
    }

    /**
     * 命名空间
     */
    private String namespace;

    public String getNamespace() {
        if (Objects.isNull(this.namespace)) {
            throw new WsException("ws.ns.is.null");
        }
        return this.namespace;
    }

    /**
     * 请求方法
     */
    private String method;

    public String getMethod() {
        if (Objects.isNull(this.method)) {
            throw new WsException("ws.method.is.null");
        }
        return this.method;
    }

    /**
     * 封装soap实体
     */
    private WsEntity wsEntity = new WsEntity();

    public WsEntity getWsEntity() {
        return wsEntity;
    }

    /**
     * 请求参数实体
     */
    private Object object;

    public Object getObject() {
        return object;
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
     * WSDL地址
     */
    private String wsdl;

    public String getWsdl(){
        if (Objects.isNull(this.wsdl)) {
            throw new WsException("wsdl.is.null");
        }
        return this.wsdl;
    }

    public WsClient wsdl(String wsdl){
        this.wsdl = wsdl;
        return this;
    }

    /**
     * 链式设置参数
     * @param obj
     * @return
     * @throws Exception
     */
    public WsClient object(Object obj, Class clazz) throws Exception {
        WsEntity entity = this.getWsEntity();
        entity.setSoap(this.getProtocolValue());
        entity.setNamespace(this.getNamespace());
        WsBody body = new WsBody();
        body.setMethod(getMethod());
        body.setObj(obj);
        entity.setBody(body);

        this.originXml = convertToXml(entity, this.getCharset(), entity.getClass(), clazz);
        this.object = obj;
        this.wsEntity = entity;
        return this;
    }

    /**
     * 当前原始报文
     */
    private String originXml;

    public String getOriginXml(){
        if (Objects.isNull(this.originXml)) {
            return "";
        }
        return this.originXml;
    }

    /**
     * pojo转换成xml
     *
     * @param obj 待转化的对象
     * @param encoding 编码
     * @return xml格式字符串
     * @throws Exception JAXBException
     */
    public String convertToXml(Object obj, String encoding,Class clazz, Class clazz2) throws Exception {
        JAXBContext context = JAXBContext.newInstance(clazz, clazz2);
        Marshaller marshaller = context.createMarshaller();
        // 指定是否使用换行和缩排对已编组 XML 数据进行格式化的属性名称。
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    /**
     * 字符编码，默认UTF-8
     */
    private String charset;

    public String getCharset(){
        if (Objects.isNull(this.charset)) {
            return "UTF-8";
        }
        return this.charset;
    }

    public WsClient charset(String charset){
        this.charset = charset;
        return this;
    }

    private String protocol;

    public String getProtocol(){
        if (this.protocol == null) {
            return SOAPConstants.SOAP_1_1_PROTOCOL;
        }
        return this.protocol;
    }

    /**
     * 获取soap协议值
     * @return
     */
    public String getProtocolValue(){
        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(this.getProtocol())) {
            return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
        } else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(this.getProtocol())) {
            return SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE;
        } else {
            throw new WsException("soap.protocol.error");
        }
    }

    /**
     * 获取协议类型
     * @param protocol
     * @return
     */
    public WsClient protocol(String protocol){
        this.protocol = protocol;
        return this;
    }

    private Map<String, String> headers = new HashMap<String, String>();

    public Map<String, String> getHeaders(){
        return this.headers;
    }

    public WsClient headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public WsClient header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    /**
     * 结果解析
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T convert(Class<T> clazz) throws Exception {
        return (T) convertToJavaBean(clazz).getValue();
    }

    /**
     * xml转换成JavaBean
     *
     * @param clazz 待转化的对象
     * @return 转化后的对象
     * @throws Exception JAXBException
     */
    @SuppressWarnings("unchecked")
    public <T> JAXBElement convertToJavaBean(Class<T> clazz) throws WsException, JAXBException, SOAPException, IOException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream in = new ByteArrayInputStream(this.getResultXml().getBytes());
        SOAPMessage soapMessage = MessageFactory.newInstance(this.getProtocol()).createMessage(null, in);

        JAXBElement<T> element = unmarshaller.unmarshal(soapMessage.getSOAPBody().extractContentAsDocument(), clazz);
        return element;
    }


    public static class WsException extends RuntimeException {

        private static final long serialVersionUID = -2061666676241079034L;

        /**
         * Constructs a {@code WsException} with no detail message.
         */
        public WsException() {
            super();
        }

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

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(propOrder = {"header", "body"})
    @XmlRootElement(name = "soap:Envelope")
    public static class WsEntity {

        /**
         * soap协议
         */
        @XmlAttribute(name = "xmlns:soap")
        protected String soap;

        /**
         * 命名空间属性
         */
        @XmlAttribute(name = "xmlns")
        protected String namespace;

        @XmlElement(required = true, name="soap:Header")
        protected WsHeader header = new WsHeader();

        @XmlElement(required = true, name="soap:Body")
        protected WsBody body = new WsBody();

        public String getSoap() {
            return soap;
        }

        public void setSoap(String soap) {
            this.soap = soap;
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public WsHeader getHeader() {
            return header;
        }

        public void setHeader(WsHeader header) {
            this.header = header;
        }

        public WsBody getBody() {
            return body;
        }

        public void setBody(WsBody body) {
            this.body = body;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WsHeader {

    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class WsBody {

        @XmlTransient
        protected String method;

        @XmlElement
        protected Object object;

        public void setObj(Object obj) throws Exception {
            this.object = obj;
            this.build();
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }

        /**
         * 将包含的属性类的注解@XmlRootElement上name属性，实现自定义节点名称功能
         * @throws Exception
         */
        private void build() throws Exception{
            // 获取消息体注解值

            if (this.method == null) {
                return ;
            }

            //获取 WsBody 的 content 字段
            Field field = WsBody.class.getDeclaredField("object");
            //获取 method 字段上的 XmlElement 注解实例
            XmlElement foo = field.getAnnotation(XmlElement.class);
            //获取 XmlElement 这个代理实例所持有的 InvocationHandler
            InvocationHandler h = Proxy.getInvocationHandler(foo);
            // 获取 AnnotationInvocationHandler 的 memberValues 字段
            Field hField = h.getClass().getDeclaredField("memberValues");
            // 因为这个字段事 private final 修饰，所以要打开权限
            hField.setAccessible(true);
            // 获取 memberValues
            Map memberValues = (Map) hField.get(h);
            // 修改 value 属性值
            memberValues.put("name", this.method);
        }
    }

    /**
     * 前置处理器
     */
    public interface WsClientRequestHandler {

        /**
         * 处理 web service 请求数据
         * @param soapMessage
         */
        void request(SOAPMessage soapMessage);

    }

    /**
     * 后置处理器
     */
    public interface WsClientResponseHandler {

        /**
         * 处理 web service 响应数据
         * @param soapMessage
         */
        void response(SOAPMessage soapMessage);
    }

}
