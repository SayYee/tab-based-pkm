package com.sayyi.software.tbp.common;

/**
 * @author SayYi
 */
public class TbpException extends RuntimeException {

    public TbpException(String msg) {
        super(msg);
    }

    public TbpException(Throwable cause) {
        super(cause);
    }

    public TbpException(String msg, Exception e) {
        super(msg, e);
    }
}
