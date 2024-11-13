package com.cowave.commons.client.http.request;

import com.cowave.commons.client.http.asserts.Asserts;
import com.cowave.commons.client.http.request.meta.HttpMethodMeta;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shanhuiming
 *
 */
public class MultipartRequestFactory extends HttpRequestFactory {

    public MultipartRequestFactory(HttpMethodMeta metadata) {
        super(metadata);
    }

    @Override
    protected HttpRequest resolve(Object[] argv, HttpRequest httpRequest,
                                  Map<String, Object> variables, Map<String, Object> multiParams) throws Exception {
        Map<String, Object> multiParamMap = new HashMap<>();
        if (metadata.getMultipartFormIndex() != null) {
            Object form = argv[metadata.getMultipartFormIndex()];
            if (form instanceof Map) {
                Map<String, Object> multiForm = (Map<String, Object>) form;
                multiParamMap.putAll(multiForm);
            } else {
                throw new RemoteException("HttpMultiForm parameter must be a Map");
            }
        }
        multiParamMap.putAll(multiParams);

        InputStream multiFile = null;
        String multiFileName = null;
        if (metadata.getMultipartFileIndex() != null) {
            multiFileName = metadata.getMultipartFileName();

            Object multipartFile = argv[metadata.getMultipartFileIndex()];
            Asserts.notNull(multipartFile, "HttpMultiFile parameter was null");
            if (multipartFile instanceof InputStream) {
                multiFile = (InputStream) multipartFile;
            } else if (multipartFile instanceof File) {
                multiFile = new FileInputStream((File) multipartFile);
            } else if (multipartFile instanceof MultipartFile) {
                multiFile = ((MultipartFile) multipartFile).getInputStream();
            } else if (multipartFile instanceof byte[]) {
                multiFile = new ByteArrayInputStream((byte[]) multipartFile);
            } else {
                throw new RemoteException("HttpMultiFile only supports type of (InputStream、File、MultipartFile、byte[])");
            }
        }

        httpRequest.setMultiForm(multiParamMap);
        httpRequest.setMultiFile(multiFile);
        httpRequest.setMultiFileName(multiFileName);
        return super.resolve(argv, httpRequest, variables, multiParams);
    }
}
