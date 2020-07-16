package ru.mineradio.psvs_app;
/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();

    //HM-10 HM-10 Services and Characteristics
    public static String CUSTOM_SERVICE3 = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static String CUSTOM_CHARACTERISTIC = "0000ffe1-0000-1000-8000-00805f9b34fb";

    static {
        //HM-10 (MLT-BT05)
        attributes.put(CUSTOM_SERVICE3, "MLT-BT05");
        attributes.put(CUSTOM_CHARACTERISTIC, "CUSTOMER DATA");

    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}