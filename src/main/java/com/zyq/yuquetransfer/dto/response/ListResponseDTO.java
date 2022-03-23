package com.zyq.yuquetransfer.dto.response;

import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求响应
 */
@Data
public class ListResponseDTO {

    private JSONArray data;

    public <T> List<T> getMyData(Class<T> clazz) {
        if (CollectionUtils.isEmpty(data)) {
            return new ArrayList<>();
        }
        return data.toJavaList(clazz);
    }

}
