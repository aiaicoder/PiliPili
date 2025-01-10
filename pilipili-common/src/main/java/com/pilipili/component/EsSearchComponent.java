package com.pilipili.component;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pilipili.Model.Vo.VideoInfoEsVO;
import com.pilipili.Model.Vo.VideoInfoVo;
import com.pilipili.Model.entity.UserInfo;
import com.pilipili.Model.entity.VideoInfo;
import com.pilipili.common.ErrorCode;
import com.pilipili.config.AppConfig;
import com.pilipili.enums.SearchOrderTypeEnum;
import com.pilipili.exception.BusinessException;
import com.pilipili.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="https://github.com/aiaicoder">  小新
 * @version 1.0
 * @date 2025/1/7 13:01
 */
@Component
@Slf4j
public class EsSearchComponent {
    @Resource
    private AppConfig appConfig;

    @Resource
    private RestHighLevelClient client;

    @Resource
    private UserInfoService userInfoService;


    private Boolean isExists() throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(appConfig.getEsIndexVideoName());
        return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
    }


    public void createIndex() {
        try {
            if (isExists()) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(appConfig.getEsIndexVideoName());
            request.source(MAPPING_TEMPLATE, XContentType.JSON);
            CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            if (!createIndexResponse.isAcknowledged()) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建索引失败");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建索引失败" + e);
        }
    }

    public void saveDoc(VideoInfo videoInfo) {
        VideoInfoEsVO videoInfoEsVO = BeanUtil.copyProperties(videoInfo, VideoInfoEsVO.class);
        try {
            if (docExist(videoInfo.getVideoId())) {
                updateDoc(videoInfoEsVO);
            } else {
                videoInfoEsVO.setPlayCount(0);
                videoInfoEsVO.setDanMuCount(0);
                videoInfoEsVO.setCollectCount(0);
                IndexRequest indexRequest = new IndexRequest(appConfig.getEsIndexVideoName());
                indexRequest.id(videoInfo.getVideoId()).source(JSONUtil.toJsonStr(videoInfoEsVO), XContentType.JSON);
                client.index(indexRequest, RequestOptions.DEFAULT);
            }

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "创建索引失败" + e);
        }
    }


    public void updateDocCount(String videoId, String filedName, Integer count) {
        try {
            UpdateRequest updateRequest = new UpdateRequest(appConfig.getEsIndexVideoName(), videoId);
            Script script = new Script(ScriptType.INLINE, "painless", "crx._source" + filedName + "+=params.count",
                    Collections.singletonMap("count", count));
            updateRequest.script(script);
        } catch (Exception e) {
            log.error("更新文档失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新文档失败" + e);
        }
    }

    public void deleteDoc(String videoId) {
        DeleteRequest deleteRequest = new DeleteRequest(appConfig.getEsIndexVideoName(), videoId);
        try {
            client.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("删除文档失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除文档失败" + e);
        }
    }

    /**
     * 搜索，根据标签和名字进行查询
     *
     * @param highlight
     * @param keyword
     * @param orderType
     * @param pageNum
     * @param pageSize
     * @return
     */
    public Page<VideoInfoVo> searchDoc(Boolean highlight, String keyword, Integer orderType, Integer pageNum, Integer pageSize) {
        try {
            SearchOrderTypeEnum orderTypeEnum = SearchOrderTypeEnum.getByType(orderType);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.multiMatchQuery(keyword, "videoName", "tags"));
            //高亮
            if (highlight) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field("videoName");
                highlightBuilder.preTags("<span color='red'>");
                highlightBuilder.postTags("</span>");
                searchSourceBuilder.highlighter(highlightBuilder);
            }
            //排序
            searchSourceBuilder.sort("_score", SortOrder.ASC);
            if (orderTypeEnum != null) {
                searchSourceBuilder.sort(orderTypeEnum.getField(), SortOrder.DESC);
            }
            searchSourceBuilder.from((pageNum - 1) * pageSize).size(pageSize);
            SearchRequest searchRequest = new SearchRequest(appConfig.getEsIndexVideoName());
            searchRequest.source(searchSourceBuilder);
            List<VideoInfoVo> videoInfoVOList = new ArrayList<>();
            List<String> userIdList = new ArrayList<>();

            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            int total = (int) hits.getTotalHits().value;
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {
                VideoInfoVo videoInfo = JSONUtil.toBean(hit.getSourceAsString(), VideoInfoVo.class);
                if (hit.getHighlightFields().get("videoName") != null) {
                    videoInfo.setVideoName(hit.getHighlightFields().get("videoName").fragments()[0].toString());
                }
                videoInfoVOList.add(videoInfo);
                userIdList.add(videoInfo.getUserId());
            }
            List<UserInfo> userInfos = userInfoService.listByIds(userIdList);
            Map<String, UserInfo> userInfoMap = userInfos.stream().collect(Collectors.toMap(UserInfo::getUserId, Function.identity(), (data1, data2) -> data2));
            videoInfoVOList.forEach(videoInfoVo -> {
                UserInfo userInfo = userInfoMap.get(videoInfoVo.getUserId());
                videoInfoVo.setNickName(userInfo == null ? "" : userInfo.getNickName());
            });
            Page<VideoInfoVo> videoInfoPage = new Page<>(pageNum, pageSize, hits.getTotalHits().value);
            videoInfoPage.setCurrent(pageNum);
            videoInfoPage.setSize(pageSize);
            videoInfoPage.setTotal(total);
            videoInfoPage.setRecords(videoInfoVOList);
            return videoInfoPage;
        } catch (Exception e) {
            log.error("搜索文档失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索文档失败" + e);
        }
    }


    private void updateDoc(VideoInfoEsVO videoInfoEsVO) throws IOException {
        UpdateRequest indexRequest = new UpdateRequest(appConfig.getEsIndexVideoName(), videoInfoEsVO.getVideoId());
        indexRequest.id(videoInfoEsVO.getVideoId()).doc(BeanUtil.beanToMap(videoInfoEsVO));
        client.update(indexRequest, RequestOptions.DEFAULT);
    }


    public boolean docExist(String videoId) throws IOException {
        GetRequest getRequest = new GetRequest(appConfig.getEsIndexVideoName(), videoId);
        return client.get(getRequest, RequestOptions.DEFAULT).isExists();
    }


    static final String MAPPING_TEMPLATE = "{\n" +
            " \"settings\": {  \n" +
            "    \"analysis\": {\n" +
            "      \"analyzer\": {\n" +
            "        \"comma\": {\n" +
            "          \"type\": \"pattern\",\n" +
            "          \"pattern\": \",\"\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  },\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"videoId\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"userId\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"videoCover\": {\n" +
            "         \"type\": \"text\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"videoName\": {\n" +
            "         \"type\": \"text\",\n" +
            "       \"analyzer\": \"ik_max_word\"\n" +
            "      },\n" +
            "      \"tags\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"comma\"\n" +
            "      },\n" +
            "      \"playCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"danMuCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"collectCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"commentCount\": {\n" +
            "        \"type\": \"integer\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"createTime\": {\n" +
            "        \"type\": \"date\",\n" +
            "        \"format\": \"yyyy-MM-dd HH:mm:ss\",\n" +
            "        \"index\": false\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";


}
