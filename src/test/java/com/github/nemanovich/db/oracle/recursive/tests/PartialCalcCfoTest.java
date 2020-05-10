package com.github.nemanovich.db.oracle.recursive.tests;

import com.github.nemanovich.db.oracle.recursive.entity.DeptCfoEntity;
import org.jdbi.v3.core.Jdbi;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;

import java.util.List;

import static com.github.nemanovich.db.oracle.recursive.utils.CustomMatchers.isDeptCfo;
import static com.github.nemanovich.db.oracle.recursive.utils.ScriptUtils.loadScript;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;

@RunWith(Parameterized.class)
public class PartialCalcCfoTest {
    @ClassRule
    public static JdbcDatabaseContainer jdbcContainer = new OracleContainer().withInitScript("db/init_db.sql");

    private static Jdbi jdbi;

    @Parameter
    public String calc_dept_cfo_script;

    @Parameters(name = "{0}")
    public static Iterable<String> scripts() {
        return asList("connect_by/calc_dept_cfo.sql", "with/calc_dept_cfo.sql");
    }

    @BeforeClass
    public static void setup() {
        jdbcContainer.start();
        jdbi = Jdbi.create(jdbcContainer.getJdbcUrl());
        jdbi.withHandle(h -> h.execute(loadScript("update_dept_cfo.sql")));
    }

    @Test
    public void testCalcCfoPartial() {
        jdbi.withHandle(h -> h.execute(loadScript(calc_dept_cfo_script)));
        jdbi.withHandle(h -> h.createCall("{call calc_dept_cfo(:id)}").bind("id", 5).invoke());

        List<DeptCfoEntity> deptCfo = jdbi.withHandle(h ->
                h.createQuery("SELECT * FROM dept_cfo").mapToBean(DeptCfoEntity.class).collect(toList())
        );

        assertThat(deptCfo, containsInAnyOrder(
                isDeptCfo(5, "S12"),
                isDeptCfo(6, "S12"),
                isDeptCfo(7, "S12"),
                isDeptCfo(8, "G123")
        ));
    }


    @Test
    public void testCalcCfoFromLeaf() {
        List<DeptCfoEntity> deptCfo = jdbi.withHandle(h -> {
                    h.execute(loadScript(calc_dept_cfo_script));
                    h.createCall("{call calc_dept_cfo(:id)}").bind("id", 8).invoke();
                    return h.createQuery("SELECT * FROM dept_cfo").mapToBean(DeptCfoEntity.class).collect(toList());
                }
        );
        assertThat(deptCfo, hasItem(isDeptCfo(8, "G123")));
    }
}

