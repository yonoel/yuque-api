package com.zyq.yuquetransfer.dto;

import lombok.Data;

/**
 * 仓库的目录，包含文档基本信息以及树形结构
 */
@Data
public class TocDTO {

    public static final String TYPE_TITLE = "TITLE";
    public static final String TYPE_DOC = "DOC";

    private Long id;

    /**
     * 目录类型，title表示纯标题，doc表示文档或者标题文档（如"政务网区VPN和堡垒机指导手册"，既是标题本身又是文档）
     */
    private String type;

    /**
     * 标题/文档名称
     */
    private String title;

    /**
     * 用于构建文档树
     */
    private String uuid;

    /**
     * 树形结构中，上一个节点的uuid
     */
    private String prev_uuid;

    /**
     * 树形结构中，下一个节点的uuid
     */
    private String sibling_uuid;

    /**
     * 树形结构中，父节点的id
     */
    private String parent_uuid;

    /**
     * 树形结构中，第一个子节点的uuid
     */
    private String child_uuid;

    /**
     * 文档路径
     */
    private String slug;

}
