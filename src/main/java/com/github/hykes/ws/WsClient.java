package com.github.hykes.ws;

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
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web Service Client
 * @author hykes
 * @date 2017/11/24
 */
public class WsClient {

    /**
     * 封装soap实体
     */
    private WsEntity wsEntity;

    /**
     * 请求参数实体
     */
    private Object object;

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
     * 当前原始报文
     */
    private String originXml;

    /**
     * 响应结果xml
     */
    private String resultXml;

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
     * 获取封装的soap实体
     * @return
     */
    public WsEntity getWsEntity() {
        if (this.wsEntity == null) {
            this.wsEntity = new WsEntity();
        }
        return this.wsEntity;
    }

    /**
     * 获取实际参数对象
     * @return
     */
    public Object getObject() {
        return this.object;
    }

    /**
     * 获取命名空间
     * @return
     */
    public String getNamespace() {
//        if (this.namespace == null) {
//            throw new WsException("ws.ns.is.null");
//        }
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
    public String getOriginXml(){
        if (this.originXml == null) {
            return "";
        }
        return this.originXml;
    }

    /**
     * 获取响应报文
     * @return
     */
    public String getResultXml() {
        if (this.resultXml == null) {
            return "";
        }
        return this.resultXml;
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
        WsEntity entity = this.getWsEntity();
        entity.setSoap(this.getProtocolValue());
        entity.setNamespace(this.getNamespace());
        WsBody body = new WsBody();
        body.setMethod(getMethod());
        body.setObj(obj);
        entity.setBody(body);

        this.wsEntity = entity;
        this.object = obj;
        this.originXml = convertToXml(this.getWsEntity(), this.getWsEntity().getClass(), clazz);
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
    public WsClient header(String name, String value) {
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
    public WsClient send() throws Exception{
        this.request();
        return this;
    }

    /**
     * soap请求方法
     * @return
     * @throws Exception
     */
    private void request() throws Exception{

        ByteArrayOutputStream baos;

        InputStream in = new ByteArrayInputStream(this.getOriginXml().getBytes());
        SOAPMessage reqMessage = MessageFactory.newInstance(this.getProtocol()).createMessage(null, in);
        in.close();

        for(RequestInterceptor handler: requestInterceptors){
            handler.handleMessage(reqMessage);
        }

        baos = new ByteArrayOutputStream();
        reqMessage.writeTo(baos);
        baos.close();
        this.originXml = baos.toString();

        String contentType;
        if (SOAPConstants.SOAP_1_1_PROTOCOL.equals(this.getProtocol())) {
            contentType = SOAPConstants.SOAP_1_1_CONTENT_TYPE + ";charset=" + this.getCharset();
        } else if (SOAPConstants.SOAP_1_2_PROTOCOL.equals(this.getProtocol())) {
            contentType = SOAPConstants.SOAP_1_2_CONTENT_TYPE + ";charset=" + this.getCharset();
        } else {
            throw new WsException("soap.protocol.error");
        }

        String result = sendPost(this.getWsdl(), this.getOriginXml(), contentType, this.getCharset());

        if (!responseInterceptors.isEmpty()) {
            SOAPMessage resMessage = MessageFactory.newInstance(this.getProtocol()).createMessage(null, new ByteArrayInputStream(result.getBytes()));

            for(ResponseInterceptor handler: responseInterceptors){
                handler.handleMessage(resMessage);
            }
            baos = new ByteArrayOutputStream();
            resMessage.writeTo(baos);
            baos.close();
            result = baos.toString();
        }
        this.resultXml = result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url
     *            发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    private String sendPost(String url, String data, String contentType, String charset) {
        DataOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("Content-Type", contentType);

            if (!this.getHeaders().isEmpty()) {
                for(Map.Entry<String, String> entry: this.getHeaders().entrySet()){
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new DataOutputStream(conn.getOutputStream());
            // 发送请求参数
            out.write(data.getBytes(charset));
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            //使用finally块来关闭输出流、输入流
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            } catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
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
     * 结果解析
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public <T> T convert(Class<T> clazz) throws Exception {
        JAXBElement<T> object = convertToJavaBean(clazz);
        if (object != null) {
            return object.getValue();
        } else {
            return null;
        }
    }

    /**
     * xml转换成JavaBean
     *
     * @param clazz 待转化的对象
     * @return 转化后的对象
     * @throws Exception JAXBException
     */
    @SuppressWarnings("unchecked")
    private <T> JAXBElement convertToJavaBean(Class<T> clazz) throws WsException, JAXBException, SOAPException, IOException {

        JAXBContext context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        InputStream in = new ByteArrayInputStream(this.getResultXml().getBytes());
        if (this.getResultXml() == null || this.getResultXml() == "") {
            return null;
        }
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
