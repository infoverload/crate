/*
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */

package io.crate.settings;

import io.crate.common.StringUtils;
import io.crate.types.DataType;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;

import java.util.List;

public class CrateSetting<T> {

    public static <T> CrateSetting<T> of(Setting<T> setting, DataType<?> dataType) {
        return new CrateSetting<>(setting, dataType);
    }

    private final Setting<T> setting;
    private final DataType<?> dataType;
    private final List<String> path;
    private final Boolean isGroupSetting;

    private CrateSetting(Setting<T> setting, DataType<?> dataType) {
        this.setting = setting;
        this.dataType = dataType;
        this.isGroupSetting = setting.getKey().endsWith(".");
        path = StringUtils.splitToList('.', setting.getKey());
    }

    public Setting<T> setting() {
        return setting;
    }

    public DataType<?> dataType() {
        return dataType;
    }

    public List<String> path() {
        return path;
    }

    public String getKey() {
        return setting.getKey();
    }

    public T getDefault() {
        return setting.getDefault(Settings.EMPTY);
    }

    public boolean isGroupSetting() {
        /**
         * there is already a method called isGroupSetting in ES Setting class
         * however it is package private.
         */
        return isGroupSetting;
    }
}
