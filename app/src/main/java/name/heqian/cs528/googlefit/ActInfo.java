package name.heqian.cs528.googlefit;

import java.util.Date;
import java.util.UUID;


public class ActInfo  {
    private UUID id;
    private String activity;
    private Date startTime;

    public ActInfo(UUID uuid) {
        id = uuid;
        activity = "Unknown Activity";
        startTime = new Date();
    }

    public ActInfo(String activity) {
        this.id = UUID.randomUUID();
        this.activity = activity;
        startTime = new Date();
    }

    public UUID getId() { return id; }

    public void setId(UUID uuid) { id = uuid; }

    public String getAct() { return activity; }

    public void setAct(String act) { activity = act; }

    public Date getStartTime() { return startTime; }

    public void setStartTime(Date start) { startTime = start; }

}
