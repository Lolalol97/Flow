package org.jojo.flow.model.flowChart.modules;

import org.jojo.flow.exc.ListSizeException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IData;
import org.jojo.flow.model.api.IDefaultArrow;
import org.jojo.flow.model.api.IInputPin;

public class InputPin extends ModulePin implements IInputPin {  
    public InputPin(final ModulePinImp imp, final ModulePinGR gr) {
        super(imp, gr);
    }
    
    @Override
    public synchronized boolean addConnection(final IConnection toAdd) throws ListSizeException {
        if (getConnections().isEmpty()) {
            return super.addConnection(toAdd); 
        } else {
            throw new ListSizeException(new Warning(getModule(), "input pin may only have one incoming connection", true));
        }
    }
    
    @Override
    public IData getData() {
        return getConnections().isEmpty() 
                ? getDefaultData() 
                        : (getConnections().get(0) instanceof IDefaultArrow 
                                ? ((IDefaultArrow) getConnections().get(0)).getData() : getDefaultData());
    }
}
