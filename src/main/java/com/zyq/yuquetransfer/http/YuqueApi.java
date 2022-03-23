package com.zyq.yuquetransfer.http;

import com.zyq.yuquetransfer.dto.*;
import com.zyq.yuquetransfer.dto.response.ListResponseDTO;
import com.zyq.yuquetransfer.dto.response.ObjResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语雀api
 */
@Service
public class YuqueApi {

    /**
     * 域名
     */
    private static final String DOMAIN = "https://cydata.yuque.com";
    /**
     * 获取用户能看到的组织
     */
    private static final String GET_GROUP_BY_USER = "https://cydata.yuque.com/api/v2/users/%s/groups";
    /**
     * 获取分组（如"监测3.0"）下的所有仓库
     */
    private static final String GET_REPO_BY_GROUP = "https://cydata.yuque.com/api/v2/groups/%s/repos";
    /**
     * 获取仓库中的文档目录
     */
    private static final String GET_TOC_BY_REPO = "https://cydata.yuque.com/api/v2/repos/%s/toc";
    /**
     * 获取仓库中的文档列表
     */
    private static final String GET_DOC_BY_REPO = "https://cydata.yuque.com/api/v2/repos/%s/docs";
    /**
     * 导出文档
     */
    private static final String EXPORT_DOC = "https://cydata.yuque.com/api/docs/%s/export";

    @Value("${yuque.user_id}")
    private String userId;

    @Autowired
    private RestTemplate restTemplate;

    public List<GroupDTO> getGroup() {
        String url = String.format(GET_GROUP_BY_USER, userId);
        ListResponseDTO response = restTemplate.getForObject(url, ListResponseDTO.class);
        return response.getMyData(GroupDTO.class);
    }

    public List<RepositoryDTO> getRepo(Long groupId) {
        String url = String.format(GET_REPO_BY_GROUP, groupId);
        ListResponseDTO response = restTemplate.getForObject(url, ListResponseDTO.class);
        return response.getMyData(RepositoryDTO.class);
    }

    public List<TocDTO> getToc(String namespace) {
        String url = String.format(GET_TOC_BY_REPO, namespace);
        ListResponseDTO response = restTemplate.getForObject(url, ListResponseDTO.class);
        return response.getMyData(TocDTO.class);
    }

    public List<DocDTO> getDoc(String namespace) {
        String url = String.format(GET_DOC_BY_REPO, namespace);
        ListResponseDTO response = restTemplate.getForObject(url, ListResponseDTO.class);
        return response.getMyData(DocDTO.class);
    }

    public void exportDoc(File docFile, Long docId, String type, String path) throws InterruptedException, IOException {
        String url = String.format(EXPORT_DOC, docId);

        Map<String, String> param = new HashMap<>();
        param.put("force", "0");
        param.put("type", DocDTO.FORMAT_EXCEL.equals(type) ? "excel" : "word");

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        // 这个请求头不加好像会报错？
        headers.set("referer", DOMAIN + path);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(param, headers);
//        System.out.println(url);

        ObjResponseDTO response = restTemplate.postForObject(url, request, ObjResponseDTO.class);
        ExportDTO dto = response.getMyData(ExportDTO.class);
        // 这个接口比较慢，需要好几秒才能转换成功
        while (ExportDTO.STATE_PENDING.equals(dto.getState())) {
            Thread.sleep(3000L);
            response = restTemplate.postForObject(url, request, ObjResponseDTO.class);
            dto = response.getMyData(ExportDTO.class);
        }

        String downloadUrl = DOMAIN + dto.getUrl();
        System.out.println("下载文件:" + downloadUrl);
        ResponseEntity<byte[]> fileResponse = restTemplate.getForEntity(downloadUrl, byte[].class);
        OutputStream out = new FileOutputStream(docFile);
        out.write(fileResponse.getBody());
        out.flush();
        out.close();
    }

}
