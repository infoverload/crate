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

package io.crate.execution.dsl.phases;


import com.google.common.collect.ImmutableList;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.Writeable;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ExecutionPhase extends Writeable {

    String DIRECT_RESPONSE = "_response";
    List<String> DIRECT_RESPONSE_LIST = Collections.singletonList("_response");

    int NO_EXECUTION_PHASE = Integer.MAX_VALUE;


    enum Type {
        COLLECT(RoutedCollectPhase::new, "CollectPhase"),
        COUNT(CountPhase::new, "CountPhase"),
        FILE_URI_COLLECT(FileUriCollectPhase::new, "FileUriCollectPhase"),
        MERGE(MergePhase::new, "MergePhase"),
        FETCH(FetchPhase::new, "FetchPhase"),
        NESTED_LOOP(NestedLoopPhase::new, "NestedLoopPhase"),
        HASH_JOIN(HashJoinPhase::new, "HashJoinPhase"),
        TABLE_FUNCTION_COLLECT(in -> {
            throw new UnsupportedOperationException("TableFunctionCollectPhase is not streamable"); }, "TableFunctionCollectPhase"),
        PK_LOOKUP(PKLookupPhase::new, "PrimaryKeyLookupPhase");

        public static final List<Type> VALUES = ImmutableList.copyOf(values());

        private final Writeable.Reader<ExecutionPhase> reader;
        private final String humanName;

        Type(Reader<ExecutionPhase> reader, String humanName) {
            this.reader = reader;
            this.humanName = humanName;
        }

        public ExecutionPhase fromStream(StreamInput in) throws IOException {
            return reader.read(in);
        }

        public String humanName() {
            return humanName;
        }
    }

    Type type();

    default String name() {
        return type().humanName();
    }

    int phaseId();

    Collection<String> nodeIds();

    <C, R> R accept(ExecutionPhaseVisitor<C, R> visitor, C context);
}
