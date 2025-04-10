package podgorskip.swift.utils.parser;

import org.apache.commons.lang3.tuple.Pair;
import podgorskip.swift.model.dto.SwiftCodeRequest;
import java.util.Map;

public interface FileParser {
    Pair<Map<String, SwiftCodeRequest>, Map<String, String>> parse(String filePath);
}
