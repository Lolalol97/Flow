package org.jojo.flow.model.storeLoad;

import java.awt.Point;
import java.util.Objects;

import org.jojo.flow.model.api.IDOM;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class PointDOM extends DOM {
    public static final String NAME = "Point";
    public static final String NAME_NAME = "name";
    public static final String NAME_X = "x";
    public static final String NAME_Y = "y";
    
    private PointDOM(Document document, Element parent) {
        super(document, parent);
    }
    
    /**
     * Gets a point DOM for the given name and point.
     * 
     * @param name - the given name
     * @param point - the given point
     * @return a point DOM for the given name and point
     */
    public static PointDOM of(final String name, final Point point) {
        Objects.requireNonNull(point);
        final Document doc = getDocumentForCreatingElements();
        final Element parent = doc.createElement(NAME);
        parent.setAttribute(NAME_NAME, Objects.requireNonNull(name));
        final PointDOM ret = new PointDOM(doc, parent);
        ret.appendString(NAME_NAME, name);
        ret.appendInt(NAME_X, point.x);
        ret.appendInt(NAME_Y, point.y);
        return ret;
    }

    /**
     * Gets the point defined in the given DOM.
     * 
     * @param p - the given DOM (must match a point DOM or else the behavior is undefined)
     * @return the point defined in the given DOM
     */
    public static Point pointOf(final IDOM p) {
        IDOM pointDom = p.getDOMMap().get(NAME) == null ? null : (IDOM)p.getDOMMap().get(NAME).getValue();
        pointDom = pointDom == null ? p : pointDom;
        final int x = Integer.parseInt(((IDOM)pointDom.getDOMMap().get(NAME_X).getValue()).elemGet());
        final int y = Integer.parseInt(((IDOM)pointDom.getDOMMap().get(NAME_Y).getValue()).elemGet());
        return new Point(x, y);
    }
}
