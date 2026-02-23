package net.dontcode.prj.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DontCodeProjectCreationType {
    application("application");

    private String value;

    DontCodeProjectCreationType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
