package com.sayyi.software.tbp.db.listener;

public interface TbpListener {

    int getInterestEvent();

    void call(TbpEvent tbpEvent);
}
