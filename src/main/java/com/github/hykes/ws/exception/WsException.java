package com.github.hykes.ws.exception;

/**
 * Desc:
 * Mail: hehaiyangwork@qq.com
 * Date: 2017/11/17
 */
public class WsException extends RuntimeException {

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
