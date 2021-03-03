package com.sayyi.software.tbp.cli.decorator;

/**
 * @author SayYi
 */
public class ResultHolder {

    private Object result;

    private boolean error;
    private Exception exception;

    public boolean isError() {
        return error;
    }

    public void setException(Exception exception) {
        error = true;
        this.exception = exception;
    }

    public Exception getException() {
        error = false;
        Exception temp = exception;
        exception = null;
        return temp;
    }

    public void setResult(Object result) throws Exception {
        if (isError()) {
            throw getException();
        }
        this.result = result;
    }

    public Object getResult() throws Exception {
        if (isError()) {
            throw getException();
        }
        Object res = result;
        this.result = null;
        return res;
    }
}
