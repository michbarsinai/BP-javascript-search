package bprobocode.events.input;

import robocode.BulletHitBulletEvent;

/**
 * Created by orelmosheweinstock on 6/14/15.
 */
public class BPBulletHitBullet extends BPRobocodeEvent {
    public BPBulletHitBullet(BulletHitBulletEvent event) {
        super(event);
    }

    @Override
    public BulletHitBulletEvent getRobocodeEvent() {
        return (BulletHitBulletEvent) super.getRobocodeEvent();
    }
}
