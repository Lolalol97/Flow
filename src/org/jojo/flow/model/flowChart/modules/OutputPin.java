package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.model.data.Data;
import org.jojo.flow.model.flowChart.connections.Connection;
import org.jojo.flow.model.flowChart.connections.DefaultArrow;

public class OutputPin extends ModulePin {
    public OutputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    public boolean putData(final Data data) {
        boolean ok = true;
        for (final Connection connection : getConnections()) {
            if (connection instanceof DefaultArrow) {
                final DefaultArrow arrow = (DefaultArrow)connection;
                ok &= arrow.putData(data);
            }
        }
        notifyObservers(data);
        return ok;
    }
}
