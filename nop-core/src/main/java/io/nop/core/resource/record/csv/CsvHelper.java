/**
 * Copyright (c) 2017-2024 Nop Platform. All rights reserved.
 * Author: canonical_entropy@163.com
 * Blog:   https://www.zhihu.com/people/canonical-entropy
 * Gitee:  https://gitee.com/canonical-entropy/nop-entropy
 * Github: https://github.com/entropy-cloud/nop-entropy
 */
package io.nop.core.resource.record.csv;

import io.nop.api.core.exceptions.NopException;
import io.nop.commons.util.IoHelper;
import io.nop.core.reflect.ReflectionManager;
import io.nop.core.resource.IResource;
import io.nop.core.type.IGenericType;
import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CsvHelper {

    public static <T> List<T> readCsv(IResource resource, Type type, CSVFormat format) {
        return readCsv(resource, type, format, null);
    }

    public static <T> List<T> readCsv(IResource resource, Type type, CSVFormat format, String encoding) {
        IGenericType rowType = type == null ? null : ReflectionManager.instance().buildGenericType(type);
        CsvRecordInput<T> input = new CsvRecordInput<>(resource, encoding,
                format, rowType, true, true);
        try {
            input.beforeRead(null);
            return input.readAll();
        } finally {
            IoHelper.safeCloseObject(input);
        }
    }

    public static List<Map<String, Object>> readCsv(IResource resource) {
        return readCsv(resource, null, CSVFormat.DEFAULT);
    }

    public static String format(Object value) {
        return CSVFormat.DEFAULT.format(value);
    }

    public static void print(Object value, Appendable out, boolean newRecord) throws IOException {
        CSVFormat.DEFAULT.print(value, out, newRecord);
    }

    public static <T> void writeCsv(IResource resource, CSVFormat format, List<String> headers, List<T> data) {
        CsvRecordOutput<T> output = new CsvRecordOutput<>(resource, null, format, true);
        output.setHeaders(headers);
        try {
            output.beginWrite(null);
            output.writeBatch(data);
            output.endWrite(null);
            output.flush();
        } catch (IOException e) {
            throw NopException.adapt(e);
        } finally {
            IoHelper.safeCloseObject(output);
        }
    }
}