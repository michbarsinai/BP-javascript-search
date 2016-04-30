package bp.eventsets;

import bp.events.BEvent;
import bp.exceptions.BPJRequestableSetException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("serial")
public class RequestableEventSet extends ArrayList<Requestable> implements Requestable, Serializable {

    private String name;

    public RequestableEventSet(String aName, Requestable... reqs) {
        addAll(Arrays.asList(reqs));
        name = aName;
    }

    public RequestableEventSet(Requestable... reqs) {
        this( RequestableEventSet.class.getName(), reqs );
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isEvent() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return this.stream().anyMatch((r) -> (r.contains(o)));
    }

    @Override
    public BEvent getEvent() throws BPJRequestableSetException {
        throw new BPJRequestableSetException();
    }

    @Override
    public ArrayList<BEvent> getEventList() {
        ArrayList<BEvent> list = new ArrayList<>();
        this.addEventsTo(list);
        return list;
    }

    @Override
    public void addEventsTo(List<BEvent> list) {
        this.stream().forEach((ri) -> {
            ri.addEventsTo(list);
        });
    }

}
