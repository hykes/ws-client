package com.github.hykes.ws.test.interceptor;

import com.github.hykes.ws.WsClient;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import java.util.Date;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/24
 */
public class DefaultRequestInterceptor implements WsClient.RequestInterceptor {

    /**
     * 处理soap的数据
     *
     * @param soapMessage soap数据
     */
    @Override
    public void handleMessage(SOAPMessage soapMessage) {
        try {
            QName timestamp = new QName(soapMessage.getSOAPBody().getNamespaceURI("soap"), "timestamp", "");
            soapMessage.getSOAPHeader().addChildElement(timestamp).setValue(new Date().toString());

            QName sign = new QName(soapMessage.getSOAPBody().getNamespaceURI("soap"), "sign", "");
            soapMessage.getSOAPHeader().addChildElement(sign).setValue("xxxx");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
