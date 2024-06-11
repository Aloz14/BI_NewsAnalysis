package org.bi.queryserver.Domain;


import java.time.Instant;
import java.util.Objects;

public class Exposure {
    Instant exposureTime;
    Integer dwellTime;

    public Exposure(Instant exposureTime, Integer dwellTime) {
        this.exposureTime = exposureTime;
        this.dwellTime = dwellTime;
    }

    public Instant getExposureTime() {
        return exposureTime;
    }

    public void setExposureTime(Instant exposureTime) {
        this.exposureTime = exposureTime;
    }

    public Integer getDwellTime() {
        return dwellTime;
    }

    public void setDwellTime(Integer dwellTime) {
        this.dwellTime = dwellTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exposure exposure = (Exposure) o;
        return Objects.equals(exposureTime, exposure.exposureTime) && Objects.equals(dwellTime, exposure.dwellTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exposureTime, dwellTime);
    }

    @Override
    public String toString() {
        return "Exposure{" +
                "exposureTime=" + exposureTime +
                ", dwellTime=" + dwellTime +
                '}';
    }
}
