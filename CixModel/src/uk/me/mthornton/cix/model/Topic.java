package uk.me.mthornton.cix.model;

/*
    "topics": [
      {
        "topicId": 0,
        "forum": "string",
        "name": "string",
        "description": "string",
        "flags": "READ_ONLY",
        "unreadCount": 0,
        "latestMessageDateTime": "2019-05-19T19:15:15.823Z"
      }
    ]

 */

import java.time.Instant;

public class Topic {
    private int topicId;
    private String forum;
    private String description;
    private String flags;
    private int unreadCount;
    private Instant latestMessageDateTime;

    public int getTopicId() {
        return topicId;
    }

    public String getForum() {
        return forum;
    }

    public String getDescription() {
        return description;
    }

    public String getFlags() {
        return flags;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public Instant getLatestMessageDateTime() {
        return latestMessageDateTime;
    }
}
