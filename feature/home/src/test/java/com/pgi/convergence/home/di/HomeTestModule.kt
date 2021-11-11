package com.pgi.convergence.home.di

import android.content.Context
import android.graphics.Color
import com.pgi.convergence.common.profile.ProfileManager
import com.pgi.convergence.data.model.msal.Extensions
import com.pgi.convergence.data.model.msal.MsalCalEvent
import com.pgi.convergence.data.repository.msal.MSALAuthRespositoryImpl
import com.pgi.convergence.data.repository.msal.MSALGraphRepositoryImpl
import com.pgi.convergence.home.ui.HomeCardsViewModel
import com.pgi.convergence.persistance.SharedPreferencesManager
import com.pgi.convergence.ui.SharedViewModel
import com.pgi.logging.Logger
import com.pgi.network.models.SearchResult
import com.pgi.network.repository.GMElasticSearchRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockkClass
import io.reactivex.Observable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val testMsalData = "\n" +
    "{\n" +
    "    \"@odata.context\": \"\",\n" +
    "    \"value\": [\n" +
    "        {\n" +
    "            \"@odata.etag\": \"W/\\\"IiLKjG2I7E+Xv0+ys6MD0wABbkWMUg==\\\"\",\n" +
    "            \"id\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PJAAAEA==\",\n" +
    "            \"createdDateTime\": \"2017-09-04T15:55:03.4190995Z\",\n" +
    "            \"lastModifiedDateTime\": \"2019-02-05T07:57:18.2268569Z\",\n" +
    "            \"changeKey\": \"IiLKjG2I7E+Xv0+ys6MD0wABbkWMUg==\",\n" +
    "            \"categories\": [\n" +
    "                \"Green Category\"\n" +
    "            ],\n" +
    "            \"originalStartTimeZone\": \"tzone://Microsoft/Custom\",\n" +
    "            \"originalEndTimeZone\": \"Pacific Standard Time\",\n" +
    "            \"iCalUId\": \"040000008200E00074C5B7101A82E00807E3041AABFF163EF5E5CC010000000000000000100000005FA990D0D726C647B97C78717C38CC3A\",\n" +
    "            \"reminderMinutesBeforeStart\": 0,\n" +
    "            \"isReminderOn\": false,\n" +
    "            \"hasAttachments\": false,\n" +
    "            \"subject\": \"Lunch?\",\n" +
    "            \"bodyPreview\": \"When: Occurs every Friday from 12:00 PM to 1:00 PM effective 2/10/2012. (UTC-08:00) Pacific Time (US & Canada)\\r\\nWhere: TBD\\r\\n\\r\\n*~*~*~*~*~*~*~*~*~*\\r\\n\\r\\n\\r\\nWant to have lunch on Friday? I said “TBD” for restaurant, but I was thinking of Umi Sake House.\\r\\nHere’s\",\n" +
    "            \"importance\": \"normal\",\n" +
    "            \"sensitivity\": \"normal\",\n" +
    "            \"isAllDay\": false,\n" +
    "            \"isCancelled\": false,\n" +
    "            \"isOrganizer\": true,\n" +
    "            \"responseRequested\": true,\n" +
    "            \"seriesMasterId\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAENAAAiIsqMbYjsT5e-T7KzowPTAAAYbs8kAAA=\",\n" +
    "            \"showAs\": \"tentative\",\n" +
    "            \"type\": \"occurrence\",\n" +
    "            \"webLink\": \"https://outlook.office365.com/owa/?itemid=AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E%2BXv0%2Bys6MD0wAAAAABDQAAIiLKjG2I7E%2BXv0%2Bys6MD0wAAGG7PJAAAEA%3D%3D&exvsurl=1&path=/calendar/item\",\n" +
    "            \"onlineMeetingUrl\": null,\n" +
    "            \"recurrence\": null,\n" +
    "            \"responseStatus\": {\n" +
    "                \"response\": \"organizer\",\n" +
    "                \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "            },\n" +
    "            \"body\": {\n" +
    "                \"contentType\": \"html\",\n" +
    "                \"content\": \"<html><head><meta name=\\\"Generator\\\" content=\\\"Microsoft Exchange Server\\\">\\r\\n<!-- converted from text -->\\r\\n<style><!-- .EmailQuote { margin-left: 1pt; padding-left: 4pt; border-left: #800000 2px solid; } --></style></head>\\r\\n<body>\\r\\n<font size=\\\"2\\\"><span style=\\\"font-size:11pt;\\\"><div class=\\\"PlainText\\\">When: Occurs every Friday from 12:00 PM to 1:00 PM effective 2/10/2012. (UTC-08:00) Pacific Time (US &amp; Canada)<br>\\r\\nWhere: TBD<br>\\r\\n<br>\\r\\n*~*~*~*~*~*~*~*~*~*<br>\\r\\n<br>\\r\\n<br>\\r\\nWant to have lunch on Friday? I said “TBD” for restaurant, but I was thinking of Umi Sake House.<br>\\r\\nHere’s a picture of the sushi I got last time ☺<br>\\r\\n</div></span></font>\\r\\n</body>\\r\\n</html>\\r\\n\"\n" +
    "            },\n" +
    "            \"start\": {\n" +
    "                \"dateTime\": \"2019-04-26T19:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"end\": {\n" +
    "                \"dateTime\": \"2019-04-26T20:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"location\": {\n" +
    "                \"displayName\": \"TBD\",\n" +
    "                \"locationType\": \"default\",\n" +
    "                \"uniqueId\": \"TBD\",\n" +
    "                \"uniqueIdType\": \"private\"\n" +
    "            },\n" +
    "            \"locations\": [\n" +
    "                {\n" +
    "                    \"displayName\": \"TBD\",\n" +
    "                    \"locationType\": \"default\",\n" +
    "                    \"uniqueId\": \"64263a9b-9072-4dc5-9f1e-1e7e660cf514\",\n" +
    "                    \"uniqueIdType\": \"locationStore\"\n" +
    "                }\n" +
    "            ],\n" +
    "            \"attendees\": [\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Megan Bowen\",\n" +
    "                        \"address\": \"MeganB@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                }\n" +
    "            ],\n" +
    "            \"organizer\": {\n" +
    "                \"emailAddress\": {\n" +
    "                    \"name\": \"Lynne Robbins\",\n" +
    "                    \"address\": \"LynneR@M365x214355.onmicrosoft.com\"\n" +
    "                }\n" +
    "            }\n" +
    "        },\n" +
    "        {\n" +
    "            \"@odata.etag\": \"W/\\\"IiLKjG2I7E+Xv0+ys6MD0wABbkWMZQ==\\\"\",\n" +
    "            \"id\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGwAAEA==\",\n" +
    "            \"createdDateTime\": \"2017-09-04T15:55:00.0126265Z\",\n" +
    "            \"lastModifiedDateTime\": \"2019-02-05T07:57:19.1254909Z\",\n" +
    "            \"changeKey\": \"IiLKjG2I7E+Xv0+ys6MD0wABbkWMZQ==\",\n" +
    "            \"categories\": [\n" +
    "                \"Blue Category\"\n" +
    "            ],\n" +
    "            \"originalStartTimeZone\": \"tzone://Microsoft/Custom\",\n" +
    "            \"originalEndTimeZone\": \"Pacific Standard Time\",\n" +
    "            \"iCalUId\": \"040000008200E00074C5B7101A82E00807E3041AE003002EC6E5CC0100000000000000001000000001618B7374D6BF43A8CCCD1C999CED04\",\n" +
    "            \"reminderMinutesBeforeStart\": 0,\n" +
    "            \"isReminderOn\": false,\n" +
    "            \"hasAttachments\": false,\n" +
    "            \"subject\": \"Legal and Executives Bi-Weekly\",\n" +
    "            \"bodyPreview\": \"\",\n" +
    "            \"importance\": \"normal\",\n" +
    "            \"sensitivity\": \"normal\",\n" +
    "            \"isAllDay\": false,\n" +
    "            \"isCancelled\": false,\n" +
    "            \"isOrganizer\": true,\n" +
    "            \"responseRequested\": true,\n" +
    "            \"seriesMasterId\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAENAAAiIsqMbYjsT5e-T7KzowPTAAAYbs8bAAA=\",\n" +
    "            \"showAs\": \"busy\",\n" +
    "            \"type\": \"occurrence\",\n" +
    "            \"webLink\": \"https://outlook.office365.com/owa/?itemid=AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E%2BXv0%2Bys6MD0wAAAAABDQAAIiLKjG2I7E%2BXv0%2Bys6MD0wAAGG7PGwAAEA%3D%3D&exvsurl=1&path=/calendar/item\",\n" +
    "            \"onlineMeetingUrl\": null,\n" +
    "            \"recurrence\": null,\n" +
    "            \"responseStatus\": {\n" +
    "                \"response\": \"organizer\",\n" +
    "                \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "            },\n" +
    "            \"body\": {\n" +
    "                \"contentType\": \"html\",\n" +
    "                \"content\": \"<html><head><meta name=\\\"Generator\\\" content=\\\"Microsoft Exchange Server\\\">\\r\\n<!-- converted from text -->\\r\\n<style><!-- .EmailQuote { margin-left: 1pt; padding-left: 4pt; border-left: #800000 2px solid; } --></style></head>\\r\\n<body>\\r\\n<font size=\\\"2\\\"><span style=\\\"font-size:11pt;\\\"><div class=\\\"PlainText\\\">&nbsp;</div></span></font>\\r\\n</body>\\r\\n</html>\\r\\n\"\n" +
    "            },\n" +
    "            \"start\": {\n" +
    "                \"dateTime\": \"2019-04-26T15:30:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"end\": {\n" +
    "                \"dateTime\": \"2019-04-26T16:30:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"location\": {\n" +
    "                \"displayName\": \"Conference Room - Crystal\",\n" +
    "                \"locationType\": \"default\",\n" +
    "                \"uniqueId\": \"Conference Room - Crystal\",\n" +
    "                \"uniqueIdType\": \"private\"\n" +
    "            },\n" +
    "            \"locations\": [\n" +
    "                {\n" +
    "                    \"displayName\": \"Conference Room - Crystal\",\n" +
    "                    \"locationType\": \"default\",\n" +
    "                    \"uniqueId\": \"26d9fa94-8135-494b-b4f5-229134713157\",\n" +
    "                    \"uniqueIdType\": \"locationStore\"\n" +
    "                }\n" +
    "            ],\n" +
    "            \"attendees\": [\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Christie Cline\",\n" +
    "                        \"address\": \"ChristieC@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Johanna Lorenz\",\n" +
    "                        \"address\": \"JohannaL@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Lee Gu\",\n" +
    "                        \"address\": \"LeeG@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Grady Archie\",\n" +
    "                        \"address\": \"GradyA@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Joni Sherman\",\n" +
    "                        \"address\": \"JoniS@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Megan Bowen\",\n" +
    "                        \"address\": \"MeganB@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                },\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Irvin Sayers\",\n" +
    "                        \"address\": \"IrvinS@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                }\n" +
    "            ],\n" +
    "            \"organizer\": {\n" +
    "                \"emailAddress\": {\n" +
    "                    \"name\": \"Lidia Holloway\",\n" +
    "                    \"address\": \"LidiaH@M365x214355.onmicrosoft.com\"\n" +
    "                }\n" +
    "            }\n" +
    "        },\n" +
    "        {\n" +
    "            \"@odata.etag\": \"W/\\\"IiLKjG2I7E+Xv0+ys6MD0wABbkWMaQ==\\\"\",\n" +
    "            \"id\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGQAAEA==\",\n" +
    "            \"createdDateTime\": \"2017-09-04T15:54:58.9813079Z\",\n" +
    "            \"lastModifiedDateTime\": \"2019-02-05T07:57:19.2715939Z\",\n" +
    "            \"changeKey\": \"IiLKjG2I7E+Xv0+ys6MD0wABbkWMaQ==\",\n" +
    "            \"categories\": [],\n" +
    "            \"originalStartTimeZone\": \"tzone://Microsoft/Custom\",\n" +
    "            \"originalEndTimeZone\": \"Pacific Standard Time\",\n" +
    "            \"iCalUId\": \"040000008200E00074C5B7101A82E00807E3041A7001EDB7C6E5CC010000000000000000100000002E52BF9D19D67E4E9F2D36E10889FCF8\",\n" +
    "            \"reminderMinutesBeforeStart\": 0,\n" +
    "            \"isReminderOn\": false,\n" +
    "            \"hasAttachments\": false,\n" +
    "            \"subject\": \"Lidia/Isaiah 1:1\",\n" +
    "            \"bodyPreview\": \"\",\n" +
    "            \"importance\": \"normal\",\n" +
    "            \"sensitivity\": \"normal\",\n" +
    "            \"isAllDay\": true,\n" +
    "            \"isCancelled\": false,\n" +
    "            \"isOrganizer\": true,\n" +
    "            \"responseRequested\": true,\n" +
    "            \"seriesMasterId\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAENAAAiIsqMbYjsT5e-T7KzowPTAAAYbs8ZAAA=\",\n" +
    "            \"showAs\": \"busy\",\n" +
    "            \"type\": \"occurrence\",\n" +
    "            \"webLink\": \"https://outlook.office365.com/owa/?itemid=AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E%2BXv0%2Bys6MD0wAAAAABDQAAIiLKjG2I7E%2BXv0%2Bys6MD0wAAGG7PGQAAEA%3D%3D&exvsurl=1&path=/calendar/item\",\n" +
    "            \"onlineMeetingUrl\": null,\n" +
    "            \"recurrence\": null,\n" +
    "            \"responseStatus\": {\n" +
    "                \"response\": \"organizer\",\n" +
    "                \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "            },\n" +
    "            \"body\": {\n" +
    "                \"contentType\": \"html\",\n" +
    "                \"content\": \"<html><head><meta name=\\\"Generator\\\" content=\\\"Microsoft Exchange Server\\\">\\r\\n<!-- converted from text -->\\r\\n<style><!-- .EmailQuote { margin-left: 1pt; padding-left: 4pt; border-left: #800000 2px solid; } --></style></head>\\r\\n<body>\\r\\n<font size=\\\"2\\\"><span style=\\\"font-size:11pt;\\\"><div class=\\\"PlainText\\\">&nbsp;</div></span></font>\\r\\n</body>\\r\\n</html>\\r\\n\"\n" +
    "            },\n" +
    "            \"start\": {\n" +
    "                \"dateTime\": \"2019-04-26T20:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"end\": {\n" +
    "                \"dateTime\": \"2019-04-26T21:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"location\": {\n" +
    "                \"displayName\": \"Lidia's Office\",\n" +
    "                \"locationType\": \"default\",\n" +
    "                \"uniqueId\": \"Lidia's Office\",\n" +
    "                \"uniqueIdType\": \"private\"\n" +
    "            },\n" +
    "            \"locations\": [\n" +
    "                {\n" +
    "                    \"displayName\": \"Lidia's Office\",\n" +
    "                    \"locationType\": \"default\",\n" +
    "                    \"uniqueId\": \"df414709-bc96-4856-bca2-b1f3cb4a6796\",\n" +
    "                    \"uniqueIdType\": \"locationStore\"\n" +
    "                }\n" +
    "            ],\n" +
    "            \"attendees\": [\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"Isaiah Langer\",\n" +
    "                        \"address\": \"IsaiahL@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                }\n" +
    "            ],\n" +
    "            \"organizer\": {\n" +
    "                \"emailAddress\": {\n" +
    "                    \"name\": \"Lidia Holloway\",\n" +
    "                    \"address\": \"LidiaH@M365x214355.onmicrosoft.com\"\n" +
    "                }\n" +
    "            }\n" +
    "        },\n" +
    "        {\n" +
    "            \"@odata.etag\": \"W/\\\"IiLKjG2I7E+Xv0+ys6MD0wABbkWMaw==\\\"\",\n" +
    "            \"id\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E_Xv0_ys6MD0wAAAAABDQAAIiLKjG2I7E_Xv0_ys6MD0wAAGG7PGAAAEA==\",\n" +
    "            \"createdDateTime\": \"2017-09-04T15:54:58.6219096Z\",\n" +
    "            \"lastModifiedDateTime\": \"2019-02-05T07:57:19.348649Z\",\n" +
    "            \"changeKey\": \"IiLKjG2I7E+Xv0+ys6MD0wABbkWMaw==\",\n" +
    "            \"categories\": [\n" +
    "                \"Green Category\"\n" +
    "            ],\n" +
    "            \"originalStartTimeZone\": \"tzone://Microsoft/Custom\",\n" +
    "            \"originalEndTimeZone\": \"Pacific Standard Time\",\n" +
    "            \"iCalUId\": \"040000008200E00074C5B7101A82E00807E3041A5FFF87F609E6CC010000000000000000100000000F5E856BC2152F43AD52D501FF866EB1\",\n" +
    "            \"reminderMinutesBeforeStart\": 0,\n" +
    "            \"isReminderOn\": false,\n" +
    "            \"hasAttachments\": false,\n" +
    "            \"subject\": \"Friday Unwinder\",\n" +
    "            \"bodyPreview\": \"When: Occurs every Friday from 4:00 PM to 5:00 PM effective 2/10/2012. (UTC-08:00) Pacific Time (US & Canada)\\r\\nWhere: Cafeteria\\r\\n\\r\\n*~*~*~*~*~*~*~*~*~*\",\n" +
    "            \"importance\": \"normal\",\n" +
    "            \"sensitivity\": \"normal\",\n" +
    "            \"isAllDay\": false,\n" +
    "            \"isCancelled\": false,\n" +
    "            \"isOrganizer\": true,\n" +
    "            \"responseRequested\": true,\n" +
    "            \"seriesMasterId\": \"AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OABGAAAAAAAiQ8W967B7TKBjgx9rVEURBwAiIsqMbYjsT5e-T7KzowPTAAAAAAENAAAiIsqMbYjsT5e-T7KzowPTAAAYbs8YAAA=\",\n" +
    "            \"showAs\": \"tentative\",\n" +
    "            \"type\": \"occurrence\",\n" +
    "            \"webLink\": \"https://outlook.office365.com/owa/?itemid=AAMkAGVmMDEzMTM4LTZmYWUtNDdkNC1hMDZiLTU1OGY5OTZhYmY4OAFRAAgI1snaIDaAAEYAAAAAIkPFveuwe0ygY4Mfa1RFEQcAIiLKjG2I7E%2BXv0%2Bys6MD0wAAAAABDQAAIiLKjG2I7E%2BXv0%2Bys6MD0wAAGG7PGAAAEA%3D%3D&exvsurl=1&path=/calendar/item\",\n" +
    "            \"onlineMeetingUrl\": null,\n" +
    "            \"recurrence\": null,\n" +
    "            \"responseStatus\": {\n" +
    "                \"response\": \"organizer\",\n" +
    "                \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "            },\n" +
    "            \"body\": {\n" +
    "                \"contentType\": \"html\",\n" +
    "                \"content\": \"<html><head><meta name=\\\"Generator\\\" content=\\\"Microsoft Exchange Server\\\">\\r\\n<!-- converted from text -->\\r\\n<style><!-- .EmailQuote { margin-left: 1pt; padding-left: 4pt; border-left: #800000 2px solid; } --></style></head>\\r\\n<body>\\r\\n<font size=\\\"2\\\"><span style=\\\"font-size:11pt;\\\"><div class=\\\"PlainText\\\">When: Occurs every Friday from 4:00 PM to 5:00 PM effective 2/10/2012. (UTC-08:00) Pacific Time (US &amp; Canada)<br>\\r\\nWhere: Cafeteria<br>\\r\\n<br>\\r\\n*~*~*~*~*~*~*~*~*~*<br>\\r\\n<br>\\r\\n</div></span></font>\\r\\n</body>\\r\\n</html>\\r\\n\"\n" +
    "            },\n" +
    "            \"start\": {\n" +
    "                \"dateTime\": \"2019-04-26T23:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"end\": {\n" +
    "                \"dateTime\": \"2019-04-27T00:00:00.0000000\",\n" +
    "                \"timeZone\": \"UTC\"\n" +
    "            },\n" +
    "            \"location\": {\n" +
    "                \"displayName\": \"Cafeteria\",\n" +
    "                \"locationType\": \"default\",\n" +
    "                \"uniqueId\": \"Cafeteria\",\n" +
    "                \"uniqueIdType\": \"private\"\n" +
    "            },\n" +
    "            \"locations\": [\n" +
    "                {\n" +
    "                    \"displayName\": \"Cafeteria\",\n" +
    "                    \"locationType\": \"default\",\n" +
    "                    \"uniqueId\": \"5617544f-f3e4-43c9-8563-7636aa631d58\",\n" +
    "                    \"uniqueIdType\": \"locationStore\"\n" +
    "                }\n" +
    "            ],\n" +
    "            \"attendees\": [\n" +
    "                {\n" +
    "                    \"type\": \"required\",\n" +
    "                    \"status\": {\n" +
    "                        \"response\": \"none\",\n" +
    "                        \"time\": \"0001-01-01T00:00:00Z\"\n" +
    "                    },\n" +
    "                    \"emailAddress\": {\n" +
    "                        \"name\": \"All Employees\",\n" +
    "                        \"address\": \"Employees@M365x214355.onmicrosoft.com\"\n" +
    "                    }\n" +
    "                }\n" +
    "            ],\n" +
    "            \"organizer\": {\n" +
    "                \"emailAddress\": {\n" +
    "                    \"name\": \"Megan \",\n" +
    "                    \"address\": \"MeganB@M365x214355.onmicrosoft.com\"\n" +
    "                }\n" +
    "            }\n" +
    "        }\n" +
    "    ]\n" +
    "}"

@UnstableDefault
@ExperimentalCoroutinesApi
val homeTestModuleWithNoMsalUser = module {
  val graphRepositoryMock = mockkClass(MSALGraphRepositoryImpl::class, relaxed = true)

  val graphAuthRepository = mockkClass(MSALAuthRespositoryImpl::class, relaxed = true)

  val loggerMock = mockkClass(Logger::class, relaxed = true)

  val sharedPref = mockkClass(SharedPreferencesManager::class, relaxed = true)

  val gmElacticSearchRepoMock = mockkClass(GMElasticSearchRepository::class, relaxed = true)

  val sharedFeatureViewModel = mockkClass(ProfileManager::class, relaxed = true)
  
  val context = mockkClass(Context::class, relaxed = true)



  var searchResult = SearchResult()
  var searchList = emptyList<SearchResult>()
  searchResult.hubConfId = 101202
  searchResult.furl = "test@pgi.com"
  searchResult.conferenceId = 1213
  searchResult.firstName = ""
  searchResult.lastName = ""
  searchResult.profileImageUrl = "profile.com"
  searchResult.useHtml5 = true
  searchResult.brandId = 1112
  searchList += searchResult


  coEvery {
    graphAuthRepository.authenticateUser()
  } just Runs

  coEvery {
    graphAuthRepository.handleInteractiveRequestRedirect(any(), any(), any())
  } just Runs

  coEvery {
    graphAuthRepository.getUserCount()
  } returns 0

  val calEvents = Json.parse(MsalCalEvent.serializer(), testMsalData)

  coEvery {
    graphRepositoryMock.getTimedEvents(any(), any(), any(), any())
  } returns calEvents

  coEvery {
    loggerMock.error(any(), any(), any(), any(), any())
  } just Runs

  coEvery {
    sharedPref.isfirstTimeMsalUser()
  } returns true

  coEvery {
    gmElacticSearchRepoMock.suggest(any())
  } returns  Observable.just(searchList)

  val model =  HomeCardsViewModel(graphAuthRepository, graphRepositoryMock, SharedViewModel(context),
       sharedPref, sharedFeatureViewModel, loggerMock)
  model.barBG.postValue(Color.parseColor("#00aeef"))
  viewModel { model }
}

@UseExperimental(ExperimentalCoroutinesApi::class)
@UnstableDefault
val homeTestModuleWithUserCount = module {
  val graphRepositoryMock = mockkClass(MSALGraphRepositoryImpl::class, relaxed = true)

  val graphAuthRepository = mockkClass(MSALAuthRespositoryImpl::class, relaxed = true)

  val loggerMock = mockkClass(Logger::class, relaxed = true)

  val sharedPref = mockkClass(SharedPreferencesManager::class, relaxed = true)

  val gmElacticSearchRepoMock = mockkClass(GMElasticSearchRepository::class, relaxed = true)
  val sharedFeatureViewModel = mockkClass(ProfileManager::class, relaxed = true)

  val context = mockkClass(Context::class, relaxed = true)

  var searchResult = SearchResult()
  var searchList = emptyList<SearchResult>()
  searchResult.hubConfId = 101202
  searchResult.furl = "test@pgi.com"
  searchResult.conferenceId = 1213
  searchResult.firstName = ""
  searchResult.lastName = ""
  searchResult.profileImageUrl = "profile.com"
  searchResult.useHtml5 = true
  searchResult.brandId = 1112
  searchList += searchResult



  coEvery {
    graphAuthRepository.authenticateUser()
  } just Runs

  coEvery {
    graphAuthRepository.isTokenExpired()
  } returns false

  coEvery {
    graphAuthRepository.getUserCount()
  } returns 1

  val calEvents = Json.parse(MsalCalEvent.serializer(), testMsalData)
  coEvery {
    graphRepositoryMock.getTimedEvents(any(), any(), any(), any())
  } returns calEvents

  coEvery {
    graphRepositoryMock.dismissCard(any(), any(), any())
  } returns Extensions("test")

  coEvery {
    loggerMock.error(any(), any(), any(), any(), any())
  } just Runs

  coEvery {
    sharedPref.isfirstTimeMsalUser()
  } returns false

  coEvery {
    gmElacticSearchRepoMock.suggest(any())
  } returns  Observable.just(searchList)

  val model = HomeCardsViewModel(graphAuthRepository, graphRepositoryMock, SharedViewModel(context),
       sharedPref, sharedFeatureViewModel , loggerMock)
  model.barBG.postValue(Color.parseColor("#00aeef"))
  model.office365Token = "sfsdfsfsfss"
  viewModel { model }
}