package com.zyq.yuquetransfer.dto.response;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 请求响应
 */
@Data
public class ObjResponseDTO {

    private JSONObject data;

    public <T> T getMyData(Class<T> clazz) {
        if (data.isEmpty()) {
            return null;
        }
        return data.toJavaObject(clazz);
    }

}
