/*
 * Licensed to Crate.IO GmbH ("Crate") under one or more contributor
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

package io.crate.analyze;

import io.crate.analyze.expressions.ExpressionAnalysisContext;
import io.crate.analyze.expressions.ExpressionAnalyzer;
import io.crate.analyze.relations.FieldProvider;
import io.crate.expression.symbol.Symbol;
import io.crate.metadata.CoordinatorTxnCtx;
import io.crate.metadata.Functions;
import io.crate.sql.tree.Expression;
import io.crate.sql.tree.KillStatement;

public class KillAnalyzer {

    private final Functions functions;

    KillAnalyzer(Functions functions) {
        this.functions = functions;
    }

    public AnalyzedKill analyze(KillStatement<Expression> killStatement,
                                ParamTypeHints paramTypeHints,
                                CoordinatorTxnCtx txnCtx) {
        Symbol jobId;
        if (killStatement.jobId() != null) {
            var exprAnalyzerWithoutFields = new ExpressionAnalyzer(
                functions, txnCtx, paramTypeHints, FieldProvider.UNSUPPORTED, null);
            jobId = exprAnalyzerWithoutFields.convert(
                killStatement.jobId(),
                new ExpressionAnalysisContext());
        } else {
            jobId = null;
        }
        return new AnalyzedKill(jobId);
    }
}