package com.zyq.yuquetransfer.dto;

import lombok.Data;

/**
 * 仓库（知识库）
 */
@Data
public class RepositoryDTO {

    private Long id;

    /**
     * 仓库路径
     */
    private String slug;

    /**
     * 对象类型，book表示文档仓库，蓝色logo那种，resource表示资源仓库，黄色logo那种
     */
    private String type;

    private String name;

    /**
     * 仓库命名空间，查询文档要用
     */
    private String namespace;

}
