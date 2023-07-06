package com.github.twitch4j.codegen.core.domain.template;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class NitroGeneratorImport {

    private String importpath;

    public static NitroGeneratorImport of(Map<String, Object> data) {
        NitroGeneratorImport response = new NitroGeneratorImport();
        response.importpath = (String) data.get("import");
        return response;
    }

    public static List<NitroGeneratorImport> ofList(Collection<Map<String, Object>> data) {
        return data.stream().map(d -> of(d)).collect(Collectors.toList());
    }
}
