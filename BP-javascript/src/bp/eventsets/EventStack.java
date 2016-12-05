package bp.eventsets;

import java.util.Iterator;
import java.util.Stack;

/**
 * Part of the whileBlocking mechanism.
 */
public class EventStack extends Stack<EventSet>
        implements EventSet {

    protected String _name;

    public EventStack(EventSet... esis) {
        super();

        for (EventSet esi : esis) {
            push(esi);
        }
    }

    public EventStack(String name, EventSet... esis) {
        this(esis);
        this._name = name;
    }

    @Override
    public boolean contains(Object o) {
        Iterator<EventSet> itr = this.iterator();

        while (itr.hasNext()) {
            EventSet eSetInterface = itr.next();
            if (eSetInterface.contains(o)) {
                return true;
            }
        }

        return false;
    }

    public String toString() {
        if (_name != null) {
            return _name;
        } else {
            return super.toString();
        }
    }

}
