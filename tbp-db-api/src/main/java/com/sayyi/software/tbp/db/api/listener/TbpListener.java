package com.sayyi.software.tbp.db.api.listener;

public interface TbpListener {

    int getInterestEvent();

    void call(TbpEvent tbpEvent);
}
