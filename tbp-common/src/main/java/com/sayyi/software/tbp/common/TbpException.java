package com.sayyi.software.tbp.common;

/**
 * @author SayYi
 */
public class TbpException extends Exception {

    public TbpException(String msg) {
        super(msg);
    }

    public TbpException(String msg, Exception e) {
        super(msg, e);
    }
}
