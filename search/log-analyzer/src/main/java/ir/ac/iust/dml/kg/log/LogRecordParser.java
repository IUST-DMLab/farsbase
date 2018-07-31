package ir.ac.iust.dml.kg.log;

import knowledgegraph.normalizer.PersianCharNormalizer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ali on 4/11/17.
 */
public class LogRecordParser {
    static final Logger LOGGER = LoggerFactory.getLogger(LogRecordParser.class);
    static final PersianCharNormalizer normalizer = new PersianCharNormalizer();

    /**
     * This is used to parse queryText log with one queryText at all  (Format: "Query, TotalQueryCount")
     * @param line
     * @return
     */
    public static QueryRecord ParseLine(String line) {
        QueryRecord queryRecord = null;
        try {
            if (StringUtils.countMatches(line, ",") < 1) {
                LOGGER.warn("Line: \"" + line + "\" cannot be parsed (insufficient elements in line).");
                return null;
            }
            String[] reverseSplits =  (new StringBuilder(line)).reverse().toString().split(",",2);

            long freq = Long.parseLong((new StringBuilder(reverseSplits[0])).reverse().toString());
            String queryText = (new StringBuilder(reverseSplits[1])).reverse().toString();

            queryText = normalizer.normalize(queryText);

            queryRecord = new QueryRecord(freq,queryText);
        } catch (Exception e) {
            LOGGER.warn("Line: \"" + line + "\" cannot be parsed", e);
        }

        return queryRecord;
    }

    /**
     * This is used to parse queryText log with one queryText per day (Format: "Query, QueryCount, Date")
     * @param line
     * @return
     */
    /*public static QueryRecord ParseLinesWithDate(String line) {
        QueryRecord logRecord = new QueryRecord();
        //System.out.println("line: " + line);
        try {
            if (StringUtils.countMatches(line, ",") < 2) {
                System.err.println("Line: \"" + line + "\" cannot be parsed (insufficient elements in line).");
                return null;
            }
            String[] splits = line.split(",");
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            logRecord.date =sdf.parse(splits[splits.length - 1]);
            logRecord.freq = Long.parseLong(splits[splits.length - 2]);
            logRecord.queryText = String.join("",(String[]) Arrays.copyOfRange(splits, 0, splits.length - 2));

        } catch (Exception e) {
            System.err.println("Line: \"" + line + "\" cannot be parsed");
            e.printStackTrace();
            return null;
        }
        return logRecord;
    }*/
}
