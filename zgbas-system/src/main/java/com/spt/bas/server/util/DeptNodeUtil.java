package com.spt.bas.server.util;

import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.bas.client.vo.DeptNode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DeptNodeUtil {
    private static final String DEPT_TYPE="dept";
    private static final String USER_TYPE="user";
    public static DeptNode getRoot(String name) {
        DeptNode root = new DeptNode();
        root.setId(0L);
        root.setName(name);
        root.setParentId(0L);
        root.setType(DEPT_TYPE);
        return root;
    }

    /** 机构树 */
    public static List<DeptNode> getDeptTree(List<SysDeptSdk> depts, Boolean initUser) {
        List<DeptNode> r = new ArrayList<>();
        DeptNode root = getRoot("All Depts");

        Map<Long, DeptNode> mapId2Vo = new LinkedHashMap<>();
        for (SysDeptSdk dept : depts) {
            DeptNode resultVo = new DeptNode();

            resultVo.setName(dept.getDeptName());
            resultVo.setId(dept.getDeptId());
            resultVo.setParentId(dept.getParentId());
            resultVo.setType(DEPT_TYPE);
            if (initUser && dept.getUsers()!=null) {
                // 生成人员节点
                for (SysUserSdk user : dept.getUsers()) {
                    DeptNode userNode = new DeptNode();
                    userNode.setName(user.getNickName());
                    userNode.setId(user.getUserId());

                    userNode.setType(USER_TYPE);

                    resultVo.getChildren().add(userNode);
                    userNode.setParentId(resultVo.getId());
                }
            }
            mapId2Vo.put(dept.getDeptId(), resultVo);
        }

        for (SysDeptSdk dept : depts) {
            DeptNode resultVo = mapId2Vo.get(dept.getDeptId());
            if (dept.getParentId() == null) {
                resultVo.setParentId(resultVo.getId());
                root.getChildren().add(resultVo);
            } else {
                DeptNode parentVo = mapId2Vo.get(dept.getParentId());
                parentVo.getChildren().add(resultVo);
            }
        }
        r.add(root);
        return r;
    }

}
