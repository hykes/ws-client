package com.github.hykes.ws.test.interceptor;

import com.github.hykes.ws.WsClient;

import javax.xml.soap.SOAPMessage;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
public class DefaultResponseInterceptor implements WsClient.ResponseInterceptor {

    /**
     * 处理 web service 响应数据
     *
     * @param soapMessage
     */
    @Override
    public void handleMessage(SOAPMessage soapMessage) {

    }
}
