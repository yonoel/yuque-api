package com.zyq.yuquetransfer.dto;

import lombok.Data;

/**
 * 仓库中的文件
 */
@Data
public class DocDTO {

    public static final String FORMAT_WORD = "lake";
    public static final String FORMAT_EXCEL = "lakesheet";

    private Long id;

    /**
     * 文档路径
     */
    private String slug;

    /**
     * 文档类型。lake表示word，lakesheet表示excel
     */
    private String format;

    private String title;

}
