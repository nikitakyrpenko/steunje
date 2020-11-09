package com.negeso.module.search;

import java.io.IOException;

import com.negeso.framework.controller.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import com.negeso.framework.util.StringUtil;

public class SearchIndexCallBack {

    private IndexWriter writer;
    private String[] searchList = null;
    private String[] replacementList = null;

    public SearchIndexCallBack(IndexWriter writer, String[] searchList, String[] replacementList) {
        super();
        this.writer = writer;
        this.searchList = searchList;
        this.replacementList = replacementList;
    }

    public void index(SearchEntry entry) throws IOException {
        Document doc = new Document();
        doc.add(new Field("filename", Utils.fixNull(entry.getUri()), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("language", Utils.fixNull(entry.getLangCode()), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("title", StringUtil.stripTagsEx2(Utils.fixNull(entry.getTitle())), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("contents", StringUtil.stripTagsEx2(Utils.fixNull(replacePlaceHolders(entry.getContents()))), Field.Store.YES,
                Field.Index.TOKENIZED));
        String date = Utils.getDate(entry.getDate());
        doc.add(new Field("date", date, Field.Store.YES, Field.Index.UN_TOKENIZED));
        Long cid = entry.getContainerId();
        String strCid = cid == null ? "0" : StringUtils.EMPTY + cid;
        doc.add(new Field("cid", strCid, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("imageUri", Utils.fixNull(entry.getImagePreviewUri()), Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("multipleOf", String.valueOf(entry.getMultipleOf()), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("productNumber", Utils.fixNull(entry.getProductNumber()), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("orderCode", Utils.fixNull(entry.getOrderCode()), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("categoryName", Utils.fixNull(entry.getCategoryName()), Field.Store.YES, Field.Index.UN_TOKENIZED));
        writer.addDocument(doc);
    }

    private String replacePlaceHolders(String str) {
        if (StringUtils.isNotBlank(str)) {
            str = StringUtils.replaceEach(str, searchList, replacementList);
        }
        return str;
    }
}
