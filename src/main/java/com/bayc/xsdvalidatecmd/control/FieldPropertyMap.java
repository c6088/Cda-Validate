package com.bayc.xsdvalidatecmd.control;

import java.util.HashMap;
import java.util.Map;

public class FieldPropertyMap {
    private HashMap<String, HashMap<String, String>> mapFieldProperty = new HashMap<>();

    public HashMap<String, HashMap<String, String>> getMapFieldProperty() {
        return mapFieldProperty;
    }

    private void GetCdaCodeName(String cdaCode, String xmlfile, HashMap<String, String> mapCodeName) {
        HashMap<String, String> map;
        if (!mapFieldProperty.containsKey(cdaCode)) {
            map = new HashMap<String, String>();
            mapFieldProperty.put(cdaCode, map);
        } else {
            map = mapFieldProperty.get(cdaCode);
        }

        for (Map.Entry<String, String> entry : mapCodeName.entrySet()) {
            map.put(entry.getKey(), "Key");
            map.put(entry.getValue(), "Value");
        }
    }
}
