package com.sayyi.software.tbp.common.snap;

/**
 *
 * snapshot不同版本的转换组件
 * @author xuchuang
 * @date 2021/5/20
 */
public interface Convertor {

    /**
     * 将传入的version转换为新的version对象。支持的转换版本可以通过下边的support方法获取
     * @param source    源版本
     * @return  目标版本
     */
    Version convert(Version source);

    /**
     * 支持转换的版本。
     * @return  两个元素的数组。第一个元素为源版本，第二个为目标版本。
     */
    int[] support();

}
