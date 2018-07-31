package ir.ac.iust.dml.kg.search.logic;

import ir.ac.iust.dml.kg.search.logic.data.DataValue;
import ir.ac.iust.dml.kg.search.logic.data.DataValues;
import ir.ac.iust.dml.kg.search.logic.data.ResultEntity;
import ir.ac.iust.dml.kg.search.logic.data.SearchResult;

public class FakeLogic {

    static private ResultEntity getEntity1() {
        ResultEntity entity = new ResultEntity();
        entity.setTitle("محسن رضایی");
        entity.setDescription("سیاست‌مدار");
        entity.setSubtitle("متولد مسجد سلیمان");
        entity.setLink("https://fa.wikipedia.org/wiki/%D9%85%D8%AD%D8%B3%D9%86_%D8%B1%D8%B6%D8%A7%DB%8C%DB%8C");
        entity.getPhotoUrls().add("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6c/" +
                "Sardar_Mohsen_Rezaee_by_Tasnimnews_%28cropped%29.jpg/" +
                "220px-Sardar_Mohsen_Rezaee_by_Tasnimnews_%28cropped%29.jpg");
        entity.getKeyValues().put("نام کامل", new DataValues(new DataValue("محسن رضایی میرقائد", null)));
        entity.getKeyValues().put("زادروز", new DataValues(new DataValue("۱۰ شهریور ۱۳۳۳",
                "https://fa.wikipedia.org/wiki/%DB%B1%DB%B0_%D8%B4%D9%87%D8%B1%DB%8C%D9%88%D8%B1")));
        entity.getKeyValues().put("زادگاه", new DataValues(
                new DataValue("مسجد سلیمان",
                        "https://fa.wikipedia.org/wiki/%D9%85%D8%B3%D8%AC%D8%AF%D8%B3%D9%84%DB%8C%D9%85%D8%A7%D9%86"),
                new DataValue("ایران", "https://fa.wikipedia.org/wiki/%D8%A7%DB%8C%D8%B1%D8%A7%D9%86")));
        entity.getKeyValues().put("همسر(ان)", new DataValues(new DataValue("معصومه خدنگ", null)));
        return entity;
    }

    static private ResultEntity getEntity2() {
        ResultEntity entity = new ResultEntity();
        entity.setTitle("احمد رضایی میرقائد");
        entity.setDescription("سیاست‌مدار");
        entity.setSubtitle("متوفی ۱۳۹۰ در دبی");
        entity.setLink("https://fa.wikipedia.org/wiki/" +
                "%D8%A7%D8%AD%D9%85%D8%AF_%D8%B1%D8%B6%D8%A7%DB%8C%DB%8C_%D9%85%DB%8C%D8%B1%D9%82%D8%A7%D8%A6%D8%AF");
        entity.getKeyValues().put("درگذشت", new DataValues(new DataValue("۲۲ آبان ۱۳۹۰",
                "https://fa.wikipedia.org/wiki/%DB%B1%DB%B3%DB%B9%DB%B0")));
        entity.getKeyValues().put("علت مرگ", new DataValues(
                new DataValue("ترور", "https://fa.wikipedia.org/wiki/%D8%AA%D8%B1%D9%88%D8%B1"),
                new DataValue("قتل", "https://fa.wikipedia.org/wiki/%D9%82%D8%AA%D9%84")));
        return entity;
    }

    static public SearchResult oneEntity() {
        final SearchResult result = new SearchResult();
        result.getEntities().add(getEntity1());
        return result;
    }

    static public SearchResult oneEntityAndBreadcrumb() {
        final SearchResult result = new SearchResult();
        result.getBreadcrumb().add("محسن رضایی");
        result.getBreadcrumb().add("فرزند");
        result.getEntities().add(getEntity2());
        return result;
    }

    static public SearchResult list() {
        final SearchResult result = new SearchResult();
        result.getBreadcrumb().add("ایران");
        result.getBreadcrumb().add("بازیگر");
        result.getEntities().add(getEntity1());
        result.getEntities().add(getEntity2());
        return result;
    }
}
