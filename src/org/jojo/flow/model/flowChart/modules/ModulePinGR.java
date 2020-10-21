package org.jojo.flow.model.flowChart.modules;

import static org.jojo.flow.model.storeLoad.OK.ok;

import java.awt.Point;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.exc.ParsingException;
import org.jojo.flow.model.ModelFacade;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.api.PinOrientation;
import org.jojo.flow.model.flowChart.GraphicalRepresentation;
import org.jojo.flow.model.api.IDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.PointDOM;

public abstract class ModulePinGR extends GraphicalRepresentation implements IModulePinGR {
    private int height;
    private int width;
    
    private boolean isIconTextAllowed;
    private String iconText;
    
    private Point linePoint;
    private PinOrientation pinOrientation;
    
    public ModulePinGR(final Point position, final String iconText,
            final int height, final int width) {
        super(position);
        this.height = height;
        this.width = width;
        setIconTextAllowed(iconText != null);
        setIconText(iconText);
        setLinePoint(position);
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public boolean isIconTextAllowed() {
        return this.isIconTextAllowed;
    }

    @Override
    public void setIconTextAllowed(final boolean isIconTextAllowed) {
        this.isIconTextAllowed = isIconTextAllowed;
        notifyObservers(isIconTextAllowed);
    }

    @Override
    public String getIconText() {
        return this.iconText;
    }

    @Override
    public void setIconText(final String iconText) {
        this.iconText = iconText;
        notifyObservers(iconText);
    }

    @Override
    public Point getLinePoint() {
        return this.linePoint;
    }

    @Override
    public void setLinePoint(final Point linePoint) {
        this.linePoint = Objects.requireNonNull(linePoint);
        notifyObservers(linePoint);
    }

    @Override
    public PinOrientation getPinOrientation() {
        return this.pinOrientation;
    }

    @Override
    public void setPinOrientation(PinOrientation pinOrientation) {
        this.pinOrientation = Objects.requireNonNull(pinOrientation);
        notifyObservers(pinOrientation);
    }
    
    @Override
    public IDOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendString("isIconTextAllowed", "" + isIconTextAllowed());
        dom.appendString("iconText", "" + getIconText());
        dom.appendCustomPoint("linePoint", getLinePoint());
        dom.appendString("pinOrientation", getPinOrientation().toString());
        return dom;
    }
    
    @Override
    public void restoreFromDOM(final IDOM dom) {
        if (isDOMValid(dom)) {
            super.restoreFromDOM(dom);
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                domMap = ((IDOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            final IDOM hDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT);
            final String hStr = hDom.elemGet();
            this.height = Integer.parseInt(hStr);
            final IDOM wDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_WIDTH);
            final String wStr = wDom.elemGet();
            this.width = Integer.parseInt(wStr);
            final IDOM domIs = (IDOM) domMap.get("isIconTextAllowed");
            final String str = domIs.elemGet();
            this.isIconTextAllowed = Boolean.parseBoolean(str);
            final IDOM domIct = (IDOM) domMap.get("iconText");
            this.iconText = domIct.elemGet() == null ? "" : domIct.elemGet();
            final IDOM lp = (IDOM) domMap.get("linePoint");
            this.linePoint = PointDOM.pointOf(lp);
            final IDOM pOr = (IDOM) domMap.get("pinOrientation");
            final String pOrName = pOr.elemGet();
            this.pinOrientation = PinOrientation.of(pOrName);
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final IDOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID);
            Map<String, Object> domMap = dom.getDOMMap();
            if (domMap.containsKey(GraphicalRepresentationDOM.NAME)) {
                ok(domMap.get(GraphicalRepresentationDOM.NAME) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
                domMap = ((IDOM)domMap.get(GraphicalRepresentationDOM.NAME)).getDOMMap();
            }
            ok(domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM hDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_HEIGHT);
            final String hStr = hDom.elemGet();
            ok(hStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), hStr);
            ok(domMap.get(GraphicalRepresentationDOM.NAME_WIDTH) instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM wDom = (IDOM) domMap.get(GraphicalRepresentationDOM.NAME_WIDTH);
            final String wStr = wDom.elemGet();
            ok(wStr != null, OK.ERR_MSG_NULL);
            ok(s -> Integer.parseInt(s), wStr);
            ok(domMap.get("isIconTextAllowed") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM domIs = (IDOM) domMap.get("isIconTextAllowed");
            final String str = domIs.elemGet();
            ok(str != null, OK.ERR_MSG_NULL);
            ok(s -> Boolean.parseBoolean(s), str);
            ok(domMap.get("iconText") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            ok(domMap.get("linePoint") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM lp = (IDOM) domMap.get("linePoint");
            ok(p -> PointDOM.pointOf(p), lp);
            ok(domMap.get("pinOrientation") instanceof IDOM, OK.ERR_MSG_WRONG_CAST);
            final IDOM pOr = (IDOM) domMap.get("pinOrientation");
            final String pOrName = pOr.elemGet();
            ok(pOrName != null, OK.ERR_MSG_NULL);
            ok(PinOrientation.of(pOrName) != null, OK.ERR_MSG_NULL);
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement((new ModelFacade()).getMainFlowChart()).reportWarning();
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(getClass(), this.linePoint);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (getClass().equals(other.getClass())) {
            return this.linePoint.equals(((ModulePinGR)other).linePoint);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return super.toString() + " and linePoint= "  + getLinePoint();
    }
}
