package org.jlab.clara.clas12.electron.analysis;

import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataBank;
import sys.ClasServiceEngine;
import org.jlab.hipo.schema.SchemaFactory;
import org.jlab.io.hipo.HipoDataEvent;

/**
 * Electron PID engine.
 *
 * @author kenjo
 * @author gurjyan (put on the new interface, work in progress...)
 */
public class ElectronPID extends ClasServiceEngine {

    SchemaFactory factory;

    /**
     * Constructor.
     */
    public ElectronPID() {
        super("EPID", "kenjo", "1.0", "electron PID");
        factory = new SchemaFactory();
        factory.initFromDirectory("CLAS12DIR", "etc/bankdefs/hipo");
    }

    @Override
    public boolean userInit(String json) {
        return true;
    }

    @Override
    public Object processDataEvent(DataEvent event) {
        if (event instanceof HipoDataEvent) {
            HipoDataEvent hipoEv = (HipoDataEvent) event;
            hipoEv.getHipoEvent().getSchemaFactory().addSchema(factory.getSchema("PHYS::electron"));

            if (event.hasBank("REC::Particle") && event.hasBank("REC::Calorimeter")) {
                DataBank ebank = event.getBank("REC::Particle");
                DataBank calbank = event.getBank("REC::Calorimeter");
                for (int irow = 0; irow < ebank.rows(); irow++) {
                    if (ebank.getByte("charge", irow) == -1) {
                        float edep = 0;
                        float px = ebank.getFloat("px", irow);
                        float py = ebank.getFloat("py", irow);
                        float pz = ebank.getFloat("pz", irow);
                        float p = (float) Math.sqrt(px * px + py * py + pz * pz);
                        for (int ical = 0; ical < calbank.rows(); ical++) {
                            if (calbank.getShort("pindex", ical) == irow) {
                                if (calbank.getByte("detector", ical) == 1
                                        || calbank.getByte("detector", ical) == 4
                                        || calbank.getByte("detector", ical) == 7) {
                                    edep += calbank.getFloat("energy", ical);
                                }
                            }
                        }

                        float sf = edep / p;
                        if (sf < 0.5 && sf>0.2) {
                            DataBank physBank = event.createBank("PHYS::electron", 1);
                            physBank.setFloat("p", 0, p);
                            physBank.setFloat("sf", 0, sf);
                            event.appendBank(physBank);
                        }
                    }
                }
            }
        }
        return event;
    }
}
