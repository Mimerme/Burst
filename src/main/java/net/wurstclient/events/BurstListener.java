package net.wurstclient.events;

import net.wurstclient.event.Event;
import net.wurstclient.event.Listener;

import java.util.ArrayList;

public interface BurstListener extends Listener {

    public static class BurstEvent extends Event<BurstListener>
    {
        public static final BurstListener.BurstEvent INSTANCE = new BurstListener.BurstEvent();

        @Override
        public void fire(ArrayList<BurstListener> listeners) {
            for(BurstListener listener : listeners)
                listener.onBurst();
        }

        @Override
        public Class<BurstListener> getListenerType()
        {
            return BurstListener.class;
        }
    }

    public void onBurst();
}
