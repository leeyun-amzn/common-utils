/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */
package org.opensearch.commons.notifications.action

import org.apache.lucene.search.TotalHits
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.opensearch.commons.notifications.NotificationConstants.FEATURE_ALERTING
import org.opensearch.commons.notifications.NotificationConstants.FEATURE_REPORTS
import org.opensearch.commons.notifications.model.ConfigType
import org.opensearch.commons.notifications.model.DeliveryStatus
import org.opensearch.commons.notifications.model.EventSource
import org.opensearch.commons.notifications.model.EventStatus
import org.opensearch.commons.notifications.model.NotificationEvent
import org.opensearch.commons.notifications.model.NotificationEventInfo
import org.opensearch.commons.notifications.model.NotificationEventSearchResult
import org.opensearch.commons.notifications.model.SeverityType
import org.opensearch.commons.utils.createObjectFromJsonString
import org.opensearch.commons.utils.getJsonString
import org.opensearch.commons.utils.recreateObject
import java.time.Instant

internal class GetNotificationEventResponseTests {

    private fun assertSearchResultEquals(
        expected: NotificationEventSearchResult,
        actual: NotificationEventSearchResult
    ) {
        assertEquals(expected.startIndex, actual.startIndex)
        assertEquals(expected.totalHits, actual.totalHits)
        assertEquals(expected.totalHitRelation, actual.totalHitRelation)
        assertEquals(expected.objectListFieldName, actual.objectListFieldName)
        assertEquals(expected.objectList, actual.objectList)
    }

    @Test
    fun `Search result serialize and deserialize with event object should be equal`() {
        val sampleEventSource = EventSource(
            "title",
            "reference_id",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val sampleStatus = EventStatus(
            "config_id",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("404", "invalid recipient")
        )
        val sampleEvent = NotificationEvent(sampleEventSource, listOf(sampleStatus))
        val eventInfo = NotificationEventInfo(
            "event_id",
            Instant.now(),
            Instant.now(),
            sampleEvent
        )
        val searchResult = NotificationEventSearchResult(eventInfo)
        val searchResponse = GetNotificationEventResponse(searchResult)
        val recreatedObject = recreateObject(searchResponse) { GetNotificationEventResponse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result serialize and deserialize with multiple event status object should be equal`() {
        val eventSource1 = EventSource(
            "title 1",
            "reference_id_1",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val eventSource2 = EventSource(
            "title 2",
            "reference_id_2",
            FEATURE_REPORTS,
            severity = SeverityType.HIGH
        )
        val status1 = EventStatus(
            "config_id1",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("200", "success")
        )
        val status2 = EventStatus(
            "config_id2",
            "name",
            ConfigType.CHIME,
            deliveryStatus = DeliveryStatus("404", "invalid recipient")
        )
        val eventInfo1 = NotificationEventInfo(
            "event_id1",
            Instant.now(),
            Instant.now(),
            NotificationEvent(eventSource1, listOf(status1))
        )
        val eventInfo2 = NotificationEventInfo(
            "event_id2",
            Instant.now(),
            Instant.now(),
            NotificationEvent(eventSource2, listOf(status2))
        )
        val eventInfo3 = NotificationEventInfo(
            "event_id3",
            Instant.now(),
            Instant.now(),
            NotificationEvent(eventSource1, listOf(status1, status2))
        )
        val eventInfo4 = NotificationEventInfo(
            "event_id4",
            Instant.now(),
            Instant.now(),
            NotificationEvent(eventSource2, listOf(status1, status2))
        )
        val searchResult = NotificationEventSearchResult(
            100,
            1000,
            TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO,
            listOf(eventInfo1, eventInfo2, eventInfo3, eventInfo4)
        )
        val searchResponse = GetNotificationEventResponse(searchResult)
        val recreatedObject = recreateObject(searchResponse) { GetNotificationEventResponse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result serialize and deserialize using json event object should be equal`() {
        val lastUpdatedTimeMs = Instant.ofEpochMilli(Instant.now().toEpochMilli())
        val createdTimeMs = lastUpdatedTimeMs.minusSeconds(1000)
        val sampleEventSource = EventSource(
            "title",
            "reference_id",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val sampleStatus = EventStatus(
            "config_id",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("404", "invalid recipient")
        )
        val sampleEvent = NotificationEvent(sampleEventSource, listOf(sampleStatus))
        val eventInfo = NotificationEventInfo(
            "event_id",
            lastUpdatedTimeMs,
            createdTimeMs,
            sampleEvent
        )
        val searchResult = NotificationEventSearchResult(eventInfo)
        val searchResponse = GetNotificationEventResponse(searchResult)
        val jsonString = getJsonString(searchResponse)
        val recreatedObject = createObjectFromJsonString(jsonString) { GetNotificationEventResponse.parse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result serialize and deserialize using json with multiple event object should be equal`() {
        val lastUpdatedTimeMs = Instant.ofEpochMilli(Instant.now().toEpochMilli())
        val createdTimeMs = lastUpdatedTimeMs.minusSeconds(1000)
        val eventSource1 = EventSource(
            "title 1",
            "reference_id_1",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val eventSource2 = EventSource(
            "title 2",
            "reference_id_2",
            FEATURE_REPORTS,
            severity = SeverityType.HIGH
        )
        val status1 = EventStatus(
            "config_id1",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("200", "success")
        )
        val status2 = EventStatus(
            "config_id2",
            "name",
            ConfigType.CHIME,
            deliveryStatus = DeliveryStatus("404", "invalid recipient")
        )
        val eventInfo1 = NotificationEventInfo(
            "event_id1",
            lastUpdatedTimeMs,
            createdTimeMs,
            NotificationEvent(eventSource1, listOf(status1))
        )
        val eventInfo2 = NotificationEventInfo(
            "event_id2",
            lastUpdatedTimeMs,
            createdTimeMs,
            NotificationEvent(eventSource2, listOf(status2))
        )
        val searchResult = NotificationEventSearchResult(
            100,
            1000,
            TotalHits.Relation.GREATER_THAN_OR_EQUAL_TO,
            listOf(eventInfo1, eventInfo2)
        )
        val searchResponse = GetNotificationEventResponse(searchResult)
        val jsonString = getJsonString(searchResponse)
        val recreatedObject = createObjectFromJsonString(jsonString) { GetNotificationEventResponse.parse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result should safely ignore extra field in json object`() {
        val lastUpdatedTimeMs = Instant.ofEpochMilli(Instant.now().toEpochMilli())
        val createdTimeMs = lastUpdatedTimeMs.minusSeconds(1000)
        val sampleEventSource = EventSource(
            "title",
            "reference_id",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val sampleStatus = EventStatus(
            "config_id",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("200", "success")
        )
        val sampleEvent = NotificationEvent(sampleEventSource, listOf(sampleStatus))
        val eventInfo = NotificationEventInfo(
            "event_id",
            lastUpdatedTimeMs,
            createdTimeMs,
            sampleEvent
        )
        val searchResult = NotificationEventSearchResult(eventInfo)
        val jsonString = """
        {
            "start_index":"0",
            "total_hits":"1",
            "total_hit_relation":"eq",
            "event_list":[
                {
                    "event_id":"event_id",
                    "last_updated_time_ms":"${lastUpdatedTimeMs.toEpochMilli()}",
                    "created_time_ms":"${createdTimeMs.toEpochMilli()}",
                    "event":{
                        "event_source":{
                            "title":"title",
                            "reference_id":"reference_id",
                            "feature":"alerting",
                            "severity":"info",
                            "tags":[]
                        },
                        "status_list":[
                            {
                               "config_id":"config_id",
                               "config_type":"slack",
                               "config_name":"name",
                               "delivery_status":
                               {
                                    "status_code":"200",
                                    "status_text":"success"
                               }
                            }
                        ]
                    }
                }
            ],
            "extra_field_1":["extra", "value"],
            "extra_field_2":{"extra":"value"},
            "extra_field_3":"extra value 3"
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { GetNotificationEventResponse.parse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result should safely fallback to default if startIndex, totalHits or totalHitRelation field absent in json object`() {
        val lastUpdatedTimeMs = Instant.ofEpochMilli(Instant.now().toEpochMilli())
        val createdTimeMs = lastUpdatedTimeMs.minusSeconds(1000)
        val sampleEventSource = EventSource(
            "title",
            "reference_id",
            FEATURE_ALERTING,
            severity = SeverityType.INFO
        )
        val sampleStatus = EventStatus(
            "config_id",
            "name",
            ConfigType.SLACK,
            deliveryStatus = DeliveryStatus("200", "success")
        )
        val sampleEvent = NotificationEvent(sampleEventSource, listOf(sampleStatus))
        val eventInfo = NotificationEventInfo(
            "event_id",
            lastUpdatedTimeMs,
            createdTimeMs,
            sampleEvent
        )
        val searchResult = NotificationEventSearchResult(eventInfo)
        val jsonString = """
        {
            "event_list":[
                {
                    "event_id":"event_id",
                    "last_updated_time_ms":"${lastUpdatedTimeMs.toEpochMilli()}",
                    "created_time_ms":"${createdTimeMs.toEpochMilli()}",
                    "event":{
                        "event_source":{
                            "title":"title",
                            "reference_id":"reference_id",
                            "feature":"alerting",
                            "severity":"info",
                            "tags":[]
                        },
                        "status_list":[
                            {
                               "config_id":"config_id",
                               "config_type":"slack",
                               "config_name":"name",
                               "delivery_status":
                               {
                                    "status_code":"200",
                                    "status_text":"success"
                               }
                            }
                        ]
                    }
                }
            ]
        }
        """.trimIndent()
        val recreatedObject = createObjectFromJsonString(jsonString) { GetNotificationEventResponse.parse(it) }
        assertSearchResultEquals(searchResult, recreatedObject.searchResult)
    }

    @Test
    fun `Search result should throw exception if event is absent in json`() {
        val lastUpdatedTimeMs = Instant.ofEpochMilli(Instant.now().toEpochMilli())
        val createdTimeMs = lastUpdatedTimeMs.minusSeconds(1000)
        val jsonString = """
        {
            "start_index":"0",
            "total_hits":"1",
            "total_hit_relation":"eq",
            "event_list":[
                {
                    "event_id":"event_id",
                    "last_updated_time_ms":"${lastUpdatedTimeMs.toEpochMilli()}",
                    "created_time_ms":"${createdTimeMs.toEpochMilli()}"
                }
            ]
        }
        """.trimIndent()
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            createObjectFromJsonString(jsonString) { GetNotificationEventResponse.parse(it) }
        }
    }
}
