package net.astrona.easyclans.models;

import java.util.UUID;

public record CPlayer(UUID uuid, Integer clan, long lastActive) { }
