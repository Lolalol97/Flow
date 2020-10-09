package org.jojo.flow.model.flowChart;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jojo.flow.model.flowChart.connections.ConnectionGR;
import org.jojo.flow.model.flowChart.modules.ModuleGR;
import org.jojo.flow.model.storeLoad.DOM;
import org.jojo.flow.model.storeLoad.FlowChartDOM;
import org.jojo.flow.model.storeLoad.GraphicalRepresentationDOM;
import org.jojo.flow.model.storeLoad.OK;
import org.jojo.flow.model.storeLoad.ParsingException;
import org.jojo.flow.model.storeLoad.PointDOM;

import static org.jojo.flow.model.storeLoad.OK.ok;

public class FlowChartGR extends FlowChartElementGR {
    private final List<ModuleGR> modules;
    private final List<ConnectionGR> connections;
    private Point absOriginPoint;
    private boolean isRasterEnabled;
    
    private FlowChart fc;
    
    public FlowChartGR() {
        super(new Point(0, 0));
        this.modules = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.absOriginPoint = new Point(0,0);
        this.isRasterEnabled = true;
    }
    
    protected void setFlowChart(final FlowChart fc) {
        this.fc = fc;
    }
    
    public FlowChart getFlowChart() {
        return this.fc;
    }
    
    public void addModule(final ModuleGR moduleGR) {
        this.modules.add(Objects.requireNonNull(moduleGR));
        notifyObservers(moduleGR);
    }
    
    public void addConnection(final ConnectionGR connectionGR) {
        this.connections.add(connectionGR);
    }
    
    public boolean removeModule(final ModuleGR moduleGR) {
        final boolean ret = this.modules.remove(Objects.requireNonNull(moduleGR));
        if (ret) {
            notifyObservers(moduleGR);
        }
        return ret;
    }
    
    public boolean removeModule(final int index) {
        if (index >= this.modules.size()) {
            return false;
        }
        final ModuleGR module = this.modules.get(index);
        this.modules.remove(index);
        notifyObservers(module);
        return true;
    }
    
    public boolean removeConnection(final ConnectionGR connectionGR) {
        final boolean ret = this.connections.remove(Objects.requireNonNull(connectionGR));
        if (ret) {
            notifyObservers(connectionGR);
        }
        return ret;
    }
    
    public boolean removeConnection(final int index) {
        if (index >= this.connections.size()) {
            return false;
        }
        final ConnectionGR con = this.connections.get(index);
        this.connections.remove(index);
        notifyObservers(con);
        return true;
    }
    
    @Override
    public int getHeight() {
        final int modMax = Math.max(this.modules
                            .stream()
                            .map(m -> m.getCorners())
                            .flatMap(p -> Arrays.stream(p))
                            .mapToInt(p -> p.x)
                            .max().orElse(0),
                            this.modules
                            .stream()
                            .map(m -> m.getModule())
                            .map(m -> m.getAllModulePins())
                            .flatMap(p -> p.stream())
                            .map(p -> p.getGraphicalRepresentation())
                            .mapToInt(g -> g.getPosition().y + g.getHeight())
                            .max().orElse(0));
        final int conMax = this.connections
                            .stream()
                            .mapToInt(c -> c.getHeight())
                            .max().orElse(0);
        return Math.max(modMax, conMax);
    }
    
    @Override
    public int getWidth() {
        final int modMax = Math.max(this.modules
                .stream()
                .map(m -> m.getCorners())
                .flatMap(p -> Arrays.stream(p))
                .mapToInt(p -> p.y)
                .max().orElse(0),
                this.modules
                    .stream()
                    .map(m -> m.getModule())
                    .map(m -> m.getAllModulePins())
                    .flatMap(p -> p.stream())
                    .map(p -> p.getGraphicalRepresentation())
                    .mapToInt(g -> g.getPosition().x + g.getWidth())
                    .max().orElse(0));
        final int conMax = this.connections
                .stream()
                .mapToInt(c -> c.getWidth())
                .max().orElse(0);
        return Math.max(modMax, conMax);
    }

    public Point getAbsOriginPoint() {
        return this.absOriginPoint;
    }

    public void setAbsOriginPoint(final Point absOriginPoint) {
        this.absOriginPoint = Objects.requireNonNull(absOriginPoint);
        notifyObservers(absOriginPoint);
    }

    public boolean isRasterEnabled() {
        return this.isRasterEnabled;
    }

    public void setRasterEnabled(boolean isRasterEnabled) {
        this.isRasterEnabled = isRasterEnabled;
        notifyObservers(isRasterEnabled);
    }

    @Override
    public DOM getDOM() {
        final GraphicalRepresentationDOM dom = (GraphicalRepresentationDOM) super.getDOM();
        dom.appendCustomPoint("absOriginPoint", this.absOriginPoint);
        dom.appendString("isRasterEnabled", "" + this.isRasterEnabled);
        dom.appendList(FlowChartDOM.NAME_MODULES, this.modules);
        dom.appendList(FlowChartDOM.NAME_CONNECTIONS, this.connections);
        return dom;
    }

    @Override
    public void restoreFromDOM(final DOM dom) {
        if (isDOMValid(dom)) {
            this.modules.clear();
            this.connections.clear();
            final Map<String, Object> domMap = dom.getDOMMap();
            final DOM absDom = (DOM)domMap.get("absOriginPoint");
            this.absOriginPoint = PointDOM.pointOf(absDom);
            final DOM reDom = (DOM)domMap.get("isRasterEnabled");
            final String reDomStr = reDom.elemGet();
            this.isRasterEnabled = Boolean.parseBoolean(reDomStr);
            //TODO modules and connections grs
            notifyObservers();
        }
    }
    
    @Override
    public boolean isDOMValid(final DOM dom) {
        Objects.requireNonNull(dom);
        try {
            ok(super.isDOMValid(dom), "GR " + OK.ERR_MSG_DOM_NOT_VALID);
            //TODO
            
            return true;
        } catch (ParsingException e) {
            e.getWarning().setAffectedElement(getFlowChart()).reportWarning();
            return false;
        }
    }
}
