package com.cowave.commons.client.http.request;

import com.cowave.commons.client.http.asserts.Asserts;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.cowave.commons.client.http.request.HttpRequestTemplate.valuesOrEmpty;

/**
 *
 * @author shanhuiming
 *
 */
public class HttpRequest {
    private final Map<String, Collection<String>> queries = new LinkedHashMap<>();
    private final Map<String, Collection<String>> headers = new LinkedHashMap<>();
    private StringBuilder url = new StringBuilder();
    private String method;
    private byte[] body;
    private Map<String, Object> multiForm;
    private InputStream multiFile;
    private String multiFileName;
    private String bodyTemplate;
    private transient Charset charset;
    private boolean decodeSlash = true;
    // 调用时指定的url
    private String hostUrl;

    public HttpRequest() {

    }

    public HttpRequest(HttpRequest copy) {
        this.queries.putAll(copy.queries);
        this.headers.putAll(copy.headers);
        this.url.append(copy.url);
        this.method = copy.method;
        this.body = copy.body;
        this.bodyTemplate = copy.bodyTemplate;
        this.charset = copy.charset;
        this.decodeSlash = copy.decodeSlash;
        this.hostUrl = copy.hostUrl;
    }

    public HttpRequestTemplate requestTemplate(int retryTimes, int retryInterval, int connectTimeout, int readTimeout) {
        Map<String, Collection<String>> safeCopy = new LinkedHashMap<>(headers);
        return new HttpRequestTemplate(method, url + queryLine(), body, charset,
                Collections.unmodifiableMap(safeCopy),
                connectTimeout, readTimeout, retryTimes, retryInterval,
                multiFile, multiFileName, multiForm);
    }

     void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

    public String getHostUrl() {
        return hostUrl;
    }

    void setMultiFile(InputStream multiFile) {
        this.multiFile = multiFile;
    }

    void setMultiFileName(String multiFileName) {
        this.multiFileName = multiFileName;
    }

    void setMultiForm(Map<String, Object> multiForm) {
        this.multiForm = multiForm;
    }

    private static String urlDecode(String arg) throws UnsupportedEncodingException {
        return URLDecoder.decode(arg, "UTF-8");
    }

    private static String urlEncode(Object arg) throws UnsupportedEncodingException {
        return URLEncoder.encode(String.valueOf(arg), "UTF-8");
    }

    private static boolean isHttpUrl(CharSequence value) {
        return value.length() >= 4 && value.subSequence(0, 3).equals("http".substring(0, 3));
    }

    private static CharSequence removeTrailingSlash(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() > 0 && charSequence.charAt(charSequence.length() - 1) == '/') {
            return charSequence.subSequence(0, charSequence.length() - 1);
        } else {
            return charSequence;
        }
    }

    /**
     * Expands a {@code template}, such as {@code username}, using the {@code variables} supplied. Any
     * unresolved parameters will remain. <br> Note that if you'd like curly braces literally in the
     * {@code template}, urlencode them first.
     *
     * @param template  URI template that can be in level 1 <a href="http://tools.ietf.org/html/rfc6570">RFC6570</a>
     *                  form.
     * @param variables to the URI template
     * @return expanded template, leaving any unresolved parameters literal
     */
    public static String expand(String template, Map<String, ?> variables) {
        // skip expansion if there's no valid variables set. ex. {a} is the
        // first valid
        Asserts.notNull(template, "template can't be null");
        Asserts.notNull(variables, "variables for " + template + " can't be null");
        if (template.length() < 3) {
            return template;
        }

        boolean inVar = false;
        StringBuilder var = new StringBuilder();
        StringBuilder builder = new StringBuilder();
        for (char c : template.toCharArray()) {
            switch (c) {
                case '{':
                    if (inVar) {
                        // '{{' is an escape: write the brace and don't interpret as a variable
                        builder.append("{");
                        inVar = false;
                        break;
                    }
                    inVar = true;
                    break;
                case '}':
                    if (!inVar) { // then write the brace literally
                        builder.append('}');
                        break;
                    }
                    inVar = false;
                    String key = var.toString();
                    Object value = variables.get(var.toString());
                    if (value != null) {
                        builder.append(value);
                    } else {
                        builder.append('{').append(key).append('}');
                    }
                    var = new StringBuilder();
                    break;
                default:
                    if (inVar) {
                        var.append(c);
                    } else {
                        builder.append(c);
                    }
            }
        }
        return builder.toString();
    }

    private static Map<String, Collection<String>> parseAndDecodeQueries(String queryLine) throws UnsupportedEncodingException {
        Map<String, Collection<String>> map = new LinkedHashMap<>();
        if (!StringUtils.hasText(queryLine)) {
            return map;
        }
        if (queryLine.indexOf('&') == -1) {
            putKV(queryLine, map);
        } else {
            char[] chars = queryLine.toCharArray();
            int start = 0;
            int i = 0;
            for (; i < chars.length; i++) {
                if (chars[i] == '&') {
                    putKV(queryLine.substring(start, i), map);
                    start = i + 1;
                }
            }
            putKV(queryLine.substring(start, i), map);
        }
        return map;
    }

    private static void putKV(String stringToParse, Map<String, Collection<String>> map) throws UnsupportedEncodingException {
        String key;
        String value;
        // note that '=' can be a valid part of the value
        int firstEq = stringToParse.indexOf('=');
        if (firstEq == -1) {
            key = urlDecode(stringToParse);
            value = null;
        } else {
            key = urlDecode(stringToParse.substring(0, firstEq));
            value = urlDecode(stringToParse.substring(firstEq + 1));
        }
        Collection<String> values = map.containsKey(key) ? map.get(key) : new ArrayList<>();
        values.add(value);
        map.put(key, values);
    }

    public HttpRequest resolve(Map<String, ?> paramMap) throws UnsupportedEncodingException {
        replaceQueryValues(paramMap);
        Map<String, String> encoded = new LinkedHashMap<>();
        for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
            encoded.put(entry.getKey(), urlEncode(String.valueOf(entry.getValue())));
        }
        String resolvedUrl = expand(url.toString(), encoded).replace("+", "%20");
        if (decodeSlash) {
            resolvedUrl = resolvedUrl.replace("%2F", "/");
        }
        url = new StringBuilder(resolvedUrl);

        Map<String, Collection<String>> resolvedHeaders = new LinkedHashMap<>();
        for (String field : headers.keySet()) {
            Collection<String> resolvedValues = new ArrayList<>();
            for (String value : valuesOrEmpty(headers, field)) {
                String resolved = expand(value, paramMap);
                resolvedValues.add(resolved);
            }
            resolvedHeaders.put(field, resolvedValues);
        }
        headers.clear();
        headers.putAll(resolvedHeaders);
        if (bodyTemplate != null) {
            body(urlDecode(expand(bodyTemplate, encoded)));
        }
        return this;
    }

    public HttpRequest method(String method) {
        Asserts.notNull(method, "method can't be null");
        Asserts.isTrue(method.matches("^[A-Z]+$"), "Invalid HTTP Method: " + method);
        this.method = method;
        return this;
    }

    /* @see Request#method() */
    public String method() {
        return method;
    }

    public HttpRequest decodeSlash(boolean decodeSlash) {
        this.decodeSlash = decodeSlash;
        return this;
    }

    public boolean decodeSlash() {
        return decodeSlash;
    }

    /* @see #url() */
    public HttpRequest append(CharSequence value) throws UnsupportedEncodingException {
        url.append(value);
        url = pullAnyQueriesOutOfUrl(url);
        return this;
    }

    /* @see #url() */
    public HttpRequest insert(int pos, CharSequence value) throws UnsupportedEncodingException {
        if (isHttpUrl(value)) {
            value = removeTrailingSlash(value);
            if (url.length() > 0 && url.charAt(0) != '/') {
                url.insert(0, '/');
            }
        }
        url.insert(pos, pullAnyQueriesOutOfUrl(new StringBuilder(value)));
        return this;
    }

    public String url() {
        return url.toString();
    }

    /**
     * Replaces queries with the specified {@code name} with the {@code values} supplied.
     * <br> Values can be passed in decoded or in url-encoded form depending on the value of the
     * {@code encoded} parameter.
     * <br> When the {@code value} is {@code null}, all queries with the {@code configKey} are
     * removed. <br> <br><br><b>relationship to JAXRS 2.0</b><br> <br> Like {@code WebTarget.query},
     * except the values can be templatized. <br> ex. <br>
     * <pre>
     * template.query(&quot;Signature&quot;, &quot;{signature}&quot;);
     * </pre>
     * <br> <b>Note:</b> behavior of RequestTemplate is not consistent if a query parameter with
     * unsafe characters is passed as both encoded and unencoded, although no validation is performed.
     * <br> ex. <br>
     * <pre>
     * template.query(true, &quot;param[]&quot;, &quot;value&quot;);
     * template.query(false, &quot;param[]&quot;, &quot;value&quot;);
     * </pre>
     *
     * @param encoded whether name and values are already url-encoded
     * @param name    the name of the query
     * @param values  can be a single null to imply removing all values. Else no values are expected
     *                to be null.
     * @see #queries()
     */
    public HttpRequest query(boolean encoded, String name, String... values) throws UnsupportedEncodingException {
        return doQuery(encoded, name, values);
    }

    /* @see #query(boolean, String, String...) */
    public HttpRequest query(boolean encoded, String name, Iterable<String> values) throws UnsupportedEncodingException {
        return doQuery(encoded, name, values);
    }

    /**
     * Shortcut for {@code query(false, String, String...)}
     *
     * @see #query(boolean, String, String...)
     */
    public HttpRequest query(String name, String... values) throws UnsupportedEncodingException {
        return doQuery(false, name, values);
    }

    /**
     * Shortcut for {@code query(false, String, Iterable<String>)}
     *
     * @see #query(boolean, String, String...)
     */
    public HttpRequest query(String name, Iterable<String> values) throws UnsupportedEncodingException {
        return doQuery(false, name, values);
    }

    private HttpRequest doQuery(boolean encoded, String name, String... values) throws UnsupportedEncodingException {
        Asserts.notNull(name, "name can't be null");
        String paramName = encoded ? name : encodeIfNotVariable(name);
        queries.remove(paramName);
        if (values != null && values.length > 0 && values[0] != null) {
            ArrayList<String> paramValues = new ArrayList<String>();
            for (String value : values) {
                paramValues.add(encoded ? value : encodeIfNotVariable(value));
            }
            this.queries.put(paramName, paramValues);
        }
        return this;
    }

    private HttpRequest doQuery(boolean encoded, String name, Iterable<String> values) throws UnsupportedEncodingException {
        if (values != null) {
            return doQuery(encoded, name, toArray(values, String.class));
        }
        return doQuery(encoded, name, (String[]) null);
    }

    private static String encodeIfNotVariable(String in) throws UnsupportedEncodingException {
        if (in == null || in.indexOf('{') == 0) {
            return in;
        }
        return urlEncode(in);
    }

    /**
     * Replaces all existing queries with the newly supplied url decoded queries. <br>
     * <br><br><b>relationship to JAXRS 2.0</b><br> <br> Like {@code WebTarget.queries}, except the
     * values can be templatized. <br> ex. <br>
     * <pre>
     * template.queries(ImmutableMultimap.of(&quot;Signature&quot;, &quot;{signature}&quot;));
     * </pre>
     *
     * @param queries if null, remove all queries. else value to replace all queries with.
     * @see #queries()
     */
    public HttpRequest queries(Map<String, Collection<String>> queries) throws UnsupportedEncodingException {
        if (queries == null || queries.isEmpty()) {
            this.queries.clear();
        } else {
            for (Map.Entry<String, Collection<String>> entry : queries.entrySet()) {
                query(entry.getKey(), toArray(entry.getValue(), String.class));
            }
        }
        return this;
    }

    /**
     * Returns an immutable copy of the url decoded queries.
     */
    public Map<String, Collection<String>> queries() throws UnsupportedEncodingException {
        Map<String, Collection<String>> decoded = new LinkedHashMap<String, Collection<String>>();
        for (String field : queries.keySet()) {
            Collection<String> decodedValues = new ArrayList<String>();
            for (String value : valuesOrEmpty(queries, field)) {
                if (value != null) {
                    decodedValues.add(urlDecode(value));
                } else {
                    decodedValues.add(null);
                }
            }
            decoded.put(urlDecode(field), decodedValues);
        }
        return Collections.unmodifiableMap(decoded);
    }

    /**
     * Replaces headers with the specified {@code configKey} with the {@code values} supplied. <br>
     * When the {@code value} is {@code null}, all headers with the {@code configKey} are removed.
     * <br> <br><br><b>relationship to JAXRS 2.0</b><br> <br> Like {@code WebTarget.queries} and
     * {@code javax.ws.rs.client.Invocation.Builder.header}, except the values can be templatized.
     * <br> ex. <br>
     * <pre>
     * template.query(&quot;X-Application-Version&quot;, &quot;{version}&quot;);
     * </pre>
     *
     * @param name   the name of the header
     * @param values can be a single null to imply removing all values. Else no values are expected to
     *               be null.
     * @see #headers()
     */
    public HttpRequest header(String name, String... values) {
        Asserts.notNull(name, "header name can't be null");
        if (values == null || (values.length == 1 && values[0] == null)) {
            headers.remove(name);
        } else {
            List<String> headers = new ArrayList<String>();
            headers.addAll(Arrays.asList(values));
            this.headers.put(name, headers);
        }
        return this;
    }

    /* @see #header(String, String...) */
    public HttpRequest header(String name, Iterable<String> values) {
        if (values != null) {
            return header(name, toArray(values, String.class));
        }
        return header(name, (String[]) null);
    }

    /**
     * Replaces all existing headers with the newly supplied headers. <br> <br><br><b>relationship to
     * JAXRS 2.0</b><br> <br> Like {@code Invocation.Builder.headers(MultivaluedMap)}, except the
     * values can be templatized. <br> ex. <br>
     * <pre>
     * template.headers(mapOf(&quot;X-Application-Version&quot;, asList(&quot;{version}&quot;)));
     * </pre>
     *
     * @param headers if null, remove all headers. else value to replace all headers with.
     * @see #headers()
     */
    public HttpRequest headers(Map<String, Collection<String>> headers) {
        if (headers == null || headers.isEmpty()) {
            this.headers.clear();
        } else {
            this.headers.putAll(headers);
        }
        return this;
    }

    /**
     * Returns an immutable copy of the current headers.
     */
    public Map<String, Collection<String>> headers() {
        return Collections.unmodifiableMap(headers);
    }

    public HttpRequest body(String bodyText) {
        byte[] bodyData = bodyText != null ? bodyText.getBytes(StandardCharsets.UTF_8) : null;
        return body(bodyData, StandardCharsets.UTF_8);
    }

    public HttpRequest body(byte[] bodyData, Charset charset) {
        this.bodyTemplate = null;
        this.charset = charset;
        this.body = bodyData;
        int bodyLength = bodyData != null ? bodyData.length : 0;
        header("Content-Length", String.valueOf(bodyLength));
        return this;
    }

    public Charset charset() {
        return charset;
    }

    public byte[] body() {
        return body;
    }


    public HttpRequest bodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
        this.charset = null;
        this.body = null;
        return this;
    }


    public String bodyTemplate() {
        return bodyTemplate;
    }

    /**
     * if there are any query params in the URL, this will extract them out.
     */
    private StringBuilder pullAnyQueriesOutOfUrl(StringBuilder url) throws UnsupportedEncodingException {
        // parse out queries
        int queryIndex = url.indexOf("?");
        if (queryIndex != -1) {
            String queryLine = url.substring(queryIndex + 1);
            Map<String, Collection<String>> firstQueries = parseAndDecodeQueries(queryLine);
            if (!queries.isEmpty()) {
                firstQueries.putAll(queries);
                queries.clear();
            }
            //Since we decode all queries, we want to use the
            //query()-method to re-add them to ensure that all
            //logic (such as url-encoding) are executed, giving
            //a valid queryLine()
            for (String key : firstQueries.keySet()) {
                Collection<String> values = firstQueries.get(key);
                if (allValuesAreNull(values)) {
                    //Queries where all values are null will
                    //be ignored by the query(key, value)-method
                    //So we manually avoid this case here, to ensure that
                    //we still fulfill the contract (ex. parameters without values)
                    queries.put(urlEncode(key), values);
                } else {
                    query(key, values);
                }

            }
            return new StringBuilder(url.substring(0, queryIndex));
        }
        return url;
    }

    private boolean allValuesAreNull(Collection<String> values) {
        if (values == null || values.isEmpty()) {
            return true;
        }
        for (String val : values) {
            if (val != null) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // 请求行
        builder.append(method).append(' ').append(url).append(" HTTP/1.1\n");
        // Header
        for (String field : headers.keySet()) {
            for (String value : valuesOrEmpty(headers, field)) {
                builder.append(field).append(": ").append(value).append('\n');
            }
        }
        // Body
        if (body != null) {
            builder.append('\n').append(charset != null ? new String(body, charset) : "Binary data");
        }
        return builder.toString();
    }

    /**
     * Replaces query values which are templated with corresponding values from the {@code unencoded}
     * map. Any unresolved queries are removed.
     */
    public void replaceQueryValues(Map<String, ?> unencoded) throws UnsupportedEncodingException {
        Iterator<Map.Entry<String, Collection<String>>> iterator = queries.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Collection<String>> entry = iterator.next();
            if (entry.getValue() == null) {
                continue;
            }
            Collection<String> values = new ArrayList<String>();
            for (String value : entry.getValue()) {
                if (value.indexOf('{') == 0 && value.indexOf('}') == value.length() - 1) {
                    Object variableValue = unencoded.get(value.substring(1, value.length() - 1));
                    // only add non-null expressions
                    if (variableValue == null) {
                        continue;
                    }
                    if (variableValue instanceof Iterable) {
                        for (Object val : Iterable.class.cast(variableValue)) {
                            values.add(urlEncode(String.valueOf(val)));
                        }
                    } else {
                        values.add(urlEncode(String.valueOf(variableValue)));
                    }
                } else {
                    values.add(value);
                }
            }
            if (values.isEmpty()) {
                iterator.remove();
            } else {
                entry.setValue(values);
            }
        }
    }

    public String queryLine() {
        if (queries.isEmpty()) {
            return "";
        }
        StringBuilder queryBuilder = new StringBuilder();
        for (String field : queries.keySet()) {
            for (String value : valuesOrEmpty(queries, field)) {
                queryBuilder.append('&');
                queryBuilder.append(field);
                if (value != null) {
                    queryBuilder.append('=');
                    if (!value.isEmpty()) {
                        queryBuilder.append(value);
                    }
                }
            }
        }
        queryBuilder.deleteCharAt(0);
        return queryBuilder.insert(0, '?').toString();
    }

    private static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        Collection<T> collection;
        if (iterable instanceof Collection) {
            collection = (Collection<T>) iterable;
        } else {
            collection = new ArrayList<T>();
            for (T element : iterable) {
                collection.add(element);
            }
        }
        T[] array = (T[]) Array.newInstance(type, collection.size());
        return collection.toArray(array);
    }
}
