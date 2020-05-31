package pl.cwikla.bazy.projekt;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataParser {
    public Result parseData(InputStream inputStream) {
        BufferedReader data = new BufferedReader(new InputStreamReader(inputStream));
        var iter = data.lines().iterator();
        if (iter.hasNext()) {
            String[] splitHeaders = nextRecord(iter);
            Map<String, Integer> columnToIndex = IntStream.range(0, splitHeaders.length).boxed().collect(Collectors.toUnmodifiableMap(
                    i -> splitHeaders[i], Function.identity()
            ));
            return new Result(columnToIndex, iter);
        }

        return new Result(Collections.emptyMap(), Collections.emptyIterator());
    }

    private static String[] nextRecord(Iterator<String> iter) {
        String headers = iter.next();
        return headers.split(",");
    }

    public static class Result {
        private final Map<String, Integer> columnToIndex;
        private final Iterator<String> lines;
        private String[] record;

        private Result(Map<String, Integer> columnToIndex, Iterator<String> lines) {
            this.columnToIndex = columnToIndex;
            this.lines = lines;
        }

        public boolean hasNextRecord() {
            return lines.hasNext();
        }

        public void moveToNextRecord() {
            record = nextRecord(lines);
        }

        public String getValue(String columnName) {
            return record[columnToIndex.get(columnName)];
        }

        public Set<String> columns() {
            return columnToIndex.keySet();
        }
    }
}
