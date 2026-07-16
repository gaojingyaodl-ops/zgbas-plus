package com.spt.bas.web.util;

import com.google.common.collect.Lists;
import com.spt.auth.sdk.entity.SysAppSdk;
import com.spt.auth.sdk.entity.SysDeptSdk;
import com.spt.auth.sdk.entity.SysMenuSdk;
import com.spt.auth.sdk.entity.SysUserSdk;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasManual;
import com.spt.bas.client.entity.BsDictData;
import com.spt.pm.entity.PmProcess;
import com.spt.tools.core.collection.CollectionUtil;
import com.spt.tools.data.easyui.EasyTreeNode;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: gaojy
 * @create 2022/7/18 13:51
 * @version: 1.0
 * @description:
 */
public class EasyTreeUtil2 {
    private static final String PARENT_ID = "parentId";
    private static final String ENABLE_FLG = "enableFlg";

    public static EasyTreeNode getRoot(String name) {
        EasyTreeNode root = new EasyTreeNode();
        root.setId("0");
        root.setText(name);
        root.setState(EasyTreeNode.STATE_OPEN);
        return root;
    }

    /**
     * 菜单树
     */
    public static EasyTreeNode getMenuTree(List<SysMenuSdk> menus) {
        return getCheckMenuTree(menus, null);
    }

    /**
     * 资源权限树
     */
    //public static EasyTreeNode gePermCheckedTree(List<SysPermission> permissions, List<SysPermission> lstSelect) {
    //    EasyTreeNode root = getRoot("All Permissions");
    //    for (SysPermission permission : permissions) {
    //        EasyTreeNode resultVo = new EasyTreeNode();
    //        resultVo.setText(permission.getPermName() + "(" + permission.getPermCd() + ")");
    //        resultVo.setId(String.valueOf(permission.getId()));
    //        boolean isContain = CollectionUtil.contain(lstSelect, "id", permission.getId());
    //        resultVo.setChecked(isContain);
    //        resultVo.setNodeType("permission");
    //        root.getChildren().add(resultVo);
    //    }
    //    return root;
    //}
    //
    ///**
    // * 权限角色树
    // */
    //public static EasyTreeNode getPermRoleTree(List<SysRole> roles, List<SysRole> lstSelect) {
    //    EasyTreeNode root = getRoot("All Roles");
    //    for (SysRole role : roles) {
    //        EasyTreeNode resultVo = new EasyTreeNode();
    //        resultVo.setText(role.getRoleName() + "(" + role.getRoleCd() + ")");
    //        resultVo.setId(String.valueOf(role.getId()));
    //        boolean isContain = CollectionUtil.contain(lstSelect, "id", role.getId());
    //        resultVo.setChecked(isContain);
    //        resultVo.setNodeType("role");
    //        root.getChildren().add(resultVo);
    //    }
    //    return root;
    //}
    //
    ///**
    // * 权限资源树
    // */
    //public static EasyTreeNode getPermResourceTree(List<SysResource> resources, List<SysResource> lstSelect) {
    //    EasyTreeNode root = getRoot("All Resources");
    //    for (SysResource resource : resources) {
    //        EasyTreeNode resultVo = new EasyTreeNode();
    //        resultVo.setText(resource.getUrl());
    //        resultVo.setId(String.valueOf(resource.getId()));
    //        boolean isContain = CollectionUtil.contain(lstSelect, "id", resource.getId());
    //        resultVo.setChecked(isContain);
    //        resultVo.setNodeType("resource");
    //        root.getChildren().add(resultVo);
    //    }
    //    return root;
    //}

    private static EasyTreeNode createMeneNode(SysMenuSdk menu, List<SysMenuSdk> lstSelect) {
        EasyTreeNode resultVo = new EasyTreeNode();
        resultVo.setText(menu.getMenuName());
        resultVo.setState(EasyTreeNode.STATE_OPEN);
        resultVo.setId(String.valueOf(menu.getMenuId()));
        resultVo.addAttr("url", StringUtils.isBlank(menu.getComponent()) ? "" : menu.getComponent());
        resultVo.addAttr("isRefresh", "");
        resultVo.addAttr("target", "");
        resultVo.setNodeType("menu");
        resultVo.addAttr("appId", menu.getAppId());
        resultVo.setIconCls(menu.getIcon());
        resultVo.addAttr(PARENT_ID, menu.getParent() == null ? "" : menu.getParentId());
        if (lstSelect != null) {
            boolean isContain = CollectionUtil.contain(lstSelect, "id", menu.getMenuId());
            resultVo.setChecked(isContain);
        }
        return resultVo;
    }

    public static List<EasyTreeNode> createMeneNode(List<PmProcess> list, String parentId) {
        List<EasyTreeNode> lstChildren = new ArrayList<>();
        list.forEach(p -> {
            lstChildren.add(createMeneNode(p, parentId));
        });
        return lstChildren;
    }

    private static EasyTreeNode createMeneNode(PmProcess menu, String parentId) {
        EasyTreeNode resultVo = new EasyTreeNode();
        resultVo.setText(menu.getProcessName());
        resultVo.setState(EasyTreeNode.STATE_OPEN);
        resultVo.setId(String.valueOf(menu.getId()));
        String url = "/pm/approve/newFlow/" + menu.getId();
        resultVo.addAttr("url", url);
        resultVo.addAttr("isRefresh", "0");
        resultVo.addAttr("target", "");
        resultVo.setNodeType("menu");
        resultVo.addAttr(PARENT_ID, parentId);
        return resultVo;
    }

    private static EasyTreeNode createAppNode(SysAppSdk app) {
        EasyTreeNode resultVo = new EasyTreeNode();
        resultVo.setText(app.getAppName());
        resultVo.setState(EasyTreeNode.STATE_OPEN);
        resultVo.setId("app" + app.getId());
        resultVo.addAttr("appCode", app.getAppCode());
        resultVo.setNodeType("app");
        return resultVo;
    }

    private static void initMapMenus(List<SysMenuSdk> lstAll, SysMenuSdk menu) {
        if (!CollectionUtil.contain(lstAll, "menuId", menu.getMenuId())) {
            lstAll.add(menu);
            if (menu.getParent() != null) {
                initMapMenus(lstAll, menu.getParent());
            }
        }
    }

    /**
     * 权限菜单树
     */
    public static EasyTreeNode getCheckMenuTree(List<SysMenuSdk> menus, List<SysMenuSdk> lstSelect) {
        EasyTreeNode root = getRoot("All Menus");
        List<SysMenuSdk> lstAll = new ArrayList<>();
        for (SysMenuSdk menu : menus) {
            initMapMenus(lstAll, menu);
        }
        CollectionUtil.sortList(lstAll, "orderNum");
        Map<Long, EasyTreeNode> mapId2Vo = new LinkedHashMap<>();
        Map<Long, EasyTreeNode> mapId2AppNode = new LinkedHashMap<>();
        Map<Long, EasyTreeNode> mapAppId2AppNode = new LinkedHashMap<>();
        for (SysMenuSdk menu : lstAll) {
            EasyTreeNode resultVo = createMeneNode(menu, lstSelect);
            mapId2Vo.put(menu.getMenuId(), resultVo);
            if (!mapId2AppNode.containsKey(menu.getMenuId()) && menu.getParent() == null) {
                EasyTreeNode appNode;
                if (!mapAppId2AppNode.containsKey(menu.getAppId())) {
                    appNode = createAppNode(menu.getApp());
                    root.getChildren().add(appNode);
                    mapAppId2AppNode.put(menu.getApp().getId(), appNode);
                } else {
                    appNode = mapAppId2AppNode.get(menu.getApp().getId());
                }
                mapId2AppNode.put(menu.getMenuId(), appNode);
            }
        }

        for (SysMenuSdk menu : lstAll) {
            EasyTreeNode resultVo = mapId2Vo.get(menu.getMenuId());
            if (menu.getParent() == null) {
                EasyTreeNode appNode = mapId2AppNode.get(menu.getMenuId());
                appNode.getChildren().add(resultVo);
            } else {
                EasyTreeNode parentVo = mapId2Vo.get(menu.getParent().getMenuId());
                if (parentVo != null) {
                    parentVo.getChildren().add(resultVo);
                    parentVo.setState(EasyTreeNode.STATE_CLOSED);
                }
            }
        }
        for (EasyTreeNode node : root.getChildren()) {
            node.setState(EasyTreeNode.STATE_OPEN);
        }
        return root;
    }

    /**
     * 机构树
     */
    public static EasyTreeNode getDeptTree(List<SysDeptSdk> depts) {
        return getDeptTree(depts, false, false, null);
    }

    public static EasyTreeNode getDeptTree(List<SysDeptSdk> depts, boolean initUser) {
        return getDeptTree(depts, initUser, false, null);
    }

    public static EasyTreeNode getDeptTree(List<SysDeptSdk> depts, boolean initUser, boolean onlyUserId) {
        return getDeptTree(depts, initUser, onlyUserId, null);
    }

    public static EasyTreeNode getDeptTree2(List<SysDeptSdk> depts, boolean initUser) {
        return getDeptTree2(depts, initUser, false, null);
    }

    /**
     * 机构人员树
     */
    public static EasyTreeNode getDeptTree(List<SysDeptSdk> depts, boolean initUser, boolean onlyUserId, List<SysUserSdk> lstSelect) {
        EasyTreeNode root = getRoot("All Depts");

        Map<Long, EasyTreeNode> mapId2Vo = new LinkedHashMap<>();
        for (SysDeptSdk dept : depts) {
            EasyTreeNode resultVo = new EasyTreeNode();
            resultVo.setText(dept.getDeptName());
            resultVo.setState(EasyTreeNode.STATE_OPEN);
            if (!onlyUserId) {
                resultVo.setId(String.valueOf(dept.getDeptId()));
            } else {
                resultVo.setId("dept" + dept.getDeptId());
            }
            resultVo.addAttr(PARENT_ID, dept.getParentId() == null ? "" : dept.getParentId());
            resultVo.setNodeType("dept");
            if (initUser && dept.getUsers() != null) {
                // 生成人员节点
                for (SysUserSdk user : dept.getUsers()) {
                    EasyTreeNode userNode = new EasyTreeNode();
                    userNode.setState(EasyTreeNode.STATE_OPEN);
                    userNode.setText(user.getNickName());
                    if (onlyUserId) {
                        userNode.setId(String.valueOf(user.getUserId()));
                    } else {
                        userNode.setId("user" + user.getUserId());
                    }
                    userNode.setNodeType("user");

                    if (lstSelect != null) {
                        boolean isContain = CollectionUtil.contain(lstSelect, "id", user.getUserId());
                        userNode.setChecked(isContain);
                    }
                    resultVo.getChildren().add(userNode);
                    userNode.addAttr(PARENT_ID, resultVo.getId());
                    userNode.addAttr(ENABLE_FLG, StringUtils.equals("0", user.getStatus()));
                }
            }
            mapId2Vo.put(dept.getDeptId(), resultVo);
        }

        for (SysDeptSdk dept : depts) {
            EasyTreeNode resultVo = mapId2Vo.get(dept.getDeptId());
            if (dept.getParent() == null) {
                resultVo.addAttr(PARENT_ID, resultVo.getId());
                root.getChildren().add(resultVo);
            } else {
                EasyTreeNode parentVo = mapId2Vo.get(dept.getParent().getDeptId());
                if (parentVo != null) {
                    parentVo.getChildren().add(resultVo);
                    parentVo.setState(EasyTreeNode.STATE_OPEN);
                }
            }
        }
        for (EasyTreeNode node : root.getChildren()) {
            node.setState(EasyTreeNode.STATE_OPEN);
        }
        return root;
    }
    public static EasyTreeNode getMattersTree(List<BsDictData> dictDatas, boolean initUser) {
        return getMattersTree(dictDatas, initUser, false, null);
    }
    /**
     * 事项类型树
     */
    public static EasyTreeNode getMattersTree(List<BsDictData> dictDatas, boolean initUser, boolean onlyUserId, List<SysUserSdk> lstSelect) {
        EasyTreeNode root = getRoot("All matters");

        Map<String, EasyTreeNode> mapId2Vo = new LinkedHashMap<>();
        Map<String, Integer> mapRemarkGroup = new LinkedHashMap<>();
        Integer id = 1;
        for (BsDictData dictData : dictDatas) {
            Integer integer = mapRemarkGroup.get(dictData.getRemark());
            if(integer == null) {
                EasyTreeNode resultVo = new EasyTreeNode();
                resultVo.setText(dictData.getRemark());
                resultVo.setState(EasyTreeNode.STATE_OPEN);
                resultVo.setId(String.valueOf(id));
                resultVo.setNodeType("one");
                mapId2Vo.put("one$" + String.valueOf(id), resultVo);

                mapRemarkGroup.put(dictData.getRemark(),id);
                root.getChildren().add(resultVo);
                id++;
            }
        }
        for (BsDictData dictData : dictDatas) {
            EasyTreeNode resultVo = new EasyTreeNode();
            resultVo.setText(dictData.getDictName());
            resultVo.setState(EasyTreeNode.STATE_OPEN);
            resultVo.setId(dictData.getDictCd());
            resultVo.setNodeType("two");

            Integer groupId = mapRemarkGroup.get(dictData.getRemark());
            if(groupId != null){
                EasyTreeNode easyTreeNode = mapId2Vo.get("one$" + String.valueOf(groupId));
                easyTreeNode.getChildren().add(resultVo);
            }
        }
        for (EasyTreeNode node : root.getChildren()) {
            node.setState(EasyTreeNode.STATE_OPEN);
        }
        return root;
    }

    public static EasyTreeNode getDeptTree2(List<SysDeptSdk> depts, boolean initUser, boolean onlyUserId, List<SysUserSdk> lstSelect) {
        EasyTreeNode root = getRoot("All Depts");

        Map<Long, EasyTreeNode> mapId2Vo = new LinkedHashMap<>();
        for (SysDeptSdk dept : depts) {
            EasyTreeNode resultVo = new EasyTreeNode();
            resultVo.setText(dept.getDeptName());
            resultVo.setState(EasyTreeNode.STATE_OPEN);
            if (!onlyUserId) {
                resultVo.setId(String.valueOf(dept.getDeptId()));
            } else {
                resultVo.setId("dept" + dept.getDeptId());
            }
            resultVo.addAttr(PARENT_ID, dept.getParentId() == null ? "" : dept.getParentId());
            resultVo.setNodeType("dept");
            if (initUser) {
                // 生成人员节点
                for (SysUserSdk user : dept.getUsers()) {
                    if (StringUtils.equals("0", user.getDelFlag()) && StringUtils.equals("0", user.getStatus())) {
                        EasyTreeNode userNode = new EasyTreeNode();
                        userNode.setText(user.getNickName());
                        if (onlyUserId) {
                            userNode.setId(String.valueOf(user.getUserId()));
                        } else {
                            userNode.setId("user" + user.getUserId());
                        }
                        userNode.setNodeType("user");

                        if (lstSelect != null) {
                            boolean isContain = CollectionUtil.contain(lstSelect, "id", user.getUserId());
                            userNode.setChecked(isContain);
                        }
                        resultVo.getChildren().add(userNode);
                        userNode.addAttr(PARENT_ID, resultVo.getId());
                        userNode.addAttr(ENABLE_FLG, true);
                    }

                }
            }
            mapId2Vo.put(dept.getDeptId(), resultVo);
        }

        for (SysDeptSdk dept : depts) {
            EasyTreeNode resultVo = mapId2Vo.get(dept.getDeptId());
            if (dept.getParent() == null) {
                resultVo.addAttr(PARENT_ID, resultVo.getId());
                root.getChildren().add(resultVo);
            } else {
                EasyTreeNode parentVo = mapId2Vo.get(dept.getParent().getDeptId());
                if (parentVo != null) {
                    parentVo.getChildren().add(resultVo);
                    parentVo.setState(EasyTreeNode.STATE_CLOSED);
                }
            }
        }
        for (EasyTreeNode node : root.getChildren()) {
            node.setState(EasyTreeNode.STATE_OPEN);
        }
        return root;
    }

    public static List<EasyTreeNode> getProcessTree(List<PmProcess> lstProcess) {

        EasyTreeNode node1 = createNode("P1", "供应链流程");
        EasyTreeNode node2 = createNode("P2", "工厂赊销");
        EasyTreeNode node3 = createNode("P3", "代理业务");
        EasyTreeNode node4 = createNode("P4", "质押业务");
        EasyTreeNode node5 = createNode("P5", "预售");
        EasyTreeNode node6 = createNode("P6", "财务流程");
        EasyTreeNode node7 = createNode("P7", "物管流程");
        EasyTreeNode node8 = createNode("P8", "印章使用申请");
        EasyTreeNode node9 = createNode("P9", "其他");
        lstProcess.forEach(p -> {
            EasyTreeNode nodeSub = createNode(String.valueOf(p.getId()), p.getProcessName());
            nodeSub.addAttr("processCode", p.getProcessCode());
            if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_ZYMY, p.getProcessCode())) {
                // 自营贸易
                node1.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_GCSX, p.getProcessCode())) {
                // 工厂赊销
                node2.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_DLYW, p.getProcessCode())) {
                // 代理业务
                node3.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_ZYYW, p.getProcessCode())) {
                // 质押业务
                node4.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_YS, p.getProcessCode())) {
                // 预售
                node5.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_CW, p.getProcessCode())) {
                // 财务
                node6.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_CC, p.getProcessCode())) {
                // 仓储
                node7.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_SEAL_APPLY, p.getProcessCode())) {
                // 印章使用申请
                node8.getChildren().add(nodeSub);
            } else if (ArrayUtils.contains(BasConstants.PROCESS_GROUP_OTHER, p.getProcessCode())) {
                // 其他
                node9.getChildren().add(nodeSub);
            }
        });

        List<EasyTreeNode> list = Lists.newArrayList(node1, node2, node3, node4, node5, node6, node7, node8, node9);
        list.removeIf(node -> node.getChildren().isEmpty());
        return list;
    }

    public static EasyTreeNode createNode(String id, String name) {
        EasyTreeNode root = new EasyTreeNode();
        root.setId(id);
        root.setText(name);
        root.setState(EasyTreeNode.STATE_OPEN);
        return root;
    }

    /**
     * 用户手册树
     *
     * @param manuals
     * @return
     */
    public static EasyTreeNode getManualTree(List<BasManual> manuals) {
        EasyTreeNode root = getRoot("All Title");
        Map<Long, EasyTreeNode> mapId2Vo = new LinkedHashMap<>();
        for (BasManual manual : manuals) {
            EasyTreeNode resultVo = new EasyTreeNode();
            resultVo.setText(manual.getTitle());
            resultVo.setState(EasyTreeNode.STATE_OPEN);
            resultVo.setId(manual.getId() + "");
            resultVo.addAttr("url", manual.getUrl());
            mapId2Vo.put(manual.getId(), resultVo);
        }

        for (BasManual manual : manuals) {
            EasyTreeNode resultVo = mapId2Vo.get(manual.getId());
            if (manual.getParentId() == 0) {
                resultVo.addAttr("parentId", resultVo.getId());
                root.getChildren().add(resultVo);
            } else {
                EasyTreeNode parentVo = mapId2Vo.get(manual.getParentId());
                if (parentVo != null) {
                    parentVo.getChildren().add(resultVo);
                    parentVo.setState(EasyTreeNode.STATE_OPEN);
                }
            }
        }
        return root;
    }
}
