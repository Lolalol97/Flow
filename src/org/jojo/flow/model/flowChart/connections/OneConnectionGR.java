package org.jojo.flow.model.flowChart.connections;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.exc.Warning;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IConnectionLineGR;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.flowChart.modules.ModulePinGR;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.PointDOM;
import org.jojo.flow.model.util.DynamicObjectLoader;

import static org.jojo.flow.model.flowChart.connections.ConnectionLineGR.isLine;
import static org.jojo.flow.model.storeLoad.OK.ok;

public class OneConnectionGR extends GraphicalRepresentation implements IOneConnectionGR {
    private IModulePinGR fromPin; // output pin
    private IModulePinGR toPin; // input pin
    
    private final List<ConnectionLineGR> lines;
    private final List<Point> diversionPoints;
    private Color color;
    
    public OneConnectionGR(final ModulePinGR fromPin, final IModulePinGR toPin) { 
        super(fromPin.getPosition());
        this.fromPin = Objects.requireNonNull(fromPin);
        Objects.requireNonNull(toPin);
        this.lines = new ArrayList<>();
        this.diversionPoints = new ArrayList<>();
        setColor(Color.BLACK);
        setToPin(new Point(fromPin.getPosition().x, toPin.getPosition().y), Objects.requireNonNull(toPin));
    }

    @Override
    public void setToPin(final Point diversionPoint, final IModulePinGR iModulePinGR) {
        Objects.requireNonNull(diversionPoint);
        Objects.requireNonNull(iModulePinGR);
        setColor(Color.BLACK);
        this.toPin = iModulePinGR;
        setPath(Arrays.asList(diversionPoint), true);
        notifyObservers();
    }
    
    @Override
    public List<IConnectionLineGR> getLines() {
        return new ArrayList<>(this.lines);
    }
 
    @Override
    public List<Point> getDiversionPoints() {
        return new ArrayList<>(this.diversionPoints);
    }
    
    @Override
    public boolean setPath(final List<Point> diversionPoints) {
        Objects.requireNonNull(diversionPoints);
        boolean ret = (!diversionPoints.isEmpty()) 
                        || (diversionPoints.isEmpty() 
                                && isLine(this.fromPin.getLinePoint(), this.toPin.getLinePoint()));
        
        for (int i = 0; ret && i < diversionPoints.size(); i++) {
            final Point divPointBefore = i == 0 ? this.fromPin.getLinePoint() : diversionPoints.get(i - 1);
            final Point divPoint = diversionPoints.get(i);

            ret &= isLine(divPointBefore, divPoint);
            if (i == diversionPoints.size() - 1) {
                ret &= isLine(divPoint, this.toPin.getLinePoint());
            }
        }
        
        setPath(diversionPoints, ret);
        return ret;
    }

    private void setPath(final List<Point> diversionPoints, final boolean ok) {
        if (ok) {
            this.lines.clear();
            this.diversionPoints.clear();
            if (diversionPoints.size() == 1 
                        && diversionPoints.get(0).equals(this.fromPin.getLinePoint())) {
                setPath(new ArrayList<>());
                return;
            }
            
            Point divPointBefore = this.fromPin.getPosition();
            for (final Point diversionPoint : diversionPoints) {
                this.lines.add(new ConnectionLineGR(divPointBefore, diversionPoint));
                divPointBefore = diversionPoint;
            }
            if (!divPointBefore.equals(this.toPin.getPosition())) {
                this.lines.add(new ConnectionLineGR(divPointBefore, this.toPin.getPosition()));
            }
            this.diversionPoints.addAll(diversionPoints);
            notifyObservers(getLines());
        } else {
            new Warning(null, "path of connection could not be set", false).reportWarning();
        }
    }

    @Override
    public int getHeight() {
        return this.diversionPoints.stream().mapToInt(p -> p.y).max().orElse(0);
    }

    @Override
    public int getWidth() {
        return this.diversionPoints.stream().mapToInt(p -> p.x).max().orElse(0);
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public void setColor(final Color color) {
        this.color = Objects.requireNonNull(color);
        notifyObservers(color);
    }
    
    @Override
    public IModulePinGR getFromPin() {
        return this.fromPin;
    }

    @Override
    public IModulePinGR getToPin() {
        return this.toPin;
    }

    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomDOM("fromPin", getFromPin());
        dom.appendCustomDOM("toPin", getToPin());
        dom.appendList("lines", getLines());
        dom.appendList("diversionPoints", getDiversionPoints()
                .stream()
                .map(x -> PointDOM.of("diversionPoint", x))
                .collect(Collectors.toList()));
        dom.appendInt("color", getColor().getRGB());
        return dom;
    }

    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            this.lines.clear();
            this.diversionPoints.clear();
            final Map<String, Object> domMap = dom.getDOMMap();
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin");
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            final IDOM cnDomFrom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnFrom = cnDomFrom.elemGet();
            this.fromPin = (ModulePinGR) DynamicObjectLoader.loadGR(cnFrom);
            this.fromPin.restoreFromDOM(fromPinDom);
            final IDOM toPinDom = (IDOM)domMap.get("toPin");
            final IDOM toPinDomGr = (IDOM) toPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            final IDOM cnDomTo = (IDOM) toPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnTo = cnDomTo.elemGet();
            this.toPin = (ModulePinGR) DynamicObjectLoader.loadGR(cnTo);
            this.toPin.restoreFromDOM(toPinDom);
            final IDOM connectionsDom = (IDOM)domMap.get("lines");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var lineObj : connectionsMap.values()) {
                if (lineObj instanceof IDOM) {
                    final IDOM lineDomGr = (IDOM) lineObj;
                    final IDOM cnDom = (IDOM) lineDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
                    final String lineToLoad = cnDom.elemGet();
                    final ConnectionLineGR line = (ConnectionLineGR) DynamicObjectLoader.loadGR(lineToLoad);
                    line.restoreFromDOM(lineDomGr);
                    this.lines.add(line);
                }
            }
            final IDOM pointsDom = (IDOM)domMap.get("diversionPoints");
            final Map<String, Object> pointsMap = pointsDom.getDOMMap();
            for (final var pointObj : pointsMap.values()) {
                if (pointObj instanceof IDOM) {
                    final IDOM pointDom = (IDOM) pointObj;
                    this.diversionPoints.add(PointDOM.pointOf(pointDom));
                }
            }
            final IDOM colorDom = (IDOM)domMap.get("color");
            final String rgbStr = colorDom.elemGet();
            this.color = Color.getColor(rgbStr, Color.BLACK);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID, (new ModelFacade()).getMainFlowChart());
            final Map<String, Object> domMap = dom.getDOMMap();
            ok(domMap.get("fromPin") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM fromPinDom = (IDOM)domMap.get("fromPin");
            ok(fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM fromPinDomGr = (IDOM) fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            ok(fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDomFrom = (IDOM) fromPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnFrom = cnDomFrom.elemGet();
            ok(cnFrom != null, OK.ERR_MSG_NULL);
            final ModulePinGR fromPin = ok(c -> (ModulePinGR) DynamicObjectLoader.loadGR(c), cnFrom);
            ok(fromPin.isDOMValid(fromPinDom), "FromPin " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get("toPin") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM toPinDom = (IDOM)domMap.get("toPin");
            ok(fromPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM toPinDomGr = (IDOM) toPinDom.getDOMMap().get(GraphicalRepresentationDOM.NAME);
            ok(toPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM cnDomTo = (IDOM) toPinDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
            final String cnTo = cnDomTo.elemGet();
            ok(cnTo != null, OK.ERR_MSG_NULL);
            final ModulePinGR toPin = ok(c -> (ModulePinGR) DynamicObjectLoader.loadGR(c), cnTo);
            ok(toPin.isDOMValid(toPinDom), "ToPin " + OK.ERR_MSG_DOM_NOT_VALID);
            ok(domMap.get("lines") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM connectionsDom = (IDOM)domMap.get("lines");
            final Map<String, Object> connectionsMap = connectionsDom.getDOMMap();
            for (final var lineObj : connectionsMap.values()) {
                if (lineObj instanceof IDOM) {
                    final IDOM lineDomGr = (IDOM) lineObj;
                    ok(lineDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
                    final IDOM cnDom = (IDOM) lineDomGr.getDOMMap().get(GraphicalRepresentationDOM.NAME_CLASSNAME);
                    final String lineToLoad = cnDom.elemGet();
                    ok(lineToLoad != null, OK.ERR_MSG_NULL);
                    final ConnectionLineGR line = ok(l -> (ConnectionLineGR) DynamicObjectLoader.loadGR(l), lineToLoad);
                    ok(line.isDOMValid(lineDomGr), "Line " + OK.ERR_MSG_DOM_NOT_VALID);
                }
            }
            ok(domMap.get("diversionPoints") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM pointsDom = (IDOM)domMap.get("diversionPoints");
            final Map<String, Object> pointsMap = pointsDom.getDOMMap();
            for (final var pointObj : pointsMap.values()) {
                if (pointObj instanceof IDOM) {
                    final IDOM pointDom = (IDOM) pointObj;
                    ok(d -> PointDOM.pointOf(d), pointDom);
                }
            }
            ok(domMap.get("color") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM colorDom = (IDOM)domMap.get("color");
            final String rgbStr = colorDom.elemGet();
            ok(rgbStr != null, OK.ERR_MSG_NULL);
            ok(s -> Color.getColor(rgbStr, Color.BLACK), rgbStr);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }
}
