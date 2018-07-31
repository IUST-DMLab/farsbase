package ir.ac.iust.dml.kg.log;

/**
 * Created by ali on 28/02/17.
 */
class QueryRecordTest {
    @org.junit.jupiter.api.Test
    void parseLine() {
        QueryRecord record = LogRecordParser.ParseLine("korre olagh,2,7/17/2016");
        assert record.toString().equals("\"korre olagh\"\tx 2\t 2016-07-17");
    }

}