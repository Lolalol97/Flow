package org.jojo.flow.model.storeLoad;

import org.jojo.flow.model.api.IGraphicalRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class FlowChartElementDOM extends DOM {
    public static final String NAME_ID = "ID";
    
    public FlowChartElementDOM(final Document document, final Node parent) {
        super(document, parent);
    }
    
    public void setGraphicalRepresentation(final IGraphicalRepresentation gr) {
        appendCustomDOM(gr);
    }

    public void setID(final int id) {
        appendInt(NAME_ID, id);
    }
}
