package net.dontcode.prj.model;

public record DontCodeProjectCreation(String name, DontCodeProjectCreationType type, DontCodeProjectEntities[] entities) {
}
