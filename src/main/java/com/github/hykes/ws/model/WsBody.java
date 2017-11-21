package com.github.hykes.ws.model;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Desc: 报文body
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/13
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class WsBody <T extends WsMsg> {

    private static final String MSG = "msg";

    @XmlElement(required = true, name=MSG)
    protected T msg;

    public void setMsg(T msg) throws Exception {
        this.msg = msg;
        this.build();
    }

    /**
     * 将包含的属性类的注解@XmlRootElement上name属性，替换此处定义的msg，实现自定义节点名称功能
     * @throws Exception
     */
    private void build() throws Exception{
        // 获取消息体注解值
        XmlRootElement annotation = this.getMsg().getClass().getAnnotation(XmlRootElement.class);
        if (annotation == null) {
            return ;
        }

        //获取 WsBody 的 content 字段
        Field field = WsBody.class.getDeclaredField(MSG);
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
        memberValues.put("name", annotation.name());
    }

}