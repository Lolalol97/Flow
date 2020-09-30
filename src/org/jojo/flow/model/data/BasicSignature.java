package org.jojo.flow.model.data;

import java.util.Arrays;

public class BasicSignature extends DataSignature {
    private final BasicSignatureComponentSignature[] components;
    
    public BasicSignature(final BasicCheckable data) {
        super(data.getDataId());
        this.components = new BasicSignatureComponentSignature[BasicSignatureComponents.values().length];
        this.components[BasicSignatureComponents.BASIC_TYPE.index] = new BasicTypeDataSignature(data.getBasicType());
        this.components[BasicSignatureComponents.SIZES.index] = new SizesDataSignature(data.getSizes());
        this.components[BasicSignatureComponents.UNIT.index] = new UnitDataSignature(data.getUnitSignature()); 
    }
    
    private BasicSignature(final BasicSignature toCopy) {
        super(toCopy.getDataId());
        this.components = Arrays.stream(toCopy.components)
                .map(x -> x.getCopy())
                .toArray(BasicSignatureComponentSignature[]::new);
    }

    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.components, ((BasicSignature) other).components);
        }
        return false;
    }

    @Override
    public DataSignature getCopy() {
        return new BasicSignature(this);
    }

    @Override
    public DataSignature getComponent(int index) {
        return this.components[index];
    }

    @Override
    public int size() {
        return this.components.length;
    }
}
