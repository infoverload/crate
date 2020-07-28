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

package io.crate.testing;

import io.crate.protocols.postgres.PGError;
import io.crate.protocols.postgres.PGErrorStatus;
import io.crate.rest.action.HttpError;
import io.crate.rest.action.HttpErrorStatus;
import org.hamcrest.Matcher;

import static io.crate.testing.MoreMatchers.withFeature;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;

public class SQLErrorMatcher {

    public static <T extends Throwable> Matcher<T> isSQLError(String msg, PGErrorStatus pgErrorStatus, HttpErrorStatus httpErrorStatus) {
        return anyOf(isPGError(msg, pgErrorStatus), isHttpError(msg, httpErrorStatus));
    }

    public static <T extends Throwable> Matcher<T> isPGError(String msg, PGErrorStatus pgErrorStatus) {
        return allOf(
            withFeature(e -> PGError.fromThrowable(e, null).message(), "error message", endsWith(msg)),
            withFeature(e -> PGError.fromThrowable(e, null).status(), "pg error status", equalTo(pgErrorStatus))
        );
    }

    public static <T extends Throwable> Matcher<T> isHttpError(String msg, HttpErrorStatus httpErrorStatus) {
        return allOf(
            withFeature(s -> HttpError.fromThrowable(s, null).message(), "error message", equalTo(msg)),
            withFeature(s -> HttpError.fromThrowable(s, null).status(), "http error status", equalTo(httpErrorStatus))
        );
    }
}