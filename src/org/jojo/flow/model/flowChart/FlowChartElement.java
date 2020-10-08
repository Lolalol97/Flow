package org.jojo.flow.model.flowChart;

import java.util.ArrayList;
import java.util.List;

import org.jojo.flow.model.Subject;
import org.jojo.flow.model.Warning;
import org.jojo.flow.model.flowChart.modules.InternalConfig;
import org.jojo.flow.model.storeLoad.DOMable;

public abstract class FlowChartElement extends Subject implements DOMable {
    public static final FlowChartElement GENERIC_ERROR_ELEMENT = new FlowChart(-1);
    
    private final int id;
    private final List<Warning> warnings;
    
    public FlowChartElement(final int id) {
        this.id = id;
        this.warnings = new ArrayList<>();
    }
    
    public abstract GraphicalRepresentation getGraphicalRepresentation();
    public abstract InternalConfig serializeInternalConfig();
    public abstract void restoreSerializedInternalConfig(InternalConfig internalConfig);
    public abstract String serializeSimulationState();
    public abstract void restoreSerializedSimulationState(String simulationState);
    
    public void reportWarning(final Warning warning) {
        this.warnings.add(warning);
        notifyObservers(warning);
    }
    
    public void warningResolved(final Warning warning) {
        this.warnings.remove(warning);
        notifyObservers(warning);
    }
    
    public int getId() {
        return id;
    }
    
    public List<Warning> getWarnings() {
        return new ArrayList<>(this.warnings);
    }

    public Warning getLastWarning() {
        return this.warnings.get(this.warnings.size() - 1);
    }
    
    @Override
    public int hashCode() {
        return this.id;
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other instanceof FlowChartElement) {
            return hashCode() == other.hashCode();
        }
        return false;
    }
}
