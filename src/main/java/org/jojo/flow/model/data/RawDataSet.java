package org.jojo.flow.model.data;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.jojo.flow.model.api.BasicType;
import org.jojo.flow.model.api.IRaw;
import org.jojo.flow.model.api.UnitSignature;

public final class RawDataSet extends BasicCheckable implements IRaw {
    /**
     * 
     */
    private static final long serialVersionUID = -2317113448843956394L;
    private final byte[] data;
    private final DataSignature dataSignature;
    
    public RawDataSet(final List<Byte> data) {
        Objects.requireNonNull(data);
        this.data = new byte[data.size()];
        for (int i = 0; i < data.size(); i++) {
            this.data[i] = data.get(i);
        }
        this.dataSignature = new BasicSignature(this);
    }
    
    public RawDataSet(final byte[] data) {
        this.data = Objects.requireNonNull(data);
        this.dataSignature = new BasicSignature(this);
    }
    
    public byte[] getData() {
        return this.data;
    }

    @Override
    public int[] getSizes() {
        return new int[] {this.data.length};
    }

    @Override
    public UnitSignature getUnitSignature() {
        return null;
    }

    @Override
    public BasicType getBasicType() {
        return null;
    }

    @Override
    protected int getDataId() {
        return DataSignature.RAW;
    }

    @Override
    public DataSignature getDataSignature() {
        return this.dataSignature;
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this.data);
    }
    
    @Override
    public boolean equals(final Object other) {
        if (super.equals(other)) {
            return Arrays.equals(this.data, ((RawDataSet)other).data);
        }
        return false;
    }

    @Override
    public String toString() {
        return Base64.getEncoder().encodeToString(this.data);
    }
}
