package com.tzanotto.metayway.petshop.dto.auth;

public record DecideRequest(String token, boolean approve, String secret) {}
