package org.springframework.feign.invoke.template;

import feign.RequestTemplate;
import org.springframework.feign.invoke.method.FeignMethodMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.Map;

import static feign.Util.checkArgument;

/**
 * @author shanhuiming
 */
public class FeignMultipartRequestFactory extends FeignRequestFactory {

    private final String boundary = "----boundary";

    public FeignMultipartRequestFactory(FeignMethodMetadata metadata) {
        super(metadata);
    }

    @Override
    protected FeignRequestTemplate resolve(Object[] argv, RequestTemplate template, Map<String, Object> variables) throws IOException {
        Map<String, ?> multipartForm = null;
        if (metadata.multipartFormIndex() != null) {
            Object form = argv[metadata.multipartFormIndex()];
            if (form instanceof Map) {
                multipartForm = (Map<String, ?>) form;
            } else {
                throw new RemoteException("multipartForm can only supports type of Map<String, ?>");
            }
        }

        if (metadata.multipartFileIndex() != null) {
            Object multipartFile = argv[metadata.multipartFileIndex()];
            checkArgument(multipartFile != null, "MultipartFile parameter %s was null", metadata.multipartFileIndex());
            String fileName = metadata.multipartFileName();

            byte[] fileBytes;
            if (multipartFile instanceof InputStream inputStream) {
                try {
                    fileBytes = inputStream.readAllBytes();
                } finally {
                    inputStream.close();
                }
            } else if (multipartFile instanceof File file) {
                try (FileInputStream inputStream = new FileInputStream(file)) {
                    fileBytes = inputStream.readAllBytes();
                }
            } else if (multipartFile instanceof MultipartFile file) {
                fileBytes = file.getBytes();
            } else if (multipartFile instanceof byte[] bytes) {
                fileBytes = bytes;
            } else {
                throw new RemoteException("multipartFile can only supports type of (InputStream、File、MultipartFile、byte[])");
            }

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                // file
                writer.write("--" + boundary + "\r\n");
                writer.write("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
                writer.write("Content-Type: application/octet-stream\r\n\r\n");
                writer.flush();
                outputStream.write(fileBytes);
                outputStream.write("\r\n".getBytes());

                // form
                if (multipartForm != null) {
                    for (Map.Entry<String, ?> entry : multipartForm.entrySet()) {
                        writer.write("--" + boundary + "\r\n");
                        writer.write("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                        writer.write(entry.getValue() + "\r\n");
                    }
                }
                writer.write("--" + boundary + "--\r\n");
                writer.flush();
                template.body(outputStream.toByteArray(), StandardCharsets.UTF_8);
                template.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            }
        } else {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                 OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                if (multipartForm != null) {
                    for (Map.Entry<String, ?> entry : multipartForm.entrySet()) {
                        writer.write("--" + boundary + "\r\n");
                        writer.write("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                        writer.write(entry.getValue() + "\r\n");
                    }
                }
                writer.write("--" + boundary + "--\r\n");
                writer.flush();
                template.body(outputStream.toByteArray(), StandardCharsets.UTF_8);
                template.header("Content-Type", "multipart/form-data; boundary=" + boundary);
            }
        }
        return super.resolve(argv, template, variables);
    }
}
