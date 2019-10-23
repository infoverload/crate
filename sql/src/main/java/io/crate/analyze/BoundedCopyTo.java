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

package io.crate.analyze;

import com.google.common.base.MoreObjects;
import io.crate.analyze.relations.AbstractTableRelation;
import io.crate.execution.dsl.projection.WriterProjection;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.ColumnIdent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class BoundedCopyTo {

    private final QueriedSelectRelation<? extends AbstractTableRelation<?>> relation;
    private final Symbol uri;
    private final boolean columnsDefined;
    @Nullable
    private final WriterProjection.CompressionType compressionType;
    @Nullable
    private final WriterProjection.OutputFormat outputFormat;
    @Nullable
    private final List<String> outputNames;
    /*
     * add values that should be added or overwritten
     * all symbols must normalize to literals on the shard level.
     */
    private final Map<ColumnIdent, Symbol> overwrites;

    public BoundedCopyTo(QueriedSelectRelation<? extends AbstractTableRelation<?>> relation,
                         Symbol uri,
                         @Nullable WriterProjection.CompressionType compressionType,
                         @Nullable WriterProjection.OutputFormat outputFormat,
                         @Nullable List<String> outputNames,
                         boolean columnsDefined,
                         @Nullable Map<ColumnIdent, Symbol> overwrites) {
        this.relation = relation;
        this.uri = uri;
        this.columnsDefined = columnsDefined;
        this.compressionType = compressionType;
        this.outputNames = outputNames;
        this.outputFormat = outputFormat;
        this.overwrites = MoreObjects.firstNonNull(overwrites, Map.of());
    }

    public QueriedSelectRelation<? extends AbstractTableRelation<?>> relation() {
        return relation;
    }

    public Symbol uri() {
        return uri;
    }

    public boolean columnsDefined() {
        return columnsDefined;
    }

    @Nullable
    public WriterProjection.CompressionType compressionType() {
        return compressionType;
    }

    @Nullable
    public WriterProjection.OutputFormat outputFormat() {
        return outputFormat;
    }

    @Nullable
    public List<String> outputNames() {
        return outputNames;
    }

    public Map<ColumnIdent, Symbol> overwrites() {
        return overwrites;
    }
}