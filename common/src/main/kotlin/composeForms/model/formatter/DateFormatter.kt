/*
 *
 *   ========================LICENSE_START=================================
 *   Compose Forms
 *   %%
 *   Copyright (C) 2021 FHNW Technik
 *   %%
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   =========================LICENSE_END==================================
 *
 */

package composeForms.model.formatter

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * [DateTimeFormatter] implements the [IFormatter] for formatting dates. The formatter will recognize today, tomorrow and
 * yesterday.
 *
 * @param format: String for the format of the day. Default is "dd.MM.yyyy"
 *
 * @author Louisa Reinger, Steve Vogel
 */
class DateFormatter(private val format: String = "dd.MM.yyyy"): IFormatter<String> {

    override fun format(valueAsText: String?): String {
        try {
            val dateTimeFormatter = DateTimeFormatter.ofPattern(format)

            val date = LocalDate.parse(valueAsText, dateTimeFormatter)
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)
            val yesterday = today.minusDays(1)
            if (date == today) {
                return "Today"
            } else if (date == tomorrow) {
                return "Tomorrow"
            } else if (date == yesterday) {
                return "Yesterday"
            }

        }catch(e: Exception){ /* Could not format to date */ }
        return valueAsText?: ""
    }
}