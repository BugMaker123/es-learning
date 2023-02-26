package cn.com.ice.eslearning.service.impl;

import cn.com.ice.eslearning.entity.Product;
import cn.com.ice.eslearning.service.ProductService;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private RestHighLevelClient elasticsearchClient;

    /**
     * 创建索引
     * @param product
     * @return
     * @throws Exception
     */
    @Override
    public String indexProduct(Product product) throws Exception {
        IndexRequest request = new IndexRequest("product");
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("name", product.getName());
            builder.field("description", product.getDescription());
            builder.field("price", product.getPrice());
            builder.field("create_time", product.getCreateTime());
        }
        builder.endObject();
        request.source(builder);

        IndexResponse response = elasticsearchClient.index(request,RequestOptions.DEFAULT);
        if (response.status() == RestStatus.CREATED) {
            return response.getId();
        } else {
            throw new RuntimeException("Failed to index document");
        }
    }

    /**
     * 删除数据
     * @param productId
     * @throws Exception
     */
    @Override
    public void deleteProduct(String productId) throws Exception {
        DeleteRequest request = new DeleteRequest("product", productId);
        DeleteResponse response = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
        if (response.status() != RestStatus.OK) {
            throw new RuntimeException("Failed to delete document");
        }
    }

    /**
     * 更新产品
     * @param product
     * @throws Exception
     */
    @Override
    public void updateProduct(Product product) throws Exception {
        UpdateRequest request = new UpdateRequest("product", product.getId());
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("name", product.getName());
            builder.field("description", product.getDescription());
            builder.field("price", product.getPrice());
            builder.field("create_time", product.getCreateTime());
        }
        builder.endObject();
        request.doc(builder);

        UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
        if (response.status() != RestStatus.OK) {
            throw new RuntimeException("Failed to update document");
        }
    }

    /**
     * 根据产品id获取信息
     * @param productId
     * @return
     * @throws Exception
     */
    @Override
    public Product getProductById(String productId) throws Exception {
        GetRequest request = new GetRequest("product", productId);
        GetResponse response = elasticsearchClient.get(request,RequestOptions.DEFAULT);
        if (response.isExists()) {
            Product product = new Product();
            product.setId(response.getId());
            product.setName((String) response.getSource().get("name"));
            product.setDescription((String) response.getSource().get("description"));
            product.setPrice((Double) response.getSource().get("price"));
            product.setCreateTime((Date) response.getSource().get("create_time"));
            return product;
        } else {
            return null;
        }
    }

    /**
     * 批量查询商品
     * @param keyword
     * @return
     * @throws Exception
     */
    @Override
    public List<Product> searchProducts(String keyword) throws Exception {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
        SearchRequest searchRequest = new SearchRequest("product");
        searchRequest.source(sourceBuilder);

        SearchResponse response = elasticsearchClient.search(searchRequest,RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();

        List<Product> products = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            Product product = new Product();
            product.setId(hit.getId());
            product.setName((String) hit.getSourceAsMap().get("name"));
            product.setDescription((String) hit.getSourceAsMap().get("description"));
            product.setPrice((Double) hit.getSourceAsMap().get("price"));
            product.setCreateTime((Date) hit.getSourceAsMap().get("create_time"));
            products.add(product);
        }
        return products;
    }
}