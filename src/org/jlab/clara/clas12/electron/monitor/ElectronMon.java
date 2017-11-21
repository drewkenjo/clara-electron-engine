package org.jlab.clara.clas12.electron.monitor;

import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataBank;
import sys.ClasServiceEngine;

/**
 * Electron Monitoring engine.
 *
 * @author kenjo
 * @author gurjyan (put on the new interface, work in progress...)
 */
public class ElectronMon extends ClasServiceEngine {

    /**
     * Constructor.
     */
    public ElectronMon() {
        super("EMon", "kenjo", "1.0", "electron Mon");
    }

    @Override
    public boolean userInit(String json) {
        return true;
    }

    @Override
    public Object processDataEvent(DataEvent event) {
        //if (event.hasBank("REC::Particle"))
        if (event.hasBank("PHYS::electron")) {
            DataBank ebank = event.getBank("PHYS::electron");
            float sf = ebank.getFloat("sf", 0);
            addTsObservable("electronSF", sf);
            publishTsObservables();
        }
        return event;
    }

}
