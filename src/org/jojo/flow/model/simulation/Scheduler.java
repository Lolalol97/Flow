package org.jojo.flow.model.simulation;

import java.util.List;

import org.jojo.flow.model.flowChart.modules.FlowModule;

public abstract class Scheduler {
    public abstract List<FlowModule> getSchedule(List<FlowModule> modules);
}
