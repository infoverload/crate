/*
 * Licensed to CRATE Technology GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */

package io.crate.expression.reference.doc.lucene;

import io.crate.execution.engine.fetch.FetchId;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.StoredFieldVisitor;
import org.elasticsearch.common.CheckedBiConsumer;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

public class FetchIdCollectorExpression extends LuceneCollectorExpression<Long> {

    private long fetchId;
    private int readerId;
    private int docBase;

    @Override
    public void startCollect(CollectorContext context) {
        super.startCollect(context);
        this.readerId = context.readerId();
    }

    @Override
    public void setNextDocId(int doc, boolean ordered) {
        fetchId = FetchId.encode(readerId, docBase + doc);
    }

    @Override
    public Long value() {
        return fetchId;
    }

    @Override
    public void setNextReader(LeafReaderContext context) throws IOException {
        super.setNextReader(context);
        docBase = context.docBase;
    }
}
