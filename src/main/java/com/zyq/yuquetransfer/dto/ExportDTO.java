package com.zyq.yuquetransfer.dto;

import lombok.Data;

@Data
public class ExportDTO {

    public static final String STATE_PENDING = "pending";
    public static final String STATE_SUCCESS = "success";

    /**
     * 文档转换状态。有pending和success两种
     */
    private String state;

    /**
     * 状态为success时返回url
     */
    private String url;

}
