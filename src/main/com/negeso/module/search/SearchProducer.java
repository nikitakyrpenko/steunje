package com.negeso.module.search;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.w3c.dom.Element;

import com.negeso.framework.Env;
import com.negeso.framework.ResourceMap;
import com.negeso.framework.generators.Xbuilder;
import com.negeso.framework.list.search.ListSearchProducer;
import com.negeso.module.core.service.PlaceHolderService;

public class SearchProducer {

    private static Logger logger = Logger.getLogger(SearchProducer.class);

    private static List<SearchEntryProducer> searchableList =
            new ArrayList<SearchEntryProducer>();

    private static boolean isBuildingIndex = false;

    static {
        searchableList.add(new PageSearchProducer());
        searchableList.add(new ListSearchProducer());
    }

    /**
     * Performs indexing. Add here indexing your SearchEntryProducer.
     */
    public static synchronized void buildIndex() {
        logger.debug("+ indexing start");
        isBuildingIndex = true;
        IndexWriter writer = null;
        try {
            Map<String, String> map = PlaceHolderService.getPlaceHoldersMap(Env.getSiteId());
            writer = new IndexWriter(makeIndexPath(), new SimplifiedAnalyzer(), true);
            SearchIndexCallBack searchIndexCallBack = new SearchIndexCallBack(writer, map.keySet().toArray(new String[1]), map.values().toArray(new String[1]));
            for (SearchEntryProducer producer : searchableList) {
                producer.indexEntries(searchIndexCallBack);
            }
        } catch (Throwable e) {
            logger.error("- Throwable", e);
        } finally {
            closeWriter(writer);
            isBuildingIndex = false;
            logger.debug("- Indexing done.");
        }
    }

    private static void closeWriter(IndexWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (Throwable e) {
                logger.error("- cannot close writer", e);
            }
        }
    }

    private static File makeIndexPath() {
        File p = new File(ResourceMap.getRealPath("INDEX_DIR"), Env.getSiteId().toString());
        try {
            FileUtils.forceMkdir(p);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return p;
    }

    public static Element search(org.w3c.dom.Document ownerDoc, SearchParameters searchParams) {
        if (!isBuildingIndex) {
            return doSearch(ownerDoc, searchParams);
        }
        Element elResult = Xbuilder.createEl(ownerDoc, "search_result", null);
        Xbuilder.setAttr(elResult, "isBuildingIndex", isBuildingIndex);
        return elResult;
    }

    /**
     * Serves search requests
     */
    private synchronized static Element doSearch(
            org.w3c.dom.Document ownerDoc,
            SearchParameters searchParams) {
        logger.info("+");
        Directory fsDir = null;
        IndexSearcher is = null;
        Element elResult = Xbuilder.createEl(ownerDoc, "search_result", null);

        try {
            fsDir = FSDirectory.getDirectory(makeIndexPath());
            is = new IndexSearcher(fsDir);
            if (isQueryEmpty(searchParams)) {
                logger.debug("- no query");
                return elResult;
            }
            Query query = new QueryBuilder(searchParams).getQuery();

            Hits hits = "date".equals(searchParams.getSortOrder())
                    ? is.search(query, null, new Sort("date", true))
                    : is.search(query);

            logger.info("### hits.length() = " + hits.length());

            buildResults(elResult, query, hits, searchParams.getPaging(), searchParams.getCurPage());

            return elResult;
        } catch (Exception e) {
            logger.error("- Exception", e);
            return elResult;
        } finally {
            if (fsDir != null) {
                try {
                    fsDir.close();
                } catch (Exception e) {
                    logger.error("- Exception", e);
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    logger.error("- Exception", e);
                }
            }
        }
    }


    private static void buildResults(Element elResult, Query query, Hits hits, int paging, int pageNo)
            throws IOException {

        if (hits.length() < 1) {
            logger.info("- no matches");
            return;
        }

        int totalPages = (int) Math.ceil(hits.length() / (double) paging);
        logger.info("total pages: " + totalPages);
        if (pageNo * paging > hits.length()) {
            pageNo = totalPages;
        }
        Element elHits = Xbuilder.addEl(elResult, "hits", null);
        elHits.setAttribute("total-count", "" + hits.length());
        int firstHitNo = ((pageNo - 1) * paging);
        int lastHitNo = (pageNo * paging - 1);
        if (lastHitNo > hits.length() - 1) {
            lastHitNo = hits.length() - 1;
        }
        elHits.setAttribute("current-range", "" + (firstHitNo + 1) + " - "
                + (lastHitNo + 1));

        addHits(firstHitNo, lastHitNo, hits, elHits, query);

        Element elPages = Xbuilder.addEl(elResult, "search_pages", null);
        for (int i = 1; i <= totalPages; i++) {
            Element elPage = Xbuilder.addEl(elPages, "search_page", null);
            elPage.setAttribute("id", "" + i);
            if (pageNo == i) {
                elPage.setAttribute("current", "true");
            }
        }
        logger.info("- number of matches: " + hits.length());
    }

    private static boolean isQueryEmpty(SearchParameters searchParams) {
        logger.debug("+-");
        String all = searchParams.getAllWords();
        String one = searchParams.getAtLeastOne();
        String exact = searchParams.getExactPhrase();
        String without = searchParams.getWithout();
        return
                (all == null || "".equals(all)) &&
                        (one == null || "".equals(one)) &&
                        (exact == null || "".equals(exact)) &&
                        (without == null || "".equals(without) &&
                                searchParams.getLowerTime() == 0 &&
                                searchParams.getUpperTime() == 0);
    }

    private static void addHits(int firstHitNo, int lastHitNo, Hits hits,
                                Element elHits, Query q) throws IOException {
        logger.debug("+");

        for (int i = firstHitNo; i <= lastHitNo; i++) {
            Document doc = hits.doc(i);
            // String text = StringUtil.stripTags(doc.get("contents"));
            // String quote = text.substring(0, Math.min(200, text.length()));
            String quote = "";
            try {
                quote = getBestFragments(q, doc);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }




            Element elHit = Xbuilder.addEl(elHits, "hit", quote);
            elHit.setAttribute("id", "" + (i + 1));
            elHit.setAttribute("filename", doc.get("filename"));
            elHit.setAttribute("title", doc.get("title"));
            elHit.setAttribute("score", "" + hits.score(i));
            elHit.setAttribute("imageUri", "" + doc.get("imageUri"));
            elHit.setAttribute("orderCode", doc.get("orderCode"));

            String productNumber = doc.get("productNumber");

            if (productNumber != null && !productNumber.equals("")) {
                elHit.setAttribute("productNumber", productNumber);
            }

            String multipleOf = doc.get("multipleOf");

            if (multipleOf != null && !multipleOf.equals("null")) {
                elHit.setAttribute("multipleOf", multipleOf);
            }

            String categoryName = doc.get("categoryName");
            if(categoryName != null && !categoryName.equals("")){
                elHit.setAttribute("categoryName", categoryName);
            }
        }

        logger.debug("-");
    }


    private static String getBestFragments(Query luceneQuery, Document luceneDocument)
            throws IOException {

        IndexReader luceneIndexReader = IndexReader.open(makeIndexPath());
        String fragmentSize = Env.getProperty("serch.fragment_size", "100");
        Query rewrittenLuceneQuery = luceneQuery.rewrite(luceneIndexReader);
        QueryScorer luceneScorer = new QueryScorer(rewrittenLuceneQuery, "contents");

        SimpleHTMLFormatter luceneFormatter = new SimpleHTMLFormatter();
        Highlighter luceneHighlighter = new Highlighter(luceneFormatter,
                luceneScorer);

        Fragmenter luceneFragmenter = new SimpleFragmenter(Integer
                .parseInt(fragmentSize));

        luceneHighlighter.setTextFragmenter(luceneFragmenter);

        String[] luceneDocumentValues = luceneDocument.getValues("contents");
        String fragment = null;
        for (int k = 0; k < luceneDocumentValues.length; k++) {
            Analyzer luceneAnalyzer = new SimplifiedAnalyzer();
            TokenStream luceneTokenStream = luceneAnalyzer.tokenStream(
                    "contents", new StringReader(
                            luceneDocumentValues[k]));
            fragment = luceneHighlighter.getBestFragments(
                    luceneTokenStream, luceneDocumentValues[k], 2,
                    "...");

            fragment = fragment.split("keywords:")[0];

            if (fragment == null) {
            } else {
                luceneDocumentValues[k] = fragment;
            }
        }
        luceneIndexReader.close();
        return fragment;
    }

    public static List<SearchEntryProducer> getSearchableList() {
        return searchableList;
    }

    public static boolean isBuildingIndex() {
        return isBuildingIndex;
    }

}
