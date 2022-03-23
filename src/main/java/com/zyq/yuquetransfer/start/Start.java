package com.zyq.yuquetransfer.start;

import com.zyq.yuquetransfer.dto.DocDTO;
import com.zyq.yuquetransfer.dto.GroupDTO;
import com.zyq.yuquetransfer.dto.RepositoryDTO;
import com.zyq.yuquetransfer.dto.TocDTO;
import com.zyq.yuquetransfer.file.FileUtil;
import com.zyq.yuquetransfer.http.YuqueApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class Start {

    @Autowired
    private YuqueApi yuqueApi;

    @GetMapping("/start")
    public void start() throws IOException, InterruptedException {
        List<GroupDTO> groupList = yuqueApi.getGroup();
        log.info("查询到的组织：\n{}", groupList);

        // 保存到桌面
        String rootPath = System.getProperty("user.home") + File.separator + "Desktop";
        // 一个一个来
        for (GroupDTO groupDTO : groupList) {
            // （测试）只导出监测3.0
            if (!"监测3.0".equals(groupDTO.getName())) {
                continue;
            }

            log.info("开始创建组织：{}", groupDTO.getName());
            File groupDir = new File(rootPath + File.separator + groupDTO.getName());
            FileUtil.deleteDir(groupDir);
            groupDir.mkdir();

            // 创建group下的仓库
            List<RepositoryDTO> repoList = yuqueApi.getRepo(groupDTO.getId());
            log.info("查询到的仓库：\n{}", repoList);
            for (RepositoryDTO repositoryDTO : repoList) {
                // （测试）只导出项目服务器连接
//                if (!"项目服务器连接".equals(repositoryDTO.getName())) {
//                    continue;
//                }

                log.info("开始创建仓库：{}", repositoryDTO.getName());
                File repoDir = FileUtil.createChildDir(groupDir, repositoryDTO.getName());

                // 创建仓库下的文档。文档已按照树形结构排序，以下逻辑的前提是文档已排序
                List<TocDTO> tocList = yuqueApi.getToc(repositoryDTO.getNamespace());
                if (CollectionUtils.isEmpty(tocList)) {
                    continue;
                }
                // 因为toc接口没有返回文档类型，doc接口又没有返回文档的树形结构，所以两个接口都要调
                List<DocDTO> docList = yuqueApi.getDoc(repositoryDTO.getNamespace());
                Map<Long/*文档id*/, String/*文件类型*/> docTypeMap = docList.stream().collect(Collectors.toMap(DocDTO::getId, DocDTO::getFormat));

                // 目录的uuid和本地文件目录的映射，创建文档的时候从这里面获取父目录路径
                Map<String/*uuid*/, File/*本地文件*/> tocFileMap = new HashMap<>(64);
                for (TocDTO tocDTO : tocList) {
                    log.info("开始创建文档：{}", tocDTO.getTitle());
                    String docType = docTypeMap.get(tocDTO.getId());

                    // 子节点不为空说明是目录，需要创建目录
                    if (!tocDTO.getChild_uuid().isEmpty()) {
                        // 如果父节点不为空，需要在父节点的目录下创建；父节点为空则说明是一级节点，直接在repo目录下创建
                        File targetDir = !tocDTO.getParent_uuid().isEmpty() ? tocFileMap.get(tocDTO.getParent_uuid()) : repoDir;
                        File tocDir = FileUtil.createChildDir(targetDir, tocDTO.getTitle());
                        tocFileMap.put(tocDTO.getUuid(), tocDir);

                        // 如果是既是目录同时又是文档，还需要在目录下创建同名的文档
                        if (tocDTO.getType().equals(TocDTO.TYPE_DOC)) {
                            File docFile = FileUtil.createChildFile(tocDir, this.getFileName(docType, tocDTO.getTitle()));
                            yuqueApi.exportDoc(docFile, tocDTO.getId(), docType, this.getFileUrlPath(repositoryDTO.getNamespace(), tocDTO.getSlug()));
                        }
                    } else {
                        // 没有子节点说明是纯文档，根据父uuid找到父目录然后创建
                        File parentDir = tocFileMap.get(tocDTO.getParent_uuid());
                        File docFile = FileUtil.createChildFile(parentDir, this.getFileName(docType, tocDTO.getTitle()));
                        yuqueApi.exportDoc(docFile, tocDTO.getId(), docType, this.getFileUrlPath(repositoryDTO.getNamespace(), tocDTO.getSlug()));
                    }
                }
            }
        }
    }

    private String getFileName(String format, String fileName) {
        return fileName + (DocDTO.FORMAT_EXCEL.equals(format) ? ".xlsx" : ".docx");
    }

    private String getFileUrlPath(String namespace, String slug) {
        return "/" + namespace + "/" + slug;
    }

}
