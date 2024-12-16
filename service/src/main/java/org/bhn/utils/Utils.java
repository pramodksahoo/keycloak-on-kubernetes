package org.bhn.utils;

import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import ua_parser.Client;
import ua_parser.Parser;

import javax.ws.rs.core.HttpHeaders;

import java.util.List;

@UtilityClass
public class Utils {

    public static String getDisplayLanguage(HttpHeaders headers, String localeHeader) {
        String value = getHeaderValue(headers, localeHeader);
        if (StringUtils.isNotEmpty(value)) {
            return value.split(",")[0];
        }
        return null;
    }

    public static String getHeaderValue(HttpHeaders headers, String headerName) {
        List<String> headerValue = headers.getRequestHeaders().get(headerName);
        if (CollectionUtils.isNotEmpty(headerValue)) {
            return headerValue.get(0);
        }
        return "";
    }

    public static Client getDeviceInformation(String userAgentHeader) {
        Parser uaParser = new Parser();
        return uaParser.parse(userAgentHeader);
    }
}
