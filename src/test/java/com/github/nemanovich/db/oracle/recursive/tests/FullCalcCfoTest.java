package com.github.nemanovich.db.oracle.recursive.tests;

import com.github.nemanovich.db.oracle.recursive.entity.DeptCfoEntity;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.junit.Before;
import org.junit.Rule;
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
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class FullCalcCfoTest {
    @Rule
    public JdbcDatabaseContainer jdbcContainer = new OracleContainer().withInitScript("db/init_db.sql");

    @Parameter
    public String calc_dept_cfo_script;

    private Jdbi jdbi;

    @Parameters(name = "{0}")
    public static Iterable<String> scripts() {
        return asList("connect_by/calc_dept_cfo_for_all.sql", "with/calc_dept_cfo_for_all.sql");
    }

    @Before
    public void initDb() {
        jdbcContainer.start();
        jdbi = Jdbi.create(jdbcContainer.getJdbcUrl());
        jdbi.withHandle(h -> h.execute(loadScript("update_dept_cfo.sql")));
    }

    @Test
    public void testCalcCfoWithSingleRoot() {
        List<DeptCfoEntity> deptCfo = jdbi.withHandle(h -> {
            h.execute(loadScript(calc_dept_cfo_script));
            h.createCall("{call calc_dept_cfo}").invoke();
            return h.createQuery("SELECT * FROM dept_cfo").mapToBean(DeptCfoEntity.class).collect(toList());
        });

        assertThat(deptCfo, hasSize(8));
        assertThat(deptCfo, hasItems(
                isDeptCfo(1, "D1"),
                isDeptCfo(2, "D1"),
                isDeptCfo(3, "D1"),
                isDeptCfo(4, "D1"),
                isDeptCfo(5, "S12"),
                isDeptCfo(6, "S12"),
                isDeptCfo(7, "S12"),
                isDeptCfo(8, "G123")
        ));
    }

    @Test
    public void testCalcCfoWithMultipleRoots() {
        List<DeptCfoEntity> deptCfo = jdbi.withHandle(h -> {
                    h.execute(loadScript(calc_dept_cfo_script));

                    h.execute("INSERT INTO depart VALUES (?, ?)", 9, "Dep2");
                    h.execute("INSERT INTO depart VALUES (?, ?)", 10, "Service21");
                    h.execute("INSERT INTO depart VALUES (?, ?)", 11, "Service22");
                    h.execute("INSERT INTO depart_add VALUES (?, ?, ?)", 9, "CFO", "D2");
                    h.execute("INSERT INTO depart_add VALUES (?, ?, ?)", 11, "CFO", "S22");
                    h.execute("INSERT INTO hierarchy VALUES (NULL, ?)", 9);
                    h.execute("INSERT INTO hierarchy VALUES (?, ?)", 9, 10);
                    h.execute("INSERT INTO hierarchy VALUES (?, ?)", 9, 11);

                    h.createCall("{call calc_dept_cfo}").invoke();
                    return h.createQuery("SELECT * FROM dept_cfo").mapToBean(DeptCfoEntity.class).collect(toList());
                }
        );
        assertThat(deptCfo, hasSize(11));
        assertThat(deptCfo, hasItems(
                isDeptCfo(1, "D1"),
                isDeptCfo(2, "D1"),
                isDeptCfo(3, "D1"),
                isDeptCfo(4, "D1"),
                isDeptCfo(5, "S12"),
                isDeptCfo(6, "S12"),
                isDeptCfo(7, "S12"),
                isDeptCfo(8, "G123"),
                // new tree
                isDeptCfo(9, "D2"),
                isDeptCfo(10, "D2"),
                isDeptCfo(11, "S22")
        ));
    }

    @Test
    public void testCalcUpdate() {
        List<DeptCfoEntity> deptCfo = jdbi.withHandle(h -> {
            h.execute(loadScript(calc_dept_cfo_script));
            h.createCall("{call calc_dept_cfo}").invoke();
            h.execute("UPDATE depart_add SET add_type = ?, add_value = ?, WHERE depart_id = ?", "CFO", "NEW-S12", 5);
            h.createCall("{call calc_dept_cfo}").invoke();
            return h.createQuery("SELECT * FROM dept_cfo").mapToBean(DeptCfoEntity.class).collect(toList());
        });

        assertThat(deptCfo, hasSize(8));
        assertThat(deptCfo, hasItems(
                isDeptCfo(1, "D1"),
                isDeptCfo(2, "D1"),
                isDeptCfo(3, "D1"),
                isDeptCfo(4, "D1"),
                isDeptCfo(5, "NEW-S12"),
                isDeptCfo(6, "NEW-S12"),
                isDeptCfo(7, "NEW-S12"),
                isDeptCfo(8, "G123")
        ));
    }

    @Test(timeout = 1_000, expected = UnableToExecuteStatementException.class)
    public void testCalcCfoCircle() {
        jdbi.useHandle(h -> {
                    h.execute(loadScript(calc_dept_cfo_script));
                    h.execute("INSERT INTO hierarchy VALUES (?, ?)", 5, 1);
                    h.createCall("{call calc_dept_cfo}").invoke();
                }
        );
        fail();
    }
}
