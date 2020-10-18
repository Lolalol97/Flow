package org.jojo.flow.model.flowChart.connections;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.awt.Shape;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IDefaultArrowGR;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.flowChart.modules.DefaultInputPinGR;
import org.jojo.flow.model.flowChart.modules.DefaultOutputPinGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.util.DynamicObjectLoader;

public class DefaultArrowGR extends ConnectionGR implements IDefaultArrowGR {
    private Shape defaultArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    private Shape selectedArrow; //TODO evtl. anderer Typ jenachdem ob das so geht
    
    public DefaultArrowGR(final DefaultOutputPinGR fromPin, final DefaultInputPinGR toPin, final Shape defaultArrow) {
        super(fromPin, toPin);
        this.defaultArrow = defaultArrow;
    }

    @Override
    public void addToPin(final Point diversionPoint, final IModulePinGR toPin) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(toPin);
        if (!(toPin instanceof DefaultInputPinGR)) {
            throw new IllegalArgumentException("to pin GR must be default input pin GR");
        }
        addConnection(new OneConnectionGR(getFromPin(), toPin));
    }

    @Override
    public Shape getDefaultArrow() {
        return this.defaultArrow;
    }

    @Override
    public void setDefaultArrow(final Shape defaultArrow) {
        this.defaultArrow = Objects.requireNonNull(defaultArrow);
        notifyObservers(defaultArrow);
    }

    @Override
    public Shape getSelectedArrow() {
        return this.selectedArrow;
    }

    @Override
    public void setSelectedArrow(final Shape selectedArrow) {
        this.selectedArrow = Objects.requireNonNull(selectedArrow);
        notifyObservers(selectedArrow);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendList("connections", getSingleConnections());
        dom.appendString("defaultArrow", "TODO"); //TODO class name of arrow for recreation of shape
        dom.appendString("selectedArrow", "TODO");//TODO class name of arrow for recreation of shape
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            deleteAllConnections();
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM fromPinDom = (DOM)domMap.get("fromPin");
            final DOM fromPinDomGr = (DOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            final DOM cnDom = (DOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cn = cnDom.elemGet();
            final DefaultOutputPinGR fromPin = (DefaultOutputPinGR)DynamicObjectLoader.loadGR(cn);
            fromPin.restoreFromDOM(fromPinDom);
            setFromPin(fromPin);
            final DOM connectionsDom = (DOM)domMap.get("connections");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof DOM) {
                    final DOM connectionDom = (DOM)conObj;
                    final OneConnectionGR con = (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName());
                    con.restoreFromDOM(connectionDom);
                    addConnection(con);
                }
            }
            //TODO Arrow Shapes
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "FCE_GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("fromPin") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM fromPinDom = (DOM)domMap.get("fromPin");
            ok(fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM fromPinDomGr = (DOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            ok(fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM cnDomFrom = (DOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnFrom = cnDomFrom.elemGet();
            ok(cnFrom != null, OK.ERR_MSG_NULL);
            final DefaultOutputPinGR fromPin = ok(c -> (DefaultOutputPinGR) DynamicObjectLoader.loadGR(c), cnFrom);
            ok(fromPin.isDOMValid(fromPinDom), "FromPin " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get("connections") instanceof DOM, OK.ERR_MSG_WRONG_CAST);
            final DOM connectionsDom = (DOM)domMap.get("connections");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var conObj : connectionsMap.values()) {
                if (conObj instanceof DOM) {
                    final DOM connectionDom = (DOM)conObj;
                    final OneConnectionGR con = ok(d -> (OneConnectionGR) DynamicObjectLoader.loadGR(OneConnectionGR.class.getName()), "");
                    ok(con.isDOMValid(connectionDom), "OneConnectionGR " + OK.ERR_MSG_DOM_NOT_VALID);
                    ok(con.getToPin() instanceof DefaultInputPinGR, OK.ERR_MSG_WRONG_CAST);
                    ok(isAddable(con), "OneConnectionGR " + con + " not addable");
                }
            }
            //TODO Arrow Shapes
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }

}
