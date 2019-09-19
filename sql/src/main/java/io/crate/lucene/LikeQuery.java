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

package io.crate.lucene;

import io.crate.expression.operator.LikeOperator;
import io.crate.expression.symbol.Function;
import io.crate.lucene.match.CrateRegexCapabilities;
import io.crate.lucene.match.CrateRegexQuery;
import io.crate.metadata.Reference;
import io.crate.types.ArrayType;
import io.crate.types.DataType;
import io.crate.types.DataTypes;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.WildcardQuery;
import org.elasticsearch.common.lucene.BytesRefs;
import org.elasticsearch.common.lucene.search.Queries;
import org.elasticsearch.index.mapper.MappedFieldType;

import javax.annotation.Nullable;

final class LikeQuery implements FunctionToQuery {

    @Override
    public Query apply(Function input, LuceneQueryBuilder.Context context) {
        CaseAwareRefAndLiteral csRrefAndLiteral = CaseAwareRefAndLiteral.of(input);
        if (csRrefAndLiteral == null) {
            return null;
        }
        return toQuery(csRrefAndLiteral.reference(),
                       csRrefAndLiteral.literal().value(),
                       csRrefAndLiteral.ignoreCase(),
                       context);
    }

    static Query toQuery(Reference reference, Object value, boolean ignoreCase, LuceneQueryBuilder.Context context) {
        DataType dataType = ArrayType.unnest(reference.valueType());
        return like(
            dataType,
            context.getFieldTypeOrNull(reference.column().fqn()),
            value,
            ignoreCase
        );
    }

    public static Query like(DataType dataType, @Nullable MappedFieldType fieldType, Object value, boolean ignoreCase) {
        if (fieldType == null) {
            // column doesn't exist on this index -> no match
            return Queries.newMatchNoDocsQuery("column does not exist in this index");
        }
        if (dataType.equals(DataTypes.STRING)) {
            Term term = new Term(
                fieldType.name(),
                convertSqlLikeToLuceneWildcard(BytesRefs.toString(value)));
            if (ignoreCase) {
                return new CrateRegexQuery(term,
                    CrateRegexCapabilities.FLAG_CASE_INSENSITIVE | CrateRegexCapabilities.FLAG_UNICODE_CASE);
            } else {
                return new WildcardQuery(term);
            }
        }
        return fieldType.termQuery(value, null);
    }

    static String convertSqlLikeToLuceneWildcard(String wildcardString) {
        // lucene uses * and ? as wildcard characters
        // but via SQL they are used as % and _
        // here they are converted back.
        StringBuilder regex = new StringBuilder();

        boolean escaped = false;
        for (char currentChar : wildcardString.toCharArray()) {
            if (!escaped && currentChar == LikeOperator.DEFAULT_ESCAPE) {
                escaped = true;
            } else {
                switch (currentChar) {
                    case '%':
                        regex.append(escaped ? '%' : '*');
                        escaped = false;
                        break;
                    case '_':
                        regex.append(escaped ? '_' : '?');
                        escaped = false;
                        break;
                    default:
                        switch (currentChar) {
                            case '\\':
                            case '*':
                            case '?':
                                regex.append('\\');
                                break;
                            default:
                        }
                        regex.append(currentChar);
                        escaped = false;
                }
            }
        }
        return regex.toString();
    }
}
