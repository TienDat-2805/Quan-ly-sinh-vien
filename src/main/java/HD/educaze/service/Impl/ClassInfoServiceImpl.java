package HD.educaze.service.Impl;

import HD.educaze.Wrapper.ClassInfoWrapper;
import HD.educaze.dao.IClassInfoDao;
import HD.educaze.model.ClassInfo;
import HD.educaze.service.ClassInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class           ClassInfoServiceImpl implements ClassInfoService {

    private static final Logger logger = LoggerFactory.getLogger(ClassInfoServiceImpl.class);

    @Autowired
    private IClassInfoDao classInfoDao;

    @Override
    public ClassInfo create(ClassInfoWrapper wrapper) {
        logger.info("Creating ClassInfo: {}", wrapper.getName());
        ClassInfo classInfo = new ClassInfo();
        mapWrapperToEntity(wrapper, classInfo);
        return classInfoDao.save(classInfo);
    }

    @Override
    public ClassInfo update(ClassInfoWrapper wrapper) {
        logger.info("Updating ClassInfo: {}", wrapper.getId());
        ClassInfo existingClassInfo = classInfoDao.getById(wrapper.getId());
        if (existingClassInfo == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "ClassInfo not found with id: " + wrapper.getId());
        }
        mapWrapperToEntity(wrapper, existingClassInfo);
        return classInfoDao.update(existingClassInfo);
    }

    @Override
    public void delete(String id) {
        logger.info("Deleting ClassInfo: {}", id);
        classInfoDao.delete(id);
    }

    @Override
    public ClassInfo getById(String id) {
        logger.info("Getting ClassInfo by id: {}", id);
        return classInfoDao.getById(id);
    }

    @Override
    public ClassInfo getByName(String name) {
        logger.info("Getting ClassInfo by name: {}", name);
        return classInfoDao.getByName(name);
    }

    @Override
    public List<ClassInfo> getAll() {
        logger.info("Getting all ClassInfo");
        return classInfoDao.getAll();
    }

    @Override
    public List<ClassInfo> getByDomain(String domain) {
        logger.info("Getting ClassInfo by domain: {}", domain);
        return classInfoDao.getByDomain(domain);
    }

    @Override
    public List<ClassInfo> search(String query, Integer lowerLimit, Integer upperLimit, String orderBy, String orderType) {
        logger.info("Searching ClassInfo with query: {}", query);
        if (lowerLimit == null) {
            lowerLimit = 0;
        }
        if (upperLimit == null) {
            upperLimit = 10;
        }
        return classInfoDao.search(query, lowerLimit, upperLimit, orderBy, orderType);
    }

    @Override
    public Integer getCount(String query) {
        logger.info("Getting ClassInfo count");
        return classInfoDao.getCount(query);
    }

    private void mapWrapperToEntity(ClassInfoWrapper wrapper, ClassInfo classInfo) {
        if (wrapper.getAppId() != null) {
            classInfo.setAppId(wrapper.getAppId());
        }
        if (wrapper.getName() != null) {
            classInfo.setName(wrapper.getName());
        }
        if (wrapper.getSoftwareType() != null) {
            classInfo.setSoftwareType(wrapper.getSoftwareType());
        }
        if (wrapper.getDomain() != null) {
            classInfo.setDomain(wrapper.getDomain());
        }
        if (wrapper.getTargetOperator() != null) {
            classInfo.setTargetOperator(wrapper.getTargetOperator());
        }
        if (wrapper.getReview() != null) {
            classInfo.setReview(wrapper.getReview());
        }
        classInfo.setReviewCount(wrapper.getReviewCount());
        classInfo.setInstallationCount(wrapper.getInstallationCount());
        if (wrapper.getIconPath() != null) {
            classInfo.setIconPath(wrapper.getIconPath());
        }
        if (wrapper.getTemplateDetailId() != null) {
            classInfo.setTemplateDetailId(wrapper.getTemplateDetailId());
        }
    }
}
