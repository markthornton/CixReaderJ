package uk.me.mthornton.cix.model;

/*
[
  {
    "name": "string",
    "category": "string",
    "subCategory": "string",
    "status": "OPEN",
    "summary": "string",
    "description": "string",
    "pictureUrl": "string",
    "isParticipant": true,
    "joinPending": true,
    "flags": "PERMANENT",
    "moderators": [
      "string"
    ],
    "participants": [
      "string"
    ],
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
  }
]



 */

import java.util.List;
import java.util.Set;

public class Forum {
    private String name;
    private String category;
    private String subCategory;
    private String status;
    private String summary;
    private String description;
    private String pictureUrl;
    private boolean isParticipant;
    private boolean joinPending;
    private String flags;
    private Set<String> moderators;
    private Set<String> participants;
    private List<Topic> topics;

    public ChangeEvent updateFrom(Forum forum) {
        if (!name.equals(forum.name)) {
            throw new IllegalArgumentException();
        }
        UpdateEvent update = new UpdateEvent(this);
        category = update.compare("category", category, forum.category);
        subCategory = update.compare("subCategory", subCategory, forum.subCategory);
        status = update.compare("status", status, forum.status);
        summary = update.compare("summary", summary, forum.summary);
        description = update.compare("description", description, forum.description);
        pictureUrl = update.compare("pictureUrl", pictureUrl, forum.pictureUrl);
        isParticipant = update.compare("isParticpant", isParticipant, forum.isParticipant);
        joinPending = update.compare("joinPending", joinPending, forum.joinPending);
        flags = update.compare("flags", flags, forum.flags);
        moderators = update.compare("moderators", moderators, forum.moderators);
        participants = update.compare("particpants", participants, forum.participants);
        // Don't update topics here
        return update.isEmpty() ? null : update;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getStatus() {
        return status;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public boolean isParticipant() {
        return isParticipant;
    }

    public boolean isJoinPending() {
        return joinPending;
    }

    public String getFlags() {
        return flags;
    }

    public Set<String> getModerators() {
        return moderators;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public List<Topic> getTopics() {
        return topics;
    }
}
