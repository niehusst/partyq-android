/*
 * Copyright 2020 Liam Niehus-Staab
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

package com.niehusst.partyq.network.models.connection

/**
 * Wrapper for any payload sent via Nearby Connections API.
 *
 * @param type - identifies of what type of data `payload` contains
 * @param payload - the data (in JSON form) being delivered via Nearby Connections API
 */
data class ConnectionPayload(
    val type: Type,
    val payload: String
)
