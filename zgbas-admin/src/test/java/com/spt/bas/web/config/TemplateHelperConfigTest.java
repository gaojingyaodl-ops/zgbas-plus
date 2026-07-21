package com.spt.bas.web.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the guard-clause behavior of {@link DictTemplateHelper} and
 * {@link PermissionTemplateHelper}.
 *
 * <p>These tests cover the null/empty input safety behaviors specified in
 * 02-01-PLAN.md Task 1 &lt;behavior&gt;:
 * <ul>
 *   <li>{@code getLabel(null, "0")} returns ""</li>
 *   <li>{@code getLabel(type, null)} returns ""</li>
 *   <li>{@code hasPermi(null)} returns false</li>
 *   <li>{@code hasPermi("")} returns false</li>
 *   <li>{@code lacksPermi("xxx")} returns {@code !hasPermi("xxx")}</li>
 * </ul>
 *
 * <p>The delegation paths ({@code getType} → {@code DictUtil.getListByCategory},
 * {@code hasPermi(non-null)} → {@code ShiroUtil.isPermitted}) involve static method
 * calls that require a Shiro Subject / initialized dict cache. Those paths are
 * verified by the capstone {@link com.spt.ZgbasApplicationTest} context-load smoke
 * and the 02-03 end-to-end plan, not by these unit tests.
 *
 * <p>TDD RED phase: these tests fail because {@link DictTemplateHelper} and
 * {@link PermissionTemplateHelper} do not exist yet.
 */
class TemplateHelperConfigTest {

    // ---- DictTemplateHelper.getLabel guard clauses ----

    @Test
    @DisplayName("getLabel returns empty string when dictType is null")
    void getLabel_nullDictType_returnsEmpty() {
        DictTemplateHelper dict = new DictTemplateHelper();
        assertThat(dict.getLabel(null, "0")).isEqualTo("");
    }

    @Test
    @DisplayName("getLabel returns empty string when dictValue is null")
    void getLabel_nullDictValue_returnsEmpty() {
        DictTemplateHelper dict = new DictTemplateHelper();
        assertThat(dict.getLabel("sys_common_status", null)).isEqualTo("");
    }

    @Test
    @DisplayName("getLabel returns empty string when both args are null")
    void getLabel_bothNull_returnsEmpty() {
        DictTemplateHelper dict = new DictTemplateHelper();
        assertThat(dict.getLabel(null, null)).isEqualTo("");
    }

    // ---- PermissionTemplateHelper.hasPermi / lacksPermi guard clauses ----

    @Test
    @DisplayName("hasPermi returns false when permission is null")
    void hasPermi_null_returnsFalse() {
        PermissionTemplateHelper permission = new PermissionTemplateHelper();
        assertThat(permission.hasPermi(null)).isFalse();
    }

    @Test
    @DisplayName("hasPermi returns false when permission is empty")
    void hasPermi_empty_returnsFalse() {
        PermissionTemplateHelper permission = new PermissionTemplateHelper();
        assertThat(permission.hasPermi("")).isFalse();
    }

    @Test
    @DisplayName("lacksPermi returns true when permission is null (inverse of hasPermi)")
    void lacksPermi_null_returnsTrue() {
        PermissionTemplateHelper permission = new PermissionTemplateHelper();
        assertThat(permission.lacksPermi(null)).isTrue();
    }

    @Test
    @DisplayName("lacksPermi returns true when permission is empty (inverse of hasPermi)")
    void lacksPermi_empty_returnsTrue() {
        PermissionTemplateHelper permission = new PermissionTemplateHelper();
        assertThat(permission.lacksPermi("")).isTrue();
    }
}
