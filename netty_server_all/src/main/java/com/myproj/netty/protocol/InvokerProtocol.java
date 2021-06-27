package com.myproj.netty.protocol;

import lombok.Data;

import java.io.Serializable;

/**
 * 自定义传输协议
 *
 * @author shenxie
 * @date 2021/1/24
 */
@Data
public class InvokerProtocol  implements Serializable {

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] params;

    /**
     * 参数值
     */
    private Object[] value;

}
