package org.jojo.flow.model.storeLoad;

import org.jojo.flow.model.api.IConnection;
import org.jojo.flow.model.api.IFlowModule;
import org.w3c.dom.Element;

public class FlowChartDOM extends FlowChartElementDOM {
    public static final String NAME = "FlowChart";
    
    public static final String NAME_MODULES = "Modules";
    public static final String NAME_CONNECTIONS = "Connections";
    
    private final Element modules;
    private final Element connections;
    
    public FlowChartDOM() {
        super(DOM.getDocumentForCreatingElements(), DOM.getDocumentForCreatingElements().createElement(NAME));
        this.modules = getDocument().createElement(NAME_MODULES);
        this.connections = getDocument().createElement(NAME_CONNECTIONS);
        addElement(this.modules);
        addElement(this.connections);
    }

    public void addModule(final IFlowModule module) {
        this.modules.appendChild(module.getDOM().getParentNode());
    }
    
    public void addConnection(final IConnection connection) {
        this.connections.appendChild(connection.getDOM().getParentNode());
    }
}
