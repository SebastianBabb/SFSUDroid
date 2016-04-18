package woverines.sfsuapp.models;

import java.util.List;


public class ForumsModel {
    public int classId;
    public String className;
    public List<Forum> forums;

    public class Forum {
        public int forumId;
        public String time;
        public String message;
    }
}
