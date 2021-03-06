package org.jojo.flow.model.flowChart.connections;

import org.jojo.flow.model.api.IConnectionGR;
import org.jojo.flow.model.api.IModulePinGR;
import org.jojo.flow.model.api.IOneConnectionGR;
import org.jojo.flow.model.flowChart.FlowChartElementGR;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.jojo.flow.model.flowChart.modules.ModulePinGR;

public abstract class ConnectionGR extends FlowChartElementGR implements IConnectionGR {
    private ModulePinGR fromPin;
    private final List<IOneConnectionGR> connections;
    
    public ConnectionGR(final ModulePinGR fromPin, final ModulePinGR toPin) { 
        super(fromPin.getPosition());
        this.fromPin = Objects.requireNonNull(fromPin);
        this.connections = new ArrayList<>();
        addToPin(new Point(fromPin.getLinePoint().x, toPin.getLinePoint().y), toPin);
    }
    
    @Override
    public abstract void addToPin(final Point diversionPoint, final IModulePinGR toPin);
    
    /**
     * Determines whether the connection is addable, i.e. the connection is not {@code null} and
     * the from pin of this connection and the connection to add are equal.
     * 
     * @param connection - the connection
     * @return whether the connection is addable
     */
    protected boolean isAddable(final OneConnectionGR connection) {
        if (connection == null || !connection.getFromPin().equals(this.fromPin)) {
            return false;
        }
        return true;
    }
    
    /**
     * Adds the given connection.
     * 
     * @param c - the given connection
     * @see #isAddable(OneConnectionGR)
     */
    protected void addConnection(final IOneConnectionGR c) {
        Objects.requireNonNull(c);
        if (!c.getFromPin().equals(this.fromPin)) {
            throw new IllegalArgumentException("from pin is not the same as the one of this connection");
        }
        this.connections.add(c);
        notifyObservers(c);
    }
    
    /**
     * Sets the from pin.
     * 
     * @param fromPin - the from pin
     */
    protected void setFromPin(final ModulePinGR fromPin) {
        this.fromPin = fromPin;
    }
    
    /**
     * Deletes all connections of this connection.
     */
    protected void deleteAllConnections() {
        this.connections.clear();
    }
    
    @Override
    public boolean removeToPin(final IModulePinGR toPin) {
        final List<IOneConnectionGR> toRemove = this.connections
                .stream()
                .filter(c -> c.getToPin().equals(toPin))
                .collect(Collectors.toList());
        boolean ret = !toRemove.isEmpty();
        for (final IOneConnectionGR toRemoveElem : toRemove) {
            ret &= this.connections.remove(toRemoveElem);
        }
        if (ret) {
            notifyObservers(toRemove);
        }
        return ret;
    }
    
    @Override
    public ModulePinGR getFromPin() {
        return this.fromPin;
    }
    
    @Override
    public List<IModulePinGR> getToPins() {
        return this.connections.stream().map(c -> c.getToPin()).collect(Collectors.toList());
    }
    
    @Override
    public List<IOneConnectionGR> getSingleConnections() {
        return new ArrayList<>(this.connections);
    }
    
    @Override
    public void setColor(final Color color) {
        Objects.requireNonNull(color);
        this.connections.forEach(c -> c.setColor(color));
        notifyObservers(color);
    }
    
    @Override
    public int getHeight() {
        final int maxTo = getMaxTo(false);
        return Math.abs(this.fromPin.getLinePoint().y - maxTo);
    }
    
    private int getMaxTo(final boolean isX) {
        return this.connections
                .stream()
                .map(c -> c.getToPin().getLinePoint())
                .mapToInt(p -> isX ? (Math.abs(this.fromPin.getLinePoint().x - p.x)) 
                        : (Math.abs(this.fromPin.getLinePoint().y - p.y)))
                .max().orElse(isX ? this.fromPin.getLinePoint().x : this.fromPin.getLinePoint().y);
    }

    @Override
    public int getWidth() {
        final int maxTo = getMaxTo(true);
        return Math.abs(this.fromPin.getLinePoint().x - maxTo);
    }
}
