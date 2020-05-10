package com.github.nemanovich.db.oracle.recursive.utils;

import com.github.nemanovich.db.oracle.recursive.entity.DeptCfoEntity;
import org.hamcrest.Matcher;
import org.hamcrest.beans.SamePropertyValuesAs;
import org.jetbrains.annotations.NotNull;


public class CustomMatchers {
    @NotNull
    public static Matcher<DeptCfoEntity> isDeptCfo(int i, String d1) {
        return SamePropertyValuesAs.samePropertyValuesAs(new DeptCfoEntity(i, d1));
    }
}
