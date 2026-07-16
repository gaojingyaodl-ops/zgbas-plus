package com.spt.bas.client.cache;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.BooleanUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.spt.bas.client.constant.BasConstants;
import com.spt.bas.client.entity.BasContract;
import com.spt.bas.client.entity.BsCompanyOur;
import com.spt.bas.client.entity.BsDictData;
import com.spt.bas.client.remote.IBsCompanyOurClient;
import com.spt.tools.core.cache.LocalCacheManager;
import com.spt.tools.core.util.SpringContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class BsCompanyOurUtil {
    private static AtomicBoolean isInited = new AtomicBoolean(false);
    private static final Logger log = LoggerFactory.getLogger(BsCompanyOurUtil.class);
    private static LoadingCache<String, Map<String, BsCompanyOur>> companyOurCache;
    private static LoadingCache<String, Map<String, BsCompanyOur>> companyOurCdCache;

    private BsCompanyOurUtil() {
        if (!isInited.get()) {
            init();
        }
    }

    /**
     * 初始化：读入所有的我方企业到缓存
     *
     */
    public static void init() {
        log.info("---初始化我方企业信息");
        companyOurCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Map<String, BsCompanyOur>>() {
                    @Override
                    public Map<String, BsCompanyOur> load(String key) throws Exception {
                        IBsCompanyOurClient service = SpringContextHolder.getBean(IBsCompanyOurClient.class);
                        List<BsCompanyOur> entityList = service.findAll();
                        Map<String, BsCompanyOur> companyOurMap = new HashMap<>();
                        for (BsCompanyOur companyOur : entityList) {
                            if(BooleanUtil.isTrue(companyOur.getEnableFlg())){
                                companyOurMap.put(BasConstants.ZG_ENTERPRISE_ID + companyOur.getCompanyName(), companyOur);
                            }
                        }
                        return companyOurMap;

                    }
                });

        companyOurCdCache = CacheBuilder.newBuilder().refreshAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Map<String, BsCompanyOur>>() {
                    @Override
                    public Map<String, BsCompanyOur> load(String key) throws Exception {
                        IBsCompanyOurClient service = SpringContextHolder.getBean(IBsCompanyOurClient.class);
                        List<BsCompanyOur> entityList = service.findAll();
                        Map<String, BsCompanyOur> companyOurMap = new HashMap<>();
                        for (BsCompanyOur companyOur : entityList) {
                            if(BooleanUtil.isTrue(companyOur.getEnableFlg())){
                                companyOurMap.put(BasConstants.ZG_ENTERPRISE_ID + companyOur.getCompanyCd(), companyOur);
                            }
                        }
                        return companyOurMap;

                    }
                });
        LocalCacheManager.register(companyOurCache);
        LocalCacheManager.register(companyOurCdCache);
        isInited.set(true);
    }

    /**
     * 获取我方企业CD
     * @return
     */
    public static String getKey(Long enterpriseId ,String companyName) {

        try{
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCache.get("ALL");
            BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyName);
            if(Objects.isNull(bsCompanyOur)){
                return getKeyByCompanyCd(enterpriseId,companyName);
            } else {
                return bsCompanyOur.getCompanyCd();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    public static String getKeyByCompanyCd(Long enterpriseId ,String companyCd) {

        try{
            if (companyOurCdCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCdCache.get("ALL");
            BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyCd);
            if(Objects.isNull(bsCompanyOur)){
                return null;
            } else {
                return bsCompanyOur.getCompanyCd();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据我方企业CD获取我方企业简称
     * @param enterpriseId 企业账套ID
     * @param companyCd 我方企业CD
     * @return
     */
    public static String getCompanyAbbr(Long enterpriseId, String companyCd) {
        String companyAbbr = "";
        if (StringUtils.isBlank(companyCd)) {
            return companyAbbr;
        }
        BsCompanyOur companyOur = getBsCompanyOur(enterpriseId, companyCd);
        return companyOur.getCompanyAbbr();
    }

    /**
     * 获取我方企业名称
     * @param companyCd
     * @return
     */
    public static String getValue(Long enterpriseId ,String companyCd) {
        if (StringUtils.isBlank(companyCd)){
            return "";
        }
        return getValueInternal(enterpriseId, companyCd, false);
    }
    private static String getValueInternal(Long enterpriseId ,String companyCd, boolean ifEnglish) {
        try {
            if (companyOurCdCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCdCache.get("ALL");
            if (elementAll == null) {
                return null;
            } else {
                BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyCd);
                if (bsCompanyOur == null || Boolean.FALSE.equals(bsCompanyOur.getEnableFlg())) {
                    return getValueInternalByName(enterpriseId,companyCd,ifEnglish);
                } else {
                    if (!ifEnglish) {
                        return bsCompanyOur.getCompanyName();
                    } else {
                        return SpringContextHolder.getMessage(bsCompanyOur.getCompanyCd(), Locale.ENGLISH);
                    }
                }
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }
    private static String getValueInternalByName(Long enterpriseId ,String companyName, boolean ifEnglish) {
        try {
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCache.get("ALL");
            if (elementAll == null) {
                return null;
            } else {
                BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyName);
                if (bsCompanyOur == null || Boolean.FALSE.equals(bsCompanyOur.getEnableFlg())) {
                    return null;
                } else {
                    if (!ifEnglish) {
                        return bsCompanyOur.getCompanyName();
                    } else {
                        return SpringContextHolder.getMessage(bsCompanyOur.getCompanyCd(), Locale.ENGLISH);
                    }
                }
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取我方企业信息
     * @param companyName
     * @return
     */
    public static BsCompanyOur getBsCompanyOur(Long enterpriseId ,String companyName) {
        try{
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCache.get("ALL");
            BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyName);
            if(Objects.isNull(bsCompanyOur)){
                return getBsCompanyOurByCd(enterpriseId,companyName);
            } else {
                return bsCompanyOur;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new BsCompanyOur();
        }
    }
    public static BsCompanyOur getBsCompanyOurByCd(Long enterpriseId ,String companyCd) {
        try{
            if (companyOurCdCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCdCache.get("ALL");
            BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyCd);
            if(Objects.isNull(bsCompanyOur)){
                return new BsCompanyOur();
            } else {
                return bsCompanyOur;
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new BsCompanyOur();
        }
    }
    public static BsDictData getCompanyOurToBsDictData(Long enterpriseId ,String companyCd) {
        try {
            if (companyOurCdCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCdCache.get("ALL");
            if (elementAll == null) {
                return null;
            } else {
                BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyCd);
                if(bsCompanyOur == null){
                    return getCompanyOurByNameToBsDictData(enterpriseId,companyCd);
                } else {
                    BsDictData bsDictData = new BsDictData();
                    bsDictData.setDictCd(bsCompanyOur.getCompanyCd());
                    bsDictData.setDictName(bsCompanyOur.getCompanyName());
                    bsDictData.setRemark(bsCompanyOur.getCompanyAbbr());
                    return bsDictData;
                }
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
    public static BsDictData getCompanyOurByNameToBsDictData(Long enterpriseId ,String companyName) {
        try {
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> elementAll = companyOurCache.get("ALL");
            if (elementAll == null) {
                return null;
            } else {
                BsCompanyOur bsCompanyOur = elementAll.get(enterpriseId + companyName);
                if(bsCompanyOur == null){
                    return null;
                } else {
                    BsDictData bsDictData = new BsDictData();
                    bsDictData.setDictCd(bsCompanyOur.getCompanyCd());
                    bsDictData.setDictName(bsCompanyOur.getCompanyName());
                    bsDictData.setRemark(bsCompanyOur.getCompanyAbbr());
                    return bsDictData;
                }
            }
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
    /**
     * 获取我方企业列表
     *            类别键
     * @return
     * @throws ExecutionException
     */
    public static List<BsCompanyOur> getCompanyOurList() {
        List<BsCompanyOur> rtn = new ArrayList<>();
        try {
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> map = companyOurCache.get("ALL");
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    rtn.add(map.get(key));
                }
            }
            return rtn;
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return rtn;
        }

    }

    /**
     * 获取我方企业列表转化成字典
     *            类别键
     * @return
     * @throws ExecutionException
     */
    public static List<BsDictData> getCompanyOurToBsDictDataList() {
        List<BsCompanyOur> rtn = new ArrayList<>();
        List<BsDictData> dictDataList = new ArrayList<>();
        try {
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> map = companyOurCache.get("ALL");
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    rtn.add(map.get(key));
                }
            }

            if(CollectionUtil.isNotEmpty(rtn)){
                rtn.forEach(our->{
                    BsDictData dictData = new BsDictData();
                    dictData.setDictCd(our.getCompanyCd());
                    dictData.setDictName(our.getCompanyName());
                    dictData.setRemark(our.getCompanyAbbr());
                    dictDataList.add(dictData);
                });
            }
            return dictDataList;
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return dictDataList;
        }

    }

    /**
     * 获取我方且我司企业列表转化成字典
     *            类别键
     * @return
     * @throws ExecutionException
     */
    public static List<BsDictData> getCompanyOurFlagToBsDictDataList() {
        List<BsCompanyOur> rtn = new ArrayList<>();
        List<BsDictData> dictDataList = new ArrayList<>();
        try {
            if (companyOurCache == null){
                init();
            }
            Map<String, BsCompanyOur> map = companyOurCache.get("ALL");
            if (map != null) {
                Set<String> keySet = map.keySet();
                for (String key : keySet) {
                    rtn.add(map.get(key));
                }
            }

            if(CollectionUtil.isNotEmpty(rtn)){
                rtn.forEach(our->{
                    Boolean ourCompanyFlag = our.getOurCompanyFlag();
                    if (ourCompanyFlag) {
                        BsDictData dictData = new BsDictData();
                        dictData.setDictCd(our.getCompanyCd());
                        dictData.setDictName(our.getCompanyName());
                        dictData.setRemark(our.getCompanyAbbr());
                        dictDataList.add(dictData);
                    }
                });
            }
            return dictDataList;
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            return dictDataList;
        }

    }

}
