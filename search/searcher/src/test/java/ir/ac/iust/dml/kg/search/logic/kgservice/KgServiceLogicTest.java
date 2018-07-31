package ir.ac.iust.dml.kg.search.logic.kgservice;

import ir.ac.iust.dml.kg.search.logic.kgservice.data.*;
import org.junit.jupiter.api.Test;

class KgServiceLogicTest {

    final private KgServiceLogic logic = new KgServiceLogic();

    @Test
    void getParent() {
        final ParentNode result = logic.getParent("http://fkg.iust.ac.ir/ontology/Actor");
        assert result != null;
        assert result.getParentUrl().equals("http://fkg.iust.ac.ir/ontology/Artist");
        assert result.getLabel().equals("هنرمند");
    }

    @Test
    void getChildren() {
        final ChildNodes result = logic.getChildren("http://fkg.iust.ac.ir/ontology/Actor");
        assert result != null;
        assert result.getChildNodes() != null;
        assert result.getChildNodes().size() == 2;
        assert result.getChildNodes().get(0).getNodeUrl().equals("http://fkg.iust.ac.ir/ontology/Adultactor");
        assert result.getChildNodes().get(0).getLabel().equals("بازیگر فیلم\u200Cهای بزرگسالان");
        assert result.getChildNodes().get(1).getNodeUrl().equals("http://fkg.iust.ac.ir/ontology/Voiceactor");
        assert result.getChildNodes().get(1).getLabel().equals("صداپیشه");
    }

    @Test
    void getClassInfo() {
        final ClassInfo result = logic.getClassInfo("http://fkg.iust.ac.ir/ontology/Actor");
        assert result != null;
        assert result.getLabel().equals("بازیگر");
        assert result.getProperties() != null;
        assert result.getProperties().size() > 0;
        result.getProperties().forEach(p -> {
            assert p != null;
            assert p.getUrl() != null;
            assert p.getLabel() != null;
            assert p.getMandatory() != null;
        });
    }

    @Test
    void getEntityInfo() {
        final EntityData result = logic.getEntityInfo("http://fkg.iust.ac.ir/resource/ایران");
        assert result != null;
        assert result.getLabel().equals("ایران");
        assert result.getProperties() != null;
        assert result.getProperties().size() > 0;
        result.getProperties().forEach(p -> {
            assert p != null;
            assert p.getPropUrl() != null;
            assert p.getPropLabel() != null;
            assert p.getPropValue() != null;
            assert !p.getPropValue().isEmpty();
            assert p.getPropValue().get(0).getContent() != null || p.getPropValue().get(0).getUrl() != null;
        });
    }

    @Test
    void getEntityClasses() {
        final EntityClasses result = logic.getEntityClasses("http://fkg.iust.ac.ir/resource/تهران");
        assert result != null;
        assert result.getMainClass().equals("http://fkg.iust.ac.ir/ontology/City");
        assert !result.getClassTree().isEmpty();
    }

    @Test
    void getEntitiesOfClass() {
        final Entities entities = logic.getEntitiesOfClass("http://fkg.iust.ac.ir/ontology/Game", 0, 10);
        assert entities != null;
        assert entities.getResult().size() > 0;
        assert entities.getResult().size() <= 10;
        entities.getResult().forEach(entityInfo -> {
            assert entityInfo.getEntityLabel() != null;
            assert entityInfo.getEntityUrl() != null;
        });
    }

}